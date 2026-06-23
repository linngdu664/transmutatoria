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
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import com.linngdu664.transmutatoria.util.EssenceMetal;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.ItemStackWithSlot;
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
    public static final int WATER_PER_REACTION = 20;
    private static final int ESSENCE_INPUT_SLOT_BEGIN = 0;
    private static final int ESSENCE_OUTPUT_SLOT_BEGIN = 24;
    private static final int CATALYST_SLOT = 48;
    private static final int INPUT_SLOT = 49;
    private static final int OUTPUT_SLOT = 50;
    private static final int SLOT_COUNT = 51;

    // 源质输入 - 源质输出 - 催化剂 - 转化输入 - 转化输出
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private IntArrayList inputOrder = new IntArrayList();
    private int polarity;
    private int selectedSlot;
    private int processTimer;
    private int targetTimer;
    private int essenceInputPulseSlot = -1; // client only
    private long essenceInputPulseStartedAtMillis;  // client only

    private record ItemsSnapshot(ItemStack[] items, int[] inputOrder, int targetTimer) {}

    private final SnapshotJournal<ItemsSnapshot> itemsJournal = new SnapshotJournal<>() {
        @Override
        protected ItemsSnapshot createSnapshot() {
            ItemStack[] copy = new ItemStack[SLOT_COUNT];
            for (int i = 0; i < SLOT_COUNT; i++) {
                copy[i] = items.get(i).copy();
            }
            return new ItemsSnapshot(copy, inputOrder.toIntArray(), targetTimer);
        }

        @Override
        protected void revertToSnapshot(ItemsSnapshot snapshot) {
            ItemStack[] snapItems = snapshot.items;
            for (int i = 0; i < snapItems.length; i++) {
                items.set(i, snapItems[i]);
            }
            inputOrder = new IntArrayList(snapshot.inputOrder());
            targetTimer = snapshot.targetTimer;
        }

        @Override
        protected void onRootCommit(ItemsSnapshot snapshot) {
            // todo 疑问：onRootCommit 只在服务端被调用吗？
            if (level instanceof ServerLevel serverLevel) {
                ArrayList<ItemStackWithSlot> changes = new ArrayList<>();
                ItemStack[] snapItems = snapshot.items;
                for (int i = 0; i < snapItems.length; i++) {
                    if (!ItemStack.isSameItemSameComponents(items.get(i), snapItems[i])) {
                        changes.add(new ItemStackWithSlot(i, items.get(i)));
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
                } else if (targetTimer != 0 && water < WATER_PER_REACTION) {
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
                    items.set(CATALYST_SLOT, itemStack);
                    return 1;
                }
                return 0;
            }
            if (index == 1) {
                ItemStack itemStack = resource.toStack();
                if (canAddInput(itemStack)) {
                    itemsJournal.updateSnapshots(transaction);
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
            return result.getCount();
        }
    };

    public TransmutationCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(InitBlocks.TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY.get(), pos, state);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level.isClientSide() || !(blockEntity instanceof TransmutationCrucibleBlockEntity crucible)) {
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
            if (times > 0) {
                AbstractTransmutationScrollItem.changeEssence(level, itemStack, times);
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, crucible.getChunkPos(), new CrucibleSetItemPayload(pos, List.of(new ItemStackWithSlot(CATALYST_SLOT, itemStack))));
                crucible.setChanged();
            } else if (nextExpire != itemStack.getOrDefault(InitDataComponents.NEXT_EXPIRE, Long.MAX_VALUE)) {
                PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, crucible.getChunkPos(), new CrucibleSetItemPayload(pos, List.of(new ItemStackWithSlot(CATALYST_SLOT, itemStack))));
                crucible.setChanged();
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
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(slot, copy))));
        if (itemStack.getCount() == 1) {
            entity.setItem(ItemStack.EMPTY);
            entity.discard();
        } else {
            itemStack.setCount(itemStack.getCount() - 1);
        }
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
        if (waterHandler.getAmountAsInt(0) < WATER_PER_REACTION) {
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

    private void clearInputAndSetAllOutput() {
        ArrayList<ItemStackWithSlot> itemStackWithSlotsUpdate = new ArrayList<>();
        ItemStack catalyst = getCatalyst();
        if (catalyst.is(Items.ENDER_EYE)) {
            handleEnderEyeReaction(itemStackWithSlotsUpdate);   // 嬗变分解的反应结果
        } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            handleCrystalReaction(itemStackWithSlotsUpdate);    // 源质反应的反应结果
        } else if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            handlePhilosophersStoneReaction(itemStackWithSlotsUpdate); // 混沌分解的反应结果
        } else if (catalyst.getItem() instanceof EssenceMetalItem) {
            setItemAndRecordChange(OUTPUT_SLOT, getCatalyst().copy(), itemStackWithSlotsUpdate);    // 源质融合的反应结果
        } else if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            handleScrollReaction(itemStackWithSlotsUpdate);   // 炼金复制/炼金转化的反应结果
        }
        clearInputEssencesAndRecordChange(itemStackWithSlotsUpdate);    // 无论何种反应，输入的源质都得清空

        // 检查锅的极性，超过了就爆掉
        if (polarity > 50) {
            level.setBlock(getBlockPos(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        } else if (polarity < -50) {
            level.setBlock(getBlockPos(), InitBlocks.ALCHEMICAL_DROSS_BLOCK.get().defaultBlockState(), 3);
        } else {
            // 先重置 timer，否则 waterHandler 可能会多发一个包，虽然无伤大雅
            setAndSyncReset(true);
            PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), itemStackWithSlotsUpdate));
            // 消耗水
            try (var tx = Transaction.openRoot()) {
                waterHandler.extract(0, FluidResource.of(Fluids.WATER), WATER_PER_REACTION, tx);
                tx.commit();
            }
        }
    }

    // todo 概率要在配置文件里调吗？
    private void handleEnderEyeReaction(ArrayList<ItemStackWithSlot> itemStackWithSlotsUpdate) {
        if (getInput().is(InitItems.TRANSMUTATION_CRYSTAL)) {
            RandomSource randomSource = level.getRandom();
            if (randomSource.nextFloat() < 0.01f) {
                setItemAndRecordChange(OUTPUT_SLOT, InitItems.PANDEMONIUM.toStack(), itemStackWithSlotsUpdate);
            } else {
                setItemAndRecordChange(OUTPUT_SLOT, InitItems.ESSENCE_METAL_ITEMS[randomSource.nextInt(InitItems.ESSENCE_METAL_ITEMS.length - 1)].toStack(), itemStackWithSlotsUpdate);
            }
            setItemAndRecordChange(INPUT_SLOT, ItemStack.EMPTY, itemStackWithSlotsUpdate);
        }
    }

    private void handleCrystalReaction(ArrayList<ItemStackWithSlot> itemStackWithSlotsUpdate) {
        if (items.get(ESSENCE_INPUT_SLOT_BEGIN).getItem() instanceof EssenceMetalItem essence1 && items.get(ESSENCE_INPUT_SLOT_BEGIN + 1).getItem() instanceof EssenceMetalItem essence2) {
            EssenceMetal.Relation relation = essence1.getEssenceMetal().getRelationTo(essence2.getEssenceMetal());
            setItemAndRecordChange(ESSENCE_OUTPUT_SLOT_BEGIN, essence1.change(relation.self), itemStackWithSlotsUpdate);
            setItemAndRecordChange(ESSENCE_OUTPUT_SLOT_BEGIN + 1, essence2.change(relation.other), itemStackWithSlotsUpdate);
            polarity -= relation.self + relation.other;
        }
    }

    private void handlePhilosophersStoneReaction(ArrayList<ItemStackWithSlot> itemStackWithSlotsUpdate) {
        CrucibleRecipe recipe = CrucibleRecipeManager.findMatchRep(level, getInput());
        if (recipe != null) {
            RandomSource randomSource = level.getRandom();
            IntIntImmutablePair minMax = recipe.level().getMinMax(level, getInput());
            int[] outputSlots = new int[CATALYST_SLOT - ESSENCE_OUTPUT_SLOT_BEGIN];
            for (int i = 0; i < outputSlots.length; i++) {
                outputSlots[i] = i;
            }
            int essenceCnt = Math.min(randomSource.nextInt(minMax.leftInt(), minMax.rightInt() + 1), outputSlots.length);
            int len = InitItems.ESSENCE_METAL_ITEMS.length;
            for (int i = 0; i < essenceCnt; i++) {
                int randomSlotIndex = randomSource.nextInt(i, outputSlots.length);
                int outputSlot = outputSlots[randomSlotIndex];
                outputSlots[randomSlotIndex] = outputSlots[i];
                outputSlots[i] = outputSlot;
                setItemAndRecordChange(ESSENCE_OUTPUT_SLOT_BEGIN + outputSlot, InitItems.ESSENCE_METAL_ITEMS[randomSource.nextInt(len)].toStack(), itemStackWithSlotsUpdate);
            }
            setItemAndRecordChange(INPUT_SLOT, ItemStack.EMPTY, itemStackWithSlotsUpdate);
        }
    }

    private void handleScrollReaction(ArrayList<ItemStackWithSlot> itemStackWithSlotsUpdate) {
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
                    catalyst.hurtAndBreak(1 + entropy / 4, (ServerLevel) level, null, _ -> {});
                    annihilationCnt++;
                }
            }
            for (Runnable deferredTask : deferredTasks) {
                deferredTask.run();
            }
            catalyst.set(InitDataComponents.ALCHEMY_SLOTS, alchemySlots);
            catalyst.set(InitDataComponents.ENTROPY, entropy);

            // 如果全湮灭则消耗输入，如果极性满足条件则产出输出
            if (annihilationCnt == alchemySlots.size()) {
                RecipeConditions conditions = getCatalyst().getOrDefault(InitDataComponents.RECIPE_CONDITIONS, RecipeConditions.DEFAULT);
                ItemContainerContents container = catalyst.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                if (polarity >= conditions.minPolarity() && polarity <= conditions.maxPolarity() && container.getSlots() >= 2 && ItemStack.isSameItemSameComponents(getInput(), container.getStackInSlot(0))) {
                    setItemAndRecordChange(OUTPUT_SLOT, container.getStackInSlot(1), itemStackWithSlotsUpdate);
                    if (conditions.oneTime()) {
                        items.set(CATALYST_SLOT, ItemStack.EMPTY);
                    }
                }
                setItemAndRecordChange(INPUT_SLOT, ItemStack.EMPTY, itemStackWithSlotsUpdate);
            }

            for (int j = ESSENCE_OUTPUT_SLOT_BEGIN, upper = ESSENCE_OUTPUT_SLOT_BEGIN + alchemySlots.size(); j < upper; j++) {
                recordItemChange(j, itemStackWithSlotsUpdate);
            }
            recordItemChange(CATALYST_SLOT, itemStackWithSlotsUpdate);
        }
    }

    private void clearInputEssencesAndRecordChange(List<ItemStackWithSlot> itemStackWithSlots) {
        for (int i = ESSENCE_INPUT_SLOT_BEGIN; i < ESSENCE_OUTPUT_SLOT_BEGIN; i++) {
            if (!items.get(i).isEmpty()) {
                setItemAndRecordChange(i, ItemStack.EMPTY, itemStackWithSlots);
            }
        }
    }

    private void setItemAndRecordChange(int slot, ItemStack itemStack, List<ItemStackWithSlot> itemStackWithSlots) {
        items.set(slot, itemStack);
        itemStackWithSlots.add(new ItemStackWithSlot(slot, itemStack));
    }

    private void recordItemChange(int slot, List<ItemStackWithSlot> itemStackWithSlots) {
        itemStackWithSlots.add(new ItemStackWithSlot(slot, items.get(slot)));
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
        items.set(CATALYST_SLOT, ItemStack.EMPTY);
        level.addFreshEntity(itemEntity);
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(CATALYST_SLOT, ItemStack.EMPTY))));
        setAndSyncReset(true);
        setChanged();
    }

    public void takeInput(Player player) {
        ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), getInput());
        items.set(INPUT_SLOT, ItemStack.EMPTY);
        level.addFreshEntity(itemEntity);
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(INPUT_SLOT, ItemStack.EMPTY))));
        setAndSyncReset(true);
        setChanged();
    }

    public void takeEssenceInput(Player player) {
        ArrayList<ItemStackWithSlot> itemStackWithSlots = new ArrayList<>(ESSENCE_OUTPUT_SLOT_BEGIN - ESSENCE_INPUT_SLOT_BEGIN);
        for (int i = ESSENCE_INPUT_SLOT_BEGIN; i < ESSENCE_OUTPUT_SLOT_BEGIN; i++) {
            if (!items.get(i).isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), items.get(i));
                setItemAndRecordChange(i, ItemStack.EMPTY, itemStackWithSlots);
                level.addFreshEntity(itemEntity);
            }
        }
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), itemStackWithSlots));
        setAndSyncReset(true);
        setChanged();
    }

    public void takeAllOutput(Player player) {
        ArrayList<ItemStackWithSlot> itemStackWithSlots = new ArrayList<>(CATALYST_SLOT - ESSENCE_OUTPUT_SLOT_BEGIN);
        for (int i = ESSENCE_OUTPUT_SLOT_BEGIN; i < CATALYST_SLOT; i++) {
            if (!items.get(i).isEmpty()) {
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), items.get(i));
                setItemAndRecordChange(i, ItemStack.EMPTY, itemStackWithSlots);
                level.addFreshEntity(itemEntity);
            }
        }
        ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), getOutput());
        setItemAndRecordChange(OUTPUT_SLOT, ItemStack.EMPTY, itemStackWithSlots);
        level.addFreshEntity(itemEntity);

        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), itemStackWithSlots));
        setAndSyncReset(true);
        setChanged();
    }

    public void serverScrollSelectedSlot(boolean isIncrease) {
        ItemStack catalyst = getCatalyst();
        if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            setAndSyncSelectedSlot(selectedSlot == 0 ? 1 : 0);
            setChanged();
        } else if (catalyst.getItem() instanceof EssenceMetalItem essenceMetalItem) {
            int size = essenceMetalItem.getEssenceMetal().getRestrainsAndDoubleRestrains().size();
            setAndSyncSelectedSlot(Math.floorMod(selectedSlot + (isIncrease ? 1 : -1), size));
            setChanged();
        } else if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
            if (!alchemySlots.isEmpty()) {
                int size = alchemySlots.size();
                setAndSyncSelectedSlot(Math.floorMod(selectedSlot + (isIncrease ? 1 : -1), size));
                setChanged();
            }
        }
    }

    // ======================客户端网络同步=========================
    public void clientSetItem(int slot, ItemStack itemStack) {
        boolean filledEmptyEssenceInputSlot = slot >= ESSENCE_INPUT_SLOT_BEGIN && slot < ESSENCE_OUTPUT_SLOT_BEGIN
                && items.get(slot).isEmpty() && !itemStack.isEmpty();
        this.items.set(slot, itemStack);
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
