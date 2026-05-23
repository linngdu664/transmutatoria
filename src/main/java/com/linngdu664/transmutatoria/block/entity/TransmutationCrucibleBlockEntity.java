package com.linngdu664.transmutatoria.block.entity;

import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractTransmutationScrollItem;
import com.linngdu664.transmutatoria.item.EssenceMetalItem;
import com.linngdu664.transmutatoria.item.component.RecipeConditions;
import com.linngdu664.transmutatoria.network.to_client.*;
import com.linngdu664.transmutatoria.recipe.AlchemicalRecipeManager;
import com.linngdu664.transmutatoria.recipe.IAlchemicalRecipe;
import com.linngdu664.transmutatoria.util.AbstractAlchemySlot;
import com.linngdu664.transmutatoria.util.AlchemyReactResult;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
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
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TransmutationCrucibleBlockEntity extends BlockEntity implements WorldlyContainer {
    private static final AABB SUCK_AABB = Block.column(16.0F, 11.0F, 32.0F).toAabbs().getFirst();
    private static final int ESSENCE_INPUT_SLOT = 0;
    private static final int ESSENCE_OUTPUT_SLOT = 24;
    private static final int CATALYST_SLOT = 48;
    private static final int INPUT_SLOT = 49;
    private static final int OUTPUT_SLOT = 50;
    private static final int SLOT_COUNT = 51;
    private static final int[] UP_SLOTS = {CATALYST_SLOT, INPUT_SLOT};
    private static final int[] SIDE_SLOTS;
    private static final int[] DOWN_SLOTS;
    static {
        SIDE_SLOTS = new int[ESSENCE_OUTPUT_SLOT - ESSENCE_INPUT_SLOT];
        for (int i = 0; i < SIDE_SLOTS.length; i++) {
            SIDE_SLOTS[i] = ESSENCE_INPUT_SLOT + i;
        }
        DOWN_SLOTS = new int[CATALYST_SLOT - ESSENCE_OUTPUT_SLOT + 1];
        for (int i = 0; i < DOWN_SLOTS.length - 1; i++) {
            DOWN_SLOTS[i] = ESSENCE_OUTPUT_SLOT + i;
        }
        DOWN_SLOTS[DOWN_SLOTS.length - 1] = OUTPUT_SLOT;
    }

    // 源质输入 - 源质输出 - 催化剂 - 转化输入 - 转化输出
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int polarity;
    private int selectedSlot;
    private int processTimer;
    private int targetTimer;
    private boolean isFinish;
    private IntArrayList inputOrder = new IntArrayList();

    public TransmutationCrucibleBlockEntity(BlockPos pos, BlockState state) {
        super(InitBlocks.TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY.get(), pos, state);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level.isClientSide() || !(blockEntity instanceof TransmutationCrucibleBlockEntity crucible)) {
            return;
        }
        if (crucible.targetTimer > 0) {
            // targetTimer > 0 说明正在运行
            if (crucible.processTimer + 1 >= crucible.targetTimer) {
                crucible.clearInputAndSetAllOutput();
            } else {
                crucible.processTimer++;
                PacketDistributor.sendToPlayersTrackingChunk(
                        (ServerLevel) level,
                        crucible.getChunkPos(),
                        new CrucibleSetProcessTimerPayload(pos, crucible.processTimer)
                );
            }
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items, true);
        output.putInt("Polarity", polarity);
        output.putInt("SelectedSlot", selectedSlot);
        output.putInt("ProcessTimer", processTimer);
        output.putInt("TargetTimer", targetTimer);
        output.putBoolean("IsFinish", isFinish);
        output.putIntArray("InputOrder", inputOrder.toIntArray());
    }

    @Override
    public void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, items);
        polarity = input.getIntOr("Polarity", 0);
        selectedSlot = input.getIntOr("SelectedSlot", 0);
        processTimer = input.getIntOr("ProcessTimer", 0);
        targetTimer = input.getIntOr("TargetTimer", 0);
        isFinish = input.getBooleanOr("IsFinish", false);
        inputOrder = new IntArrayList(input.getIntArray("InputOrder").orElse(new int[0]));
    }

    // 区块加载时会自动触发全量更新同步
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public int[] getSlotsForFace(Direction direction) {
        return switch (direction) {
            case UP -> UP_SLOTS;
            case DOWN -> DOWN_SLOTS;
            default -> SIDE_SLOTS;
        };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        if (direction == null) return false;
        return switch (direction) {
            case UP -> {
                if (slot == CATALYST_SLOT) yield canAddCatalyst(itemStack);
                if (slot == INPUT_SLOT) yield canAddInput(itemStack);
                yield false;
            }
            case DOWN -> false;
            default -> canAddEssence(slot, itemStack);
        };
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
        if (!isFinish || direction != Direction.DOWN) return false;
        if (slot == OUTPUT_SLOT) return !items.get(OUTPUT_SLOT).isEmpty();
        if (slot >= ESSENCE_OUTPUT_SLOT && slot < CATALYST_SLOT) return !items.get(slot).isEmpty();
        return false;
    }

    @Override
    public int getContainerSize() {
        return SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : items) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 仅用于提供容器兼容，禁止在自己的代码中调用该函数
     */
    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    /**
     * 仅用于提供容器兼容，绝对禁止在自己的代码中调用该函数
     * 只能提取输出槽，在 getSlotsForFace 里设置？
     */
    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(items, slot, count);
        if (!result.isEmpty()) {
            ServerLevel serverLevel = (ServerLevel) level;
            ChunkPos chunkPos = getChunkPos();
            PacketDistributor.sendToPlayersTrackingChunk(serverLevel, chunkPos, new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(slot, items.get(slot)))));
            boolean isFinish1 = hasAnyOutput();
            if (isFinish1 != isFinish) {
                isFinish = isFinish1;
                PacketDistributor.sendToPlayersTrackingChunk(serverLevel, chunkPos, new CrucibleSetFinishPayload(getBlockPos(), isFinish1));
            }
            setChanged();
        }
        return result;
    }

    /**
     * 仅用于提供容器兼容，绝对禁止在自己的代码中调用该函数
     * 只能提取输出槽，在 getSlotsForFace 里设置？
     */
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    /**
     * 仅用于提供容器兼容，绝对禁止在自己的代码中调用该函数
     * 只能放置输入槽，在 getSlotsForFace 里设置？
     */
    @Override
    public void setItem(int slot, ItemStack itemStack) {
        items.set(slot, itemStack);
        PacketDistributor.sendToPlayersTrackingChunk((ServerLevel) level, getChunkPos(), new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(slot, itemStack))));
        if (slot == INPUT_SLOT) {
            tryReactAfterAddInput();
        } else if (slot >= ESSENCE_INPUT_SLOT && slot < ESSENCE_OUTPUT_SLOT) {
            tryReactAfterAddEssence();
        }
        setChanged();
    }

    /**
     * 仅用于提供容器兼容
     */
    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    /**
     * 仅用于提供容器兼容，绝对禁止在自己的代码中调用该函数
     */
    @Override
    public void clearContent() {
        ArrayList<ItemStackWithSlot> itemStackWithSlots = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            itemStackWithSlots.add(new ItemStackWithSlot(i, ItemStack.EMPTY));
        }
        items.clear();  // 由于 items 有默认值，这里的 clear 是重置而不是传统 clear
        isFinish = false;
        processTimer = 0;
        targetTimer = 0;
        selectedSlot = 0;
        inputOrder.clear();
        BlockPos blockPos = getBlockPos();
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                getChunkPos(),
                new CrucibleSetItemPayload(blockPos, itemStackWithSlots),
                new CrucibleSetFinishPayload(blockPos, false),
                new CrucibleSetPolarityPayload(blockPos, polarity),
                new CrucibleSetSelectedSlotPayload(blockPos, 0),
                new CrucibleSetProcessTimerPayload(blockPos, 0),
                new CrucibleSetTargetTimerPayload(blockPos, 0)
        );
        setChanged();
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
        tryReactAfterAddInput();
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
        int maxSlot = tryReactAfterAddEssence();

        // 公共逻辑：自动指向下一个槽位
        if (selectedSlot >= maxSlot - 1) {
            syncSelectedSlot(0);
        } else {
            syncSelectedSlot(selectedSlot + 1);
        }

        return true;
    }

    private void suckOneAndSync(ItemEntity entity, int slot) {
        ItemStack itemStack = entity.getItem();
        ItemStack copy = itemStack.copyWithCount(1);
        items.set(slot, copy);
        // sync
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                getChunkPos(),
                new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(slot, copy)))
        );
        if (itemStack.getCount() == 1) {
            entity.setItem(ItemStack.EMPTY);
            entity.discard();
        } else {
            itemStack.setCount(itemStack.getCount() - 1);
        }
    }

    private void syncTargetTimer(int targetTimer) {
        this.targetTimer = targetTimer;
        // sync
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                getChunkPos(),
                new CrucibleSetTargetTimerPayload(getBlockPos(), targetTimer)
        );
    }

    private void syncSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
        // sync
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                getChunkPos(),
                new CrucibleSetSelectedSlotPayload(getBlockPos(), selectedSlot)
        );
    }

    private ChunkPos getChunkPos() {
        return new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ()));
    }

    public boolean canAddCatalyst(ItemStack itemStack) {
        return targetTimer == 0 && !isFinish && getCatalyst().isEmpty()
                && (itemStack.is(Items.ENDER_EYE) || itemStack.is(InitItems.TRANSMUTATION_CRYSTAL)
                || itemStack.is(InitItems.PHILOSOPHERS_STONE) || itemStack.getItem() instanceof EssenceMetalItem
                || (itemStack.getItem() instanceof AbstractTransmutationScrollItem && itemStack.has(InitDataComponents.ALCHEMY_SLOTS) && itemStack.has(InitDataComponents.RECIPE_CONDITIONS)));
    }

    public boolean canAddInput(ItemStack itemStack) {
        if (targetTimer != 0 || isFinish || !getInput().isEmpty() || itemStack.isEmpty()) {
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
            return container.getSlots() > 0 && itemStack.getItem() == container.getStackInSlot(0).getItem();
        }

        if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            // 混沌分解：该物品需要可被炼金复制合成
            return AlchemicalRecipeManager.findMatchRep(level, itemStack) != null;
        }

        // 其他的催化剂不接受转化输入
        return false;
    }

    public boolean canAddEssence(int slot, ItemStack itemStack) {
        if (targetTimer != 0 || isFinish || !(itemStack.getItem() instanceof EssenceMetalItem) || slot < ESSENCE_INPUT_SLOT || !items.get(slot).isEmpty()) {
            return false;
        }
        ItemStack catalyst = getCatalyst();

        if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            // 炼金复制/炼金合成：输入符合且槽位不越界
            ItemContainerContents container = catalyst.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
            return container.getSlots() > 0 && getInput().getItem() == container.getStackInSlot(0).getItem()
                    && !alchemySlots.isEmpty() && slot - ESSENCE_INPUT_SLOT < alchemySlots.size();
        }

        if (getCatalyst().is(InitItems.TRANSMUTATION_CRYSTAL)) {
            // 源质反应：槽位只能是 0 或 1
            return slot - ESSENCE_INPUT_SLOT < 2;
        }

        if (getCatalyst().getItem() instanceof EssenceMetalItem essenceMetalItem) {
            // 源质融合：槽位不越界
            return slot - ESSENCE_INPUT_SLOT < essenceMetalItem.getEssenceMetal().getBeRestrainedOrBeDoubleRestrained().size();
        }

        // 其他的催化剂不接受源质金属输入
        return false;
    }

    private void tryReactAfterAddInput() {
        ItemStack catalyst = getCatalyst();
        if (catalyst.is(Items.ENDER_EYE)) {
            // 嬗变分解：无须源质金属，直接开始反应
            syncTargetTimer(10);
        } else if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            // 混沌分解：无须源质金属，直接开始反应
            IAlchemicalRecipe recipe = AlchemicalRecipeManager.findMatchRep(level, getInput());
            if (recipe != null) {
                syncTargetTimer(5 * (recipe.minLevel() + recipe.maxLevel()));
            }
        }
    }

    private int tryReactAfterAddEssence() {
        ItemStack catalyst = getCatalyst();
        // 显然嬗变分解、混沌分解不会调用这个函数
        if (catalyst.getItem() instanceof AbstractTransmutationScrollItem) {
            // 炼金复制/炼金合成：加够了
            List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
            if (inputOrder.size() == alchemySlots.size()) {
                syncTargetTimer(10 * alchemySlots.size());
            }
            return alchemySlots.size();
        } else if (getCatalyst().is(InitItems.TRANSMUTATION_CRYSTAL)) {
            // 源质反应：两个槽都有金属了
            if (!items.get(ESSENCE_INPUT_SLOT).isEmpty() && !items.get(ESSENCE_INPUT_SLOT + 1).isEmpty()) {
                syncTargetTimer(20);
            }
            return 2;
        } else if (getCatalyst().getItem() instanceof EssenceMetalItem essenceMetalItem) {
            // 源质融合：加够了且覆盖所有被克制
            // todo
            return 0;
        }
        return 0;
    }

    private void clearInputAndSetAllOutput() {
        ArrayList<ItemStackWithSlot> itemStackWithSlotsUpdate = new ArrayList<>();
        ItemStack catalyst = getCatalyst();
        if (catalyst.is(Items.ENDER_EYE)) {
            handleEnderEyeReaction(itemStackWithSlotsUpdate);   // 嬗变分解的反应结果
        } else if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            // todo 源质反应的反应结果
        } else if (catalyst.getItem() instanceof EssenceMetalItem) {
            // todo 源质融合的反应结果
        } else if (catalyst.is(InitItems.PHILOSOPHERS_STONE)) {
            // todo 混沌分解的反应结果
        } else {
            handleScrollReaction(catalyst, itemStackWithSlotsUpdate);   // 炼金复制/炼金转化的反应结果
        }

        // 检查锅的极性，超过了就爆掉
        if (polarity > 50) {
            level.setBlock(getBlockPos(), Blocks.REDSTONE_BLOCK.defaultBlockState(), 3);
        } else if (polarity < -50) {
            level.setBlock(getBlockPos(), InitBlocks.ALCHEMICAL_DROSS_BLOCK.get().defaultBlockState(), 3);
        } else {
            isFinish = true;
            processTimer = 0;
            targetTimer = 0;
            selectedSlot = 0;
            inputOrder.clear();
            BlockPos blockPos = getBlockPos();
            PacketDistributor.sendToPlayersTrackingChunk(
                    (ServerLevel) level,
                    getChunkPos(),
                    new CrucibleSetItemPayload(blockPos, itemStackWithSlotsUpdate),
                    new CrucibleSetFinishPayload(blockPos, true),
                    new CrucibleSetPolarityPayload(blockPos, polarity),
                    new CrucibleSetSelectedSlotPayload(blockPos, 0),
                    new CrucibleSetProcessTimerPayload(blockPos, 0),
                    new CrucibleSetTargetTimerPayload(blockPos, 0)
            );
        }
    }

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

    private void handleScrollReaction(ItemStack catalyst, ArrayList<ItemStackWithSlot> itemStackWithSlotsUpdate) {
        List<AbstractAlchemySlot> alchemySlots = catalyst.get(InitDataComponents.ALCHEMY_SLOTS);
        if (alchemySlots != null && !alchemySlots.isEmpty() && inputOrder.size() == alchemySlots.size()) {
            // 反应前预先做的事
            boolean[] inhibitionStates = new boolean[alchemySlots.size()];
            Int2IntOpenHashMap posToOutputSlot = new Int2IntOpenHashMap();
            ArrayList<Runnable> deferredTasks = new ArrayList<>();
            int i = 0;
            for (AbstractAlchemySlot slot : alchemySlots) {
                posToOutputSlot.put(slot.getPackedXY(), i);
                i++;
            }
            int hashValue = 31 + Long.hashCode(getBlockPos().asLong());
            hashValue = 31 * hashValue + Long.hashCode(catalyst.getOrDefault(InitDataComponents.NEXT_EXPIRE, Long.MAX_VALUE));
            hashValue = 31 * hashValue;

            // 反应
            int annihilationCnt = 0;
            int entropy = catalyst.getOrDefault(InitDataComponents.ENTROPY, 0);
            for (int slot : inputOrder) {
                AlchemyReactResult result = alchemySlots.get(slot).react(
                        catalyst,
                        items.get(slot),
                        items.subList(ESSENCE_OUTPUT_SLOT, CATALYST_SLOT),
                        inhibitionStates,
                        posToOutputSlot,
                        deferredTasks,
                        hashValue + slot
                );
                polarity += result.getPolarityIncrease();
                entropy += result.getEntropyIncrease();
                if (result.isTriggerDamage()) {
                    catalyst.hurtAndBreak(entropy, (ServerLevel) level, null, _ -> {});
                    annihilationCnt++;
                }
            }
            catalyst.set(InitDataComponents.ENTROPY, entropy);

            // 如果全湮灭则消耗输入，如果极性满足条件则产出输出
            if (annihilationCnt == alchemySlots.size()) {
                RecipeConditions conditions = getCatalyst().getOrDefault(InitDataComponents.RECIPE_CONDITIONS, RecipeConditions.DEFAULT);
                ItemContainerContents container = catalyst.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                if (polarity >= conditions.minPolarity() && polarity <= conditions.maxPolarity() && container.getSlots() >= 2 && getInput().getItem() == container.getStackInSlot(0).getItem()) {
                    setItemAndRecordChange(OUTPUT_SLOT, container.getStackInSlot(1), itemStackWithSlotsUpdate);
                    if (conditions.oneTime()) {
                        items.set(CATALYST_SLOT, ItemStack.EMPTY);
                    }
                }
                setItemAndRecordChange(INPUT_SLOT, ItemStack.EMPTY, itemStackWithSlotsUpdate);
            }

            for (int j = ESSENCE_INPUT_SLOT, size = alchemySlots.size(); j < size; j++) {
                setItemAndRecordChange(j, ItemStack.EMPTY, itemStackWithSlotsUpdate);
            }
            for (int j = ESSENCE_OUTPUT_SLOT, size = ESSENCE_OUTPUT_SLOT + alchemySlots.size(); j < size; j++) {
                recordItemChange(j, itemStackWithSlotsUpdate);
            }
            recordItemChange(CATALYST_SLOT, itemStackWithSlotsUpdate);
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
     * 禁止修改 ItemStack，否则未定义行为
     * @return 催化剂
     */
    public ItemStack getCatalyst() {
        return items.get(CATALYST_SLOT);
    }

    /**
     * 禁止修改 ItemStack，否则未定义行为
     * @return 转化输入
     */
    public ItemStack getInput() {
        return items.get(INPUT_SLOT);
    }

    /**
     * 禁止修改 ItemStack，否则未定义行为
     * @return 转化输出
     */
    public ItemStack getOutput() {
        return items.get(OUTPUT_SLOT);
    }

    /**
     * 禁止修改 ItemStack，禁止修改 List，否则未定义行为
     * @return 源质输入槽的不可变视图
     */
    public List<ItemStack> getInputEssences() {
        return Collections.unmodifiableList(items.subList(ESSENCE_INPUT_SLOT, ESSENCE_OUTPUT_SLOT));
    }

    /**
     * 禁止修改 ItemStack，禁止修改 List，否则未定义行为
     * @return 源质输出槽的不可变视图
     */
    public List<ItemStack> getOutputEssences() {
        return Collections.unmodifiableList(items.subList(ESSENCE_OUTPUT_SLOT, CATALYST_SLOT));
    }

    public int getPolarity() {
        return polarity;
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public int getProcessTimer() {
        return processTimer;
    }

    public int getTargetTimer() {
        return targetTimer;
    }

    public boolean isFinish() {
        return this.isFinish;
    }

    public boolean hasCatalyst() {
        return !items.get(CATALYST_SLOT).isEmpty();
    }

    public boolean hasInput() {
        return !items.get(INPUT_SLOT).isEmpty();
    }

    public boolean hasInputEssenceMetals() {
        for (int i = ESSENCE_INPUT_SLOT; i < ESSENCE_OUTPUT_SLOT; i++) {
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
        for (int i = ESSENCE_OUTPUT_SLOT; i < CATALYST_SLOT; i++) {
            if (!items.get(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void takeCatalyst(Player player) {
        player.getInventory().placeItemBackInInventory(getCatalyst(), true);
        items.set(CATALYST_SLOT, ItemStack.EMPTY);
        playerCancelSync(List.of(new ItemStackWithSlot(CATALYST_SLOT, ItemStack.EMPTY)));
        setChanged();
    }

    public void takeInput(Player player) {
        player.getInventory().placeItemBackInInventory(getInput(), true);
        items.set(INPUT_SLOT, ItemStack.EMPTY);
        playerCancelSync(List.of(new ItemStackWithSlot(INPUT_SLOT, ItemStack.EMPTY)));
        setChanged();
    }

    public void takeEssenceInput(Player player) {
        Inventory inventory = player.getInventory();
        ArrayList<ItemStackWithSlot> itemStackWithSlots = new ArrayList<>(ESSENCE_OUTPUT_SLOT - ESSENCE_INPUT_SLOT);
        for (int i = ESSENCE_INPUT_SLOT; i < ESSENCE_OUTPUT_SLOT; i++) {
            if (!items.get(i).isEmpty()) {
                inventory.placeItemBackInInventory(items.get(i));
                setItemAndRecordChange(i, ItemStack.EMPTY, itemStackWithSlots);
            }
        }
        inputOrder.clear();
        playerCancelSync(itemStackWithSlots);
        setChanged();
    }

    private void playerCancelSync(List<ItemStackWithSlot> itemStackWithSlots) {
        BlockPos blockPos = getBlockPos();
        ChunkPos chunkPos = getChunkPos();
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                chunkPos,
                new CrucibleSetItemPayload(blockPos, itemStackWithSlots)
        );
        if (targetTimer > 0) {
            processTimer = 0;
            targetTimer = 0;
            selectedSlot = 0;
            PacketDistributor.sendToPlayersTrackingChunk(
                    (ServerLevel) level,
                    chunkPos,
                    new CrucibleSetSelectedSlotPayload(blockPos, 0),
                    new CrucibleSetProcessTimerPayload(blockPos, 0),
                    new CrucibleSetTargetTimerPayload(blockPos, 0)
            );
        }
    }

    public void takeAllOutput(Player player) {
        Inventory inventory = player.getInventory();
        ArrayList<ItemStackWithSlot> itemStackWithSlots = new ArrayList<>(CATALYST_SLOT - ESSENCE_OUTPUT_SLOT);
        for (int i = ESSENCE_OUTPUT_SLOT; i < CATALYST_SLOT; i++) {
            if (!items.get(i).isEmpty()) {
                inventory.placeItemBackInInventory(items.get(i));
                setItemAndRecordChange(i, ItemStack.EMPTY, itemStackWithSlots);
            }
        }
        inventory.placeItemBackInInventory(getOutput(), true);
        setItemAndRecordChange(OUTPUT_SLOT, ItemStack.EMPTY, itemStackWithSlots);
        isFinish = false;
        // sync
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                getChunkPos(),
                new CrucibleSetItemPayload(getBlockPos(), itemStackWithSlots),
                new CrucibleSetFinishPayload(getBlockPos(), false)
        );
        setChanged();
    }

    public void serverScrollSelectedSlot(boolean isIncrease) {
        ItemStack catalyst = getCatalyst();
        if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            if (selectedSlot == 0) {
                syncSelectedSlot(1);
            } else {
                syncSelectedSlot(0);
            }
        } else if (getCatalyst().getItem() instanceof EssenceMetalItem essenceMetalItem) {
            // todo
        } else if (getCatalyst().getItem() instanceof AbstractTransmutationScrollItem) {
            List<AbstractAlchemySlot> alchemySlots = catalyst.getOrDefault(InitDataComponents.ALCHEMY_SLOTS, List.of());
            if (!alchemySlots.isEmpty()) {
                if (isIncrease) {
                    if (selectedSlot == alchemySlots.size() - 1) {
                        syncSelectedSlot(0);
                    } else {
                        syncSelectedSlot(selectedSlot + 1);
                    }
                } else {
                    if (selectedSlot == 0) {
                        syncSelectedSlot(alchemySlots.size() - 1);
                    } else {
                        syncSelectedSlot(selectedSlot - 1);
                    }
                }
            }
        }
    }

    // ======================客户端网络同步=========================
    public void clientSetItem(int slot, ItemStack itemStack) {
        this.items.set(slot, itemStack);
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

    public void clientSetFinish(boolean finish) {
        this.isFinish = finish;
    }

    public void clientSetSelectedSlot(int selectedSlot) {
        this.selectedSlot = selectedSlot;
    }
}
