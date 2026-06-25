package com.linngdu664.transmutatoria.block.entity;

import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.item.component.RecipeConditions;
import com.linngdu664.transmutatoria.network.to_client.*;
import com.linngdu664.transmutatoria.recipe.CrucibleRecipeManager;
import com.linngdu664.transmutatoria.recipe.crucible.CrucibleRecipe;
import com.linngdu664.transmutatoria.util.*;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.transaction.SnapshotJournal;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class TransmutationCrucibleBlockEntity extends BlockEntity {
    private static final AABB SUCK_AABB = Block.column(16.0F, 5.0F, 16.0F).toAabbs().getFirst();
    private static final int WATER_PER_ESSENCE = 15;
    private static final int ESSENCE_INPUT_SLOT_BEGIN = 0;
    private static final int ESSENCE_OUTPUT_SLOT_BEGIN = 24;
    private static final int CATALYST_SLOT = 48;
    private static final int INPUT_SLOT = 49;
    private static final int OUTPUT_SLOT = 50;
    public static final int SLOT_COUNT = 51;
    public static final int RENDERER_SLOT_COUNT = 27;
    private static final int INVALID_RENDERER_SLOT = 127;

    // 源质输入 - 源质输出 - 催化剂 - 转化输入 - 转化输出
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int[] realSlotToRendererSlot = new int[SLOT_COUNT];
    private IntArrayList inputOrder = new IntArrayList();   // server only
    private int rendererSlotUsage;  // server only
    private int polarity;
    private int selectedSlot;
    private int processTimer;
    private int targetTimer;
    private int essenceInputPulseSlot = -1; // client only
    private long essenceInputPulseStartedAtMillis;  // client only
    private CrucibleRendererSlotParas[] rendererSlotParas0;  // client only
    private CrucibleRendererSlotParas[] rendererSlotParas1;  // client only

    private record ItemsSnapshot(ItemStack[] items, int[] realSlotToRendererSlot, int[] inputOrder, int rendererSlotUsage, int targetTimer) {}

    private final SnapshotJournal<ItemsSnapshot> itemsJournal = new SnapshotJournal<>() {
        @Override
        protected ItemsSnapshot createSnapshot() {
            ItemStack[] copy = new ItemStack[SLOT_COUNT];
            for (int i = 0; i < SLOT_COUNT; i++) {
                copy[i] = items.get(i).copy();
            }
            return new ItemsSnapshot(copy, Arrays.copyOf(realSlotToRendererSlot, realSlotToRendererSlot.length), inputOrder.toIntArray(), rendererSlotUsage, targetTimer);
        }

        @Override
        protected void revertToSnapshot(ItemsSnapshot snapshot) {
            ItemStack[] snapItems = snapshot.items;
            for (int i = 0; i < snapItems.length; i++) {
                items.set(i, snapItems[i]);
            }
            realSlotToRendererSlot = snapshot.realSlotToRendererSlot;
            inputOrder = new IntArrayList(snapshot.inputOrder());
            rendererSlotUsage = snapshot.rendererSlotUsage;
            targetTimer = snapshot.targetTimer;
        }

        @Override
        protected void onRootCommit(ItemsSnapshot snapshot) {
            // todo 疑问：onRootCommit 只在服务端被调用吗？
            if (level instanceof ServerLevel serverLevel) {
                ArrayList<ItemStackWithTwoSlots> changes = new ArrayList<>();
                ItemStack[] snapItems = snapshot.items;
                for (int i = 0; i < snapItems.length; i++) {
                    if (!ItemStack.isSameItemSameComponents(items.get(i), snapItems[i])) {
                        changes.add(new ItemStackWithTwoSlots(i, realSlotToRendererSlot[i], items.get(i)));
                    }
                }
                if (!changes.isEmpty()) {
                    PacketDistributor.sendToPlayersTrackingChunk(serverLevel, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), changes));
                }
                if (snapshot.targetTimer != targetTimer) {
                    PacketDistributor.sendToPlayersTrackingChunk(serverLevel, getChunkPos(), new CrucibleSetTargetTimerPayload(getBlockPos(), targetTimer));
                }
            }
            setChanged();
        }
    };

    private final FluidStacksResourceHandler waterHandler = new FluidStacksResourceHandler(1, 1000) {
        @Override
        protected void onContentsChanged(int index, FluidStack prev) {
            // 这里的服务端检查是必须的，因为客户端的 setWater 最后也会调用到这里
            if (level instanceof ServerLevel serverLevel) {
                int water = getAmountAsInt(0);
                PacketDistributor.sendToPlayersTrackingChunk(serverLevel, getChunkPos(), new CrucibleSetWaterPayload(getBlockPos(), water));
                if (targetTimer == 0 && !hasAnyOutput()) {
                    tryReact(null);
                } else if (targetTimer != 0 && water < getRequiredWater()) {
                    setAndSyncReset(false);
                }
            }
            setChanged();
        }

        @Override
        public boolean isValid(int index, FluidResource resource) {
            return resource.getFluid() == Fluids.WATER;
        }
    };

    private final ResourceHandler<ItemResource> upDownItemHandler = new ResourceHandler<>() {
        @Override
        public int size() {
            return 27;
        }

        @Override
        public ItemResource getResource(int index) {
            if (index <= 2) return ItemResource.of(items.get(CATALYST_SLOT + index));
            return ItemResource.of(items.get(index - 3 + ESSENCE_OUTPUT_SLOT_BEGIN));
        }

        @Override
        public long getAmountAsLong(int index) {
            if (index <= 2) return items.get(CATALYST_SLOT + index).getCount();
            return items.get(index - 3 + ESSENCE_OUTPUT_SLOT_BEGIN).getCount();
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return 1;
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (index == 0) return canAddCatalyst(resource.toStack());
            if (index == 1) return canAddInput(resource.toStack());
            return false;
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (amount <= 0) return 0;
            if (index == 0) {
                ItemStack itemStack = resource.toStack();
                if (canAddCatalyst(itemStack)) {
                    itemsJournal.updateSnapshots(transaction);
                    allocateRendererSlot(CATALYST_SLOT);
                    items.set(CATALYST_SLOT, itemStack);
                    return 1;
                }
                return 0;
            }
            if (index == 1) {
                ItemStack itemStack = resource.toStack();
                if (canAddInput(itemStack)) {
                    itemsJournal.updateSnapshots(transaction);
                    allocateRendererSlot(INPUT_SLOT);
                    items.set(INPUT_SLOT, itemStack);
                    tryReact(transaction);
                    return 1;
                }
                return 0;
            }
            return 0;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (index < 2) return 0;
            int slot = index == 2 ? OUTPUT_SLOT : index - 3 + ESSENCE_OUTPUT_SLOT_BEGIN;
            ItemStack stack = items.get(slot);
            if (!resource.matches(stack)) return 0;
            int toExtract = Math.min(amount, stack.getCount());
            itemsJournal.updateSnapshots(transaction);
            ItemStack result = ContainerHelper.removeItem(items, slot, toExtract);
            if (stack.isEmpty()) {
                releaseRendererSlot(slot);
            }
            return result.getCount();
        }
    };

    private final ResourceHandler<ItemResource> sideItemHandler = new ResourceHandler<>() {
        @Override
        public int size() {
            return 49;
        }

        @Override
        public ItemResource getResource(int index) {
            if (index == 48) return ItemResource.of(getOutput());
            return ItemResource.of(items.get(index));
        }

        @Override
        public long getAmountAsLong(int index) {
            if (index == 48) return getOutput().getCount();
            return items.get(index).getCount();
        }

        @Override
        public long getCapacityAsLong(int index, ItemResource resource) {
            return 1;
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            if (index < 24) return canAddEssence(index, resource.toStack());
            return false;
        }

        @Override
        public int insert(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (index < 24 && amount > 0) {
                ItemStack itemStack = resource.toStack();
                if (canAddEssence(index, itemStack)) {
                    itemsJournal.updateSnapshots(transaction);
                    allocateRendererSlot(index);
                    items.set(index, itemStack);
                    inputOrder.add(index);
                    tryReact(transaction);
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int extract(int index, ItemResource resource, int amount, TransactionContext transaction) {
            if (index < 24) return 0;
            int slot = index == 48 ? OUTPUT_SLOT : index;
            ItemStack stack = items.get(slot);
            if (!resource.matches(stack)) return 0;
            int toExtract = Math.min(amount, stack.getCount());
            itemsJournal.updateSnapshots(transaction);
            ItemStack result = ContainerHelper.removeItem(items, slot, toExtract);
            if (stack.isEmpty()) {
                releaseRendererSlot(slot);
            }
            return result.getCount();
        }
    };

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (!(blockEntity instanceof TransmutationCrucibleBlockEntity crucible)) {
            return;
        }
        if (level.isClientSide()) {
            if (crucible.targetTimer != 0) {
                // 转圈圈，带clamp的公转、自转、缩放
                // todo 客户端需要再来个计时器字段（反应结束后渐变）
                crucible.rendererSlotParas0 = crucible.rendererSlotParas1;
                if (crucible.processTimer < crucible.targetTimer) {
                    crucible.rendererSlotParas1 = new CrucibleRendererSlotParas[RENDERER_SLOT_COUNT];
                    float startupPercent = crucible.processTimer >= 10 ? 1f : (float) crucible.processTimer * 0.1f;
                    int i = 0;
                    for (; i < 9; i++) {
                        crucible.rendererSlotParas1[i] = crucible.rendererSlotParas0[i].tick(0.06f, 0.018f, startupPercent);
                    }
                    for (; i < 18; i++) {
                        crucible.rendererSlotParas1[i] = crucible.rendererSlotParas0[i].tick(0.05f, 0.02f, startupPercent);
                    }
                    for (; i < RENDERER_SLOT_COUNT; i++) {
                        crucible.rendererSlotParas1[i] = crucible.rendererSlotParas0[i].tick(0.04f, 0.022f, startupPercent);
                    }
                }
            }
            return;
        }
        if (crucible.targetTimer != 0) {
            // targetTimer != 0 说明正在运行
            if (crucible.processTimer >= crucible.targetTimer) {
                crucible.clearInputAndSetAllOutput();   // 反应结果
            } else {
                crucible.processTimer++;
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, crucible.getChunkPos(), new CrucibleSetProcessTimerPayload(pos, crucible.processTimer));
            }
            crucible.setChanged();
        } else {
            // 卷轴在炼金锅里过期
            ItemStack itemStack = crucible.getCatalyst();
            long nextExpire = itemStack.getOrDefault(InitDataComponents.NEXT_EXPIRE, Long.MAX_VALUE);
            int times = AbstractTransmutationScrollItem.checkAndSetExpire(level, itemStack);

            if (times > 0 || nextExpire != itemStack.getOrDefault(InitDataComponents.NEXT_EXPIRE, Long.MAX_VALUE)) {
                if (times > 0) {
                    AbstractTransmutationScrollItem.changeEssence(level, itemStack, times);
                }
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, crucible.getChunkPos(), new CrucibleSetItemPayload(pos, List.of(new ItemStackWithTwoSlots(CATALYST_SLOT, crucible.realSlotToRendererSlot[CATALYST_SLOT], itemStack))));
                crucible.setChanged();
            }
        }
    }

    public TransmutationCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(InitBlocks.TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY.get(), pos, state);
        rendererSlotParas0 = new CrucibleRendererSlotParas[RENDERER_SLOT_COUNT];
        rendererSlotParas1 = rendererSlotParas0;
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        if (level.isClientSide()) {
            RandomSource random = level.getRandom();
            float[] baseXZ = new float[]{-0.2f, 0.0f, 0.2f};
            float[] baseY = new float[]{0.35f, 0.4f, 0.45f};
            float[] baseR = new float[]{0.28f, 0.25f, 0.28f, 0.25f, 0.04f, 0.25f, 0.28f, 0.25f, 0.28f};
            for (int i = 0; i < RENDERER_SLOT_COUNT; i++) {
                rendererSlotParas0[i] = new CrucibleRendererSlotParas(
                        baseXZ[i % 3] - 0.075f + random.nextFloat() * 0.15f,
                        baseY[i / 9],
                        baseXZ[i / 3 % 3] - 0.075f + random.nextFloat() * 0.15f,
                        baseR[i % 9],
                        -0.4f + random.nextFloat() * 0.8f,
                        -Mth.PI + random.nextFloat() * Mth.TWO_PI
                );
            }
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items, true);
        waterHandler.serialize(output);
        output.putInt("Polarity", polarity);
        output.putInt("SelectedSlot", selectedSlot);
        output.putInt("ProcessTimer", processTimer);
        output.putInt("TargetTimer", targetTimer);
        output.putIntArray("InputOrder", inputOrder.toIntArray());
        output.putIntArray("RealSlotToRendererSlot", realSlotToRendererSlot);
        output.putInt("RendererSlotUsage", rendererSlotUsage);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, items);
        waterHandler.deserialize(input);
        polarity = input.getIntOr("Polarity", 0);
        selectedSlot = input.getIntOr("SelectedSlot", 0);
        processTimer = input.getIntOr("ProcessTimer", 0);
        targetTimer = input.getIntOr("TargetTimer", 0);
        inputOrder = new IntArrayList(input.getIntArray("InputOrder").orElse(new int[0]));
        realSlotToRendererSlot = input.getIntArray("RealSlotToRendererSlot").orElseGet(() -> {
            int[] arr = new int[SLOT_COUNT];
            Arrays.fill(arr, INVALID_RENDERER_SLOT);
            return arr;
        });
        rendererSlotUsage = input.getIntOr("RendererSlotUsage", 0);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        if (level != null) {
            Containers.dropContents(level, pos, items);
        }
    }

    // 区块加载时会自动触发全量更新同步
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    // 方块更新时的同步（粗粒度同步，细粒度用自定义网络包）
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public ResourceHandler<ItemResource> getUpDownItemHandler() {
        return upDownItemHandler;
    }

    public ResourceHandler<ItemResource> getSideItemHandler() {
        return sideItemHandler;
    }

    public FluidStacksResourceHandler getWaterHandler() {
        return waterHandler;
    }

    public void entityInside(Entity entity) {
        BlockPos pos = getBlockPos();
        if (entity instanceof ItemEntity itemEntity && entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(SUCK_AABB)) {
            if (tryAddCatalystFromItemEntity(itemEntity) || tryAddInputFromItemEntity(itemEntity) || tryAddEssenceFromItemEntity(itemEntity)) {
                setChanged();
            }
        }
    }

    private boolean tryAddCatalystFromItemEntity(ItemEntity entity) {
        if (!canAddCatalyst(entity.getItem())) {
            return false;
        }
        suckOneAndSync(entity, CATALYST_SLOT);
        return true;
    }

    private boolean tryAddInputFromItemEntity(ItemEntity entity) {
        ItemStack itemStack = entity.getItem();
        if (!canAddInput(itemStack)) {
            return false;
        }
        suckOneAndSync(entity, INPUT_SLOT);
        tryReact(null);
        return true;
    }

    private boolean tryAddEssenceFromItemEntity(ItemEntity entity) {
        ItemStack itemStack = entity.getItem();
        if (!canAddEssence(selectedSlot, itemStack)) {
            return false;
        }
        // 吸入源质金属并记录加入顺序
        suckOneAndSync(entity, selectedSlot);
        inputOrder.add(selectedSlot);
        // 自动指向下一个槽位
        setAndSyncSelectedSlot((selectedSlot + 1) % getRequiredEssenceCount());
        tryReact(null);
        return true;
    }

    private void suckOneAndSync(ItemEntity entity, int slot) {
        ItemStack itemStack = entity.getItem();
        ItemStack copy = itemStack.copyWithCount(1);
        items.set(slot, copy);
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithTwoSlots(slot, allocateRendererSlot(slot), copy))));
        if (itemStack.getCount() == 1) {
            entity.setItem(ItemStack.EMPTY);
            entity.discard();
        } else {
            itemStack.setCount(itemStack.getCount() - 1);
        }
        level.playSound(null, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5,
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 1.4F + 2.0F);
    }

    private void setAndCondSyncTargetTimer(int targetTimer, TransactionContext txContext) {
        this.targetTimer = targetTimer;
        if (txContext == null) {
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetTargetTimerPayload(getBlockPos(), targetTimer));
        }
    }

    private void setAndSyncSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetSelectedSlotPayload(getBlockPos(), selectedSlot));
    }

    private void setAndSyncReset(boolean clearInputOrder) {
        this.processTimer = 0;
        this.targetTimer = 0;
        this.selectedSlot = 0;
        if (clearInputOrder) {
            this.inputOrder.clear();
        }
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleResetPayload(getBlockPos(), polarity));
    }

    public void adjustPolarityWithPhilosophersStone() {
        if (polarity != 0) {
            polarity += polarity < 0 ? 1 : -1;
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetPolarityPayload(getBlockPos(), polarity));
            level.playSound(null, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5,
                    SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            setChanged();
        }
    }

    private ChunkPos getChunkPos() {
        return new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ()));
    }

    public boolean canAddCatalyst(ItemStack itemStack) {
        return targetTimer == 0 && !hasAnyOutput() && getCatalyst().isEmpty()
                && (itemStack.is(Items.ENDER_EYE) || itemStack.is(InitItems.TRANSMUTATION_CRYSTAL)
                || itemStack.is(InitItems.PHILOSOPHERS_STONE) || itemStack.getItem() instanceof EssenceMetalItem
                || (itemStack.getItem() instanceof AbstractTransmutationScrollItem && itemStack.has(InitDataComponents.ALCHEMY_SLOTS) && itemStack.has(InitDataComponents.RECIPE_CONDITIONS)));
    }

    public boolean canAddInput(ItemStack itemStack) {
        if (targetTimer != 0 || hasAnyOutput() || !getInput().isEmpty() || itemStack.isEmpty()) {
            return false;
        }
        ItemStack catalyst = getCatalyst();

        if (catalyst.is(Items.ENDER_EYE)) {
            // 嬗变分解：转化输入是嬗变结晶
            return itemStack.is(InitItems.TRANSMUTATION_CRYSTAL);
        }

        if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            // 炼金复制/炼金合成：转化输入从卷轴读取
            ItemContainerContents container = catalyst.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            return container.getSlots() > 0 && ItemStack.isSameItemSameComponents(itemStack, container.getStackInSlot(0));
        }

        if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            // 混沌分解：该物品需要可被炼金复制合成
            return CrucibleRecipeManager.findMatchRep(level, itemStack) != null;
        }

        // 其他的催化剂不接受转化输入
        return false;
    }

    public boolean canAddEssence(int slot, ItemStack itemStack) {
        if (targetTimer != 0 || hasAnyOutput() || !(itemStack.getItem() instanceof EssenceMetalItem) || slot < ESSENCE_INPUT_SLOT_BEGIN || !items.get(slot).isEmpty()) {
            return false;
        }
        ItemStack catalyst = getCatalyst();

        if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            // 炼金复制/炼金合成：输入符合且槽位不越界
            ItemContainerContents container = catalyst.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
            return container.getSlots() > 0 && ItemStack.isSameItemSameComponents(getInput(), container.getStackInSlot(0))
                    && !alchemySlots.isEmpty() && slot - ESSENCE_INPUT_SLOT_BEGIN < alchemySlots.size();
        }

        if (getCatalyst().is(InitItems.TRANSMUTATION_CRYSTAL)) {
            // 源质反应：槽位只能是 0 或 1
            return slot - ESSENCE_INPUT_SLOT_BEGIN < 2;
        }

        if (getCatalyst().getItem() instanceof EssenceMetalItem essenceMetalItem) {
            // 源质融合：槽位不越界
            return slot - ESSENCE_INPUT_SLOT_BEGIN < essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains().size();
        }

        // 其他的催化剂不接受源质金属输入
        return false;
    }

    private int getRequiredEssenceCount() {
        ItemStack catalyst = getCatalyst();
        if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            return catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of()).size();   // 炼金复制/炼金合成
        }
        if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            return 2;   // 源质反应
        }
        if (catalyst.getItem() instanceof EssenceMetalItem essenceMetalItem) {
            return essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains().size();  // 源质融合
        }
        return 0;
    }

    private void tryReact(TransactionContext txContext) {
        if (waterHandler.getAmountAsInt(0) < getRequiredWater()) {
            return;
        }
        ItemStack catalyst = getCatalyst();
        ItemStack input = getInput();
        if (catalyst.is(Items.ENDER_EYE)) {
            // 嬗变分解
            if (input.is(InitItems.TRANSMUTATION_CRYSTAL)) {
                setAndCondSyncTargetTimer(10, txContext);
            }
        } else if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            // 混沌分解
            CrucibleRecipe recipe = CrucibleRecipeManager.findMatchRep(level, input);
            if (recipe != null) {
                IntIntImmutablePair minMax = recipe.level().getMinMax(level, input);
                setAndCondSyncTargetTimer(5 * (minMax.leftInt() + minMax.rightInt()), txContext);
            }
        } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            // 源质反应：无输入 && 加了 2 个源质金属
            if (input.isEmpty() && inputOrder.size() == 2) {
                setAndCondSyncTargetTimer(20, txContext);
            }
        } else if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            // 炼金复制/炼金合成：输入正确 && 源质金属加够了
            List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
            ItemContainerContents container = catalyst.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            if (container.getSlots() > 0 && ItemStack.isSameItemSameComponents(input, container.getStackInSlot(0)) && inputOrder.size() == alchemySlots.size()) {
                setAndCondSyncTargetTimer(10 * alchemySlots.size(), txContext);
            }
        } else if (catalyst.getItem() instanceof EssenceMetalItem essenceMetalItem) {
            // 源质融合：无输入 && 源质金属加够了 && 覆盖所有克制
            if (input.isEmpty()) {
                Set<EssenceMetal> essenceRequired = essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains();
                int size = essenceRequired.size();
                if (inputOrder.size() == size) {
                    HashSet<EssenceMetal> essenceRequired1 = new HashSet<>(essenceRequired);
                    for (int i = ESSENCE_INPUT_SLOT_BEGIN, upper = ESSENCE_OUTPUT_SLOT_BEGIN + size; i < upper; i++) {
                        if (items.get(i).getItem() instanceof EssenceMetalItem inputEssenceMetalItem) {
                            essenceRequired1.remove(inputEssenceMetalItem.getEssenceMetal());
                        }
                    }
                    if (essenceRequired1.isEmpty()) {
                        setAndCondSyncTargetTimer(20, txContext);
                    }
                }
            }
        }
    }

    public int getRequiredWater() {
        ItemStack catalyst = getCatalyst();
        if (catalyst.is(Items.ENDER_EYE)) {
            return WATER_PER_ESSENCE;
        }
        if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            ItemStack input = getInput();
            CrucibleRecipe recipe = CrucibleRecipeManager.findMatchRep(level, input);
            if (recipe != null) {
                IntIntImmutablePair minMax = recipe.level().getMinMax(level, input);
                return WATER_PER_ESSENCE * 5 * (minMax.leftInt() + minMax.rightInt());
            }
            return WATER_PER_ESSENCE;
        }
        if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            return WATER_PER_ESSENCE * 2;
        }
        if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
            return alchemySlots.isEmpty() ? WATER_PER_ESSENCE : WATER_PER_ESSENCE * alchemySlots.size();
        }
        if (catalyst.getItem() instanceof EssenceMetalItem essenceMetalItem) {
            return WATER_PER_ESSENCE * essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains().size();
        }
        return WATER_PER_ESSENCE;
    }

    private void clearInputAndSetAllOutput() {
        int requiredWater = getRequiredWater();

        ArrayList<ItemStackWithTwoSlots> itemStackUpdate = new ArrayList<>();
        ItemStack catalyst = getCatalyst();
        if (catalyst.is(Items.ENDER_EYE)) {
            handleEnderEyeReaction(itemStackUpdate);   // 嬗变分解的反应结果
        } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            handleCrystalReaction(itemStackUpdate);    // 源质反应的反应结果
        } else if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            handlePhilosophersStoneReaction(itemStackUpdate); // 混沌分解的反应结果
        } else if (catalyst.getItem() instanceof EssenceMetalItem) {
            handleEssenceMetalReaction(itemStackUpdate);    // 源质融合的反应结果
        } else if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            handleScrollReaction(itemStackUpdate);   // 炼金复制/炼金转化的反应结果
        }

        // 检查锅的极性，超过了就爆掉
        if (polarity > 50) {
            level.setBlock(getBlockPos(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        } else if (polarity < -50) {
            level.setBlock(getBlockPos(), InitBlocks.ALCHEMICAL_DROSS_BLOCK.get().defaultBlockState(), 3);
        } else {
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), itemStackUpdate));
            if (hasAnyOutput()) {
                level.playSound(null, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5,
                        SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 0.5F, 1.0F);
            }
            // 先重置 timer，否则 waterHandler 可能会多发一个包，虽然无伤大雅
            setAndSyncReset(true);
            // 消耗水
            try (var tx = Transaction.openRoot()) {
                waterHandler.extract(0, FluidResource.of(Fluids.WATER), requiredWater, tx);
                tx.commit();
            }
        }
    }

    private void handleEnderEyeReaction(ArrayList<ItemStackWithTwoSlots> updates) {
        if (getInput().is(InitItems.TRANSMUTATION_CRYSTAL)) {
            // 清空输入
            clearItemAndRecordChange(INPUT_SLOT, updates);
            // 设置输出
            int rendererSlot = allocateRendererSlot(OUTPUT_SLOT);
            ItemStack itemStack;
            RandomSource randomSource = level.getRandom();
            // todo 概率要在配置文件里调吗？
            if (randomSource.nextFloat() <= 0.01f) {
                itemStack = InitItems.PANDEMONIUM.toStack();
            } else {
                itemStack = InitItems.ESSENCE_METAL_ITEMS[randomSource.nextInt(InitItems.ESSENCE_METAL_ITEMS.length - 1)].toStack();
            }
            updates.add(new ItemStackWithTwoSlots(OUTPUT_SLOT, rendererSlot, itemStack));
        }
    }

    private void handleCrystalReaction(ArrayList<ItemStackWithTwoSlots> updates) {
        if (items.get(ESSENCE_INPUT_SLOT_BEGIN).getItem() instanceof EssenceMetalItem essence1 && items.get(ESSENCE_INPUT_SLOT_BEGIN + 1).getItem() instanceof EssenceMetalItem essence2) {
            EssenceMetal.Relation relation = essence1.getEssenceMetal().getRelationTo(essence2.getEssenceMetal());
            ItemStack essence1Stack = essence1.change(relation.self);
            ItemStack essence2Stack = essence2.change(relation.other);
            int rendererSlot1 = realSlotToRendererSlot[ESSENCE_INPUT_SLOT_BEGIN];
            int rendererSlot2 = realSlotToRendererSlot[ESSENCE_INPUT_SLOT_BEGIN + 1];

            realSlotToRendererSlot[ESSENCE_INPUT_SLOT_BEGIN] = INVALID_RENDERER_SLOT;
            items.set(ESSENCE_INPUT_SLOT_BEGIN, ItemStack.EMPTY);
            updates.add(new ItemStackWithTwoSlots(ESSENCE_INPUT_SLOT_BEGIN, INVALID_RENDERER_SLOT, ItemStack.EMPTY));
            realSlotToRendererSlot[ESSENCE_OUTPUT_SLOT_BEGIN] = rendererSlot1;
            items.set(ESSENCE_OUTPUT_SLOT_BEGIN, essence1Stack);
            updates.add(new ItemStackWithTwoSlots(ESSENCE_OUTPUT_SLOT_BEGIN, rendererSlot1, essence1Stack));

            realSlotToRendererSlot[ESSENCE_INPUT_SLOT_BEGIN + 1] = INVALID_RENDERER_SLOT;
            items.set(ESSENCE_INPUT_SLOT_BEGIN + 1, ItemStack.EMPTY);
            updates.add(new ItemStackWithTwoSlots(ESSENCE_INPUT_SLOT_BEGIN + 1, INVALID_RENDERER_SLOT, ItemStack.EMPTY));
            realSlotToRendererSlot[ESSENCE_OUTPUT_SLOT_BEGIN + 1] = rendererSlot2;
            items.set(ESSENCE_OUTPUT_SLOT_BEGIN + 1, essence2Stack);
            updates.add(new ItemStackWithTwoSlots(ESSENCE_OUTPUT_SLOT_BEGIN + 1, rendererSlot2, essence2Stack));

            polarity -= relation.self + relation.other;
        }
    }

    private void handlePhilosophersStoneReaction(ArrayList<ItemStackWithTwoSlots> updates) {
        CrucibleRecipe recipe = CrucibleRecipeManager.findMatchRep(level, getInput());
        if (recipe != null) {
            RandomSource randomSource = level.getRandom();
            IntIntImmutablePair minMax = recipe.level().getMinMax(level, getInput());

            // 随机选取槽位产出（升序）
            int essenceCnt = Math.min(randomSource.nextInt(minMax.leftInt(), minMax.rightInt() + 1), CATALYST_SLOT - ESSENCE_OUTPUT_SLOT_BEGIN);
            int[] outputSlots = new int[essenceCnt];
            int idx = 0;
            int remaining = essenceCnt;
            for (int i = 0; i < 24 && remaining > 0; i++) {
                // 当前剩余数字个数为 n - i
                if (randomSource.nextInt(24 - i) < remaining) {
                    outputSlots[idx++] = i + ESSENCE_OUTPUT_SLOT_BEGIN;
                    remaining--;
                }
            }

            // 清空输入
            clearItemAndRecordChange(INPUT_SLOT, updates);

            // 设置输出（高槽位优先选低层渲染槽位）
            int len = InitItems.ESSENCE_METAL_ITEMS.length;
            for (int i = essenceCnt - 1; i >= 0; i--) {
                ItemStack itemStack = InitItems.ESSENCE_METAL_ITEMS[randomSource.nextInt(len)].toStack();
                int slot = outputSlots[i];
                items.set(slot, itemStack);
                updates.add(new ItemStackWithTwoSlots(slot, allocateRendererSlot(slot), itemStack));
            }
        }
    }

    private void handleEssenceMetalReaction(ArrayList<ItemStackWithTwoSlots> updates) {
        ItemStack outStack = getCatalyst().copy();
        clearInputEssencesAndRecordChange(updates);
        items.set(OUTPUT_SLOT, outStack);
        updates.add(new ItemStackWithTwoSlots(OUTPUT_SLOT, allocateRendererSlot(OUTPUT_SLOT), outStack));
    }

    private void handleScrollReaction(ArrayList<ItemStackWithTwoSlots> updates) {
        ItemStack catalyst = getCatalyst();
        List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
        if (!alchemySlots.isEmpty() && inputOrder.size() == alchemySlots.size()) {
            // 反应前预先做的事
            boolean[] inhibitionStates = new boolean[alchemySlots.size()];
            Int2IntOpenHashMap posToOutputSlot = new Int2IntOpenHashMap();
            ArrayList<Runnable> deferredTasks = new ArrayList<>();
            int i = 0;
            for (AbstractAlchemySlot slot : alchemySlots) {
                posToOutputSlot.put(slot.getPackedXY(), i);
                i++;
            }
            // 反应
            int annihilationCnt = 0;
            int entropy = catalyst.getOrDefault(InitDataComponents.ENTROPY, 0);
            // 避免装箱拆箱
            for (int j = 0, size = inputOrder.size(); j < size; j++) {
                int slot = inputOrder.getInt(j);
                AlchemyReactResult result = alchemySlots.get(slot).react(
                        catalyst,
                        items.get(slot),
                        items.subList(ESSENCE_OUTPUT_SLOT_BEGIN, CATALYST_SLOT),
                        inhibitionStates,
                        posToOutputSlot,
                        deferredTasks
                );
                polarity += result.getPolarityIncrease();
                entropy += result.getEntropyIncrease();
                if (result.isTriggerDamage()) {
                    if (catalyst.isDamageableItem() && catalyst.getDamageValue() < catalyst.getMaxDamage()) {
                        catalyst.setDamageValue(catalyst.getDamageValue() + 1 + entropy / 8);
                    }
                    annihilationCnt++;
                }
            }
            for (Runnable deferredTask : deferredTasks) {
                deferredTask.run();
            }

            if (catalyst.isBroken()) {
                releaseRendererSlot(CATALYST_SLOT); // 如果卷轴爆了，释放催化剂槽
                items.set(CATALYST_SLOT, ItemStack.EMPTY);
            } else {
                catalyst.set(InitDataComponents.ALCHEMY_SLOTS, alchemySlots);
                catalyst.set(InitDataComponents.ENTROPY, entropy);
            }

            clearInputEssencesAndRecordChange(updates); // 删掉所有的输入源质

            // 如果全湮灭且极性满足条件则消耗输入产出输出
            if (annihilationCnt == alchemySlots.size()) {
                RecipeConditions conditions = catalyst.getOrDefault(InitDataComponents.RECIPE_CONDITIONS, RecipeConditions.DEFAULT);
                ItemContainerContents container = catalyst.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                if (polarity >= conditions.minPolarity() && polarity <= conditions.maxPolarity() && container.getSlots() >= 2 && ItemStack.isSameItemSameComponents(getInput(), container.getStackInSlot(0))) {
                    if (conditions.oneTime()) {
                        // 额外判定一次性配方并释放渲染槽
                        releaseRendererSlot(CATALYST_SLOT);
                        items.set(CATALYST_SLOT, ItemStack.EMPTY);
                    }
                    clearItemAndRecordChange(INPUT_SLOT, updates);  // 释放输入槽
                    // 输出槽位最高，先设置输出
                    ItemStack outStack = container.getStackInSlot(1);
                    items.set(OUTPUT_SLOT, outStack);
                    updates.add(new ItemStackWithTwoSlots(OUTPUT_SLOT, allocateRendererSlot(OUTPUT_SLOT), outStack));
                }
            }

            updates.add(new ItemStackWithTwoSlots(CATALYST_SLOT, realSlotToRendererSlot[CATALYST_SLOT], items.get(CATALYST_SLOT)));
            for (int j = ESSENCE_OUTPUT_SLOT_BEGIN + alchemySlots.size() - 1; j >= ESSENCE_OUTPUT_SLOT_BEGIN; j--) {
                ItemStack itemStack = items.get(j);
                if (!itemStack.isEmpty()) {
                    updates.add(new ItemStackWithTwoSlots(j, allocateRendererSlot(j), itemStack));
                }
            }
        }
    }

    private void clearInputEssencesAndRecordChange(List<ItemStackWithTwoSlots> updates) {
        for (int i = ESSENCE_INPUT_SLOT_BEGIN; i < ESSENCE_OUTPUT_SLOT_BEGIN; i++) {
            if (!items.get(i).isEmpty()) {
                clearItemAndRecordChange(i, updates);
            }
        }
    }

    private int allocateRendererSlot(int realSlot) {
        int[] availableSlots = new int[9];
        for (int layer = 0; layer < 3; layer++) {
            int base = layer * 9;
            int cnt = 0;
            for (int i = base; i < base + 9; i++) {
                if ((rendererSlotUsage & (1 << i)) == 0) {
                    availableSlots[cnt++] = i;
                }
            }
            if (cnt > 0) {
                int slot = availableSlots[level.getRandom().nextInt(cnt)];
                rendererSlotUsage |= 1 << slot;
                realSlotToRendererSlot[realSlot] = slot;
                return slot;
            }
        }
        // 分配失败兜底（除非用户乱调方块数据，通常不会走到这里）
        realSlotToRendererSlot[realSlot] = INVALID_RENDERER_SLOT;
        return INVALID_RENDERER_SLOT;
    }

    private void releaseRendererSlot(int realSlot) {
        int slot = realSlotToRendererSlot[realSlot];
        rendererSlotUsage &= ~(1 << slot);
        realSlotToRendererSlot[realSlot] = INVALID_RENDERER_SLOT;
    }

    private List<ItemStackWithTwoSlots> clearItemAndRecordChange(int slot, List<ItemStackWithTwoSlots> updates) {
        releaseRendererSlot(slot);
        items.set(slot, ItemStack.EMPTY);
        updates.add(new ItemStackWithTwoSlots(slot, INVALID_RENDERER_SLOT, ItemStack.EMPTY));
        return updates;
    }

    /**
     * 外部禁止修改 ItemStack，否则未定义行为
     * @return 催化剂
     */
    public ItemStack getCatalyst() {
        return items.get(CATALYST_SLOT);
    }

    /**
     * 外部禁止修改 ItemStack，否则未定义行为
     * @return 转化输入
     */
    public ItemStack getInput() {
        return items.get(INPUT_SLOT);
    }

    /**
     * 外部禁止修改 ItemStack，否则未定义行为
     * @return 转化输出
     */
    public ItemStack getOutput() {
        return items.get(OUTPUT_SLOT);
    }

    /**
     * 外部禁止修改 ItemStack，禁止修改 List，否则未定义行为
     * @return 源质输入槽的不可变视图
     */
    public List<ItemStack> getInputEssences() {
        return Collections.unmodifiableList(items.subList(ESSENCE_INPUT_SLOT_BEGIN, ESSENCE_OUTPUT_SLOT_BEGIN));
    }

    /**
     * 外部禁止修改 ItemStack，禁止修改 List，否则未定义行为
     * @return 源质输出槽的不可变视图
     */
    public List<ItemStack> getOutputEssences() {
        return Collections.unmodifiableList(items.subList(ESSENCE_OUTPUT_SLOT_BEGIN, CATALYST_SLOT));
    }

    public List<ItemStack> getItems() {
        return Collections.unmodifiableList(items);
    }

    public int[] getRealSlotToRendererSlot() {
        return realSlotToRendererSlot;
    }

    public CrucibleRendererSlotParas[] getRendererSlotParas0() {
        return rendererSlotParas0;
    }

    public CrucibleRendererSlotParas[] getRendererSlotParas1() {
        return rendererSlotParas1;
    }

    public int getPolarity() {
        return polarity;
    }

    public int getSelectedSlot() {
        return selectedSlot - ESSENCE_INPUT_SLOT_BEGIN;
    }

    public int getProcessTimer() {
        return processTimer;
    }

    public int getTargetTimer() {
        return targetTimer;
    }

    public int getWaterAmount() {
        return waterHandler.getAmountAsInt(0);
    }

    public int getEssenceInputPulseSlot() {
        return essenceInputPulseSlot;
    }

    public long getEssenceInputPulseStartedAtMillis() {
        return essenceInputPulseStartedAtMillis;
    }

    public boolean hasCatalyst() {
        return !items.get(CATALYST_SLOT).isEmpty();
    }

    public boolean hasInput() {
        return !items.get(INPUT_SLOT).isEmpty();
    }

    public boolean hasInputEssenceMetals() {
        for (int i = ESSENCE_INPUT_SLOT_BEGIN; i < ESSENCE_OUTPUT_SLOT_BEGIN; i++) {
            if (!items.get(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyOutput() {
        if (!items.get(OUTPUT_SLOT).isEmpty()) {
            return true;
        }
        for (int i = ESSENCE_OUTPUT_SLOT_BEGIN; i < CATALYST_SLOT; i++) {
            if (!items.get(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void takeCatalyst(Player player) {
        ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), getCatalyst());
        level.addFreshEntity(itemEntity);
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), clearItemAndRecordChange(CATALYST_SLOT, new ArrayList<>())));
        setAndSyncReset(true);
        setChanged();
    }

    public void takeInput(Player player) {
        ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), getInput());
        level.addFreshEntity(itemEntity);
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), clearItemAndRecordChange(INPUT_SLOT, new ArrayList<>())));
        setAndSyncReset(true);
        setChanged();
    }

    public void takeEssenceInput(Player player) {
        ArrayList<ItemStackWithTwoSlots> updates = new ArrayList<>(ESSENCE_OUTPUT_SLOT_BEGIN - ESSENCE_INPUT_SLOT_BEGIN);
        for (int i = ESSENCE_INPUT_SLOT_BEGIN; i < ESSENCE_OUTPUT_SLOT_BEGIN; i++) {
            if (!items.get(i).isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), items.get(i));
                level.addFreshEntity(itemEntity);
                clearItemAndRecordChange(i, updates);
            }
        }
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), updates));
        setAndSyncReset(true);
        setChanged();
    }

    public void takeAllOutput(Player player) {
        ArrayList<ItemStackWithTwoSlots> updates = new ArrayList<>(CATALYST_SLOT - ESSENCE_OUTPUT_SLOT_BEGIN);
        for (int i = ESSENCE_OUTPUT_SLOT_BEGIN; i < CATALYST_SLOT; i++) {
            if (!items.get(i).isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), items.get(i));
                level.addFreshEntity(itemEntity);
                clearItemAndRecordChange(i, updates);
            }
        }
        ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), getOutput());
        level.addFreshEntity(itemEntity);
        clearItemAndRecordChange(OUTPUT_SLOT, updates);

        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), updates));
        setAndSyncReset(true);
        setChanged();
    }

    public void serverScrollSelectedSlot(boolean isIncrease) {
        int size = getRequiredEssenceCount();
        if (size > 1) {
            setAndSyncSelectedSlot(Math.floorMod(selectedSlot + (isIncrease ? 1 : -1), size));
            level.playSound(null, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5,
                    SoundEvents.STONE_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 0.5F, 0.6F);
            setChanged();
        }
    }

    // ======================客户端网络同步=========================
    public void clientSetItem(int slot, int rendererSlot, ItemStack itemStack) {
        boolean filledEmptyEssenceInputSlot = slot >= ESSENCE_INPUT_SLOT_BEGIN && slot < ESSENCE_OUTPUT_SLOT_BEGIN
                && items.get(slot).isEmpty() && !itemStack.isEmpty();
        items.set(slot, itemStack);
        realSlotToRendererSlot[slot] = rendererSlot;
        if (filledEmptyEssenceInputSlot) {
            essenceInputPulseSlot = slot - ESSENCE_INPUT_SLOT_BEGIN;
            essenceInputPulseStartedAtMillis = System.currentTimeMillis();
        }
    }

    public void clientSetPolarity(int polarity) {
        this.polarity = polarity;
    }

    public void clientSetProcessTimer(int processTimer) {
        this.processTimer = processTimer;
    }

    public void clientSetTargetTimer(int targetTimer) {
        this.targetTimer = targetTimer;
    }

    public void clientSetSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }

    public void clientSetWater(int amount) {
        waterHandler.set(0, FluidResource.of(Fluids.WATER), amount);
    }
}
