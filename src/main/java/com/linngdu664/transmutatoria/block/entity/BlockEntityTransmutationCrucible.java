package com.linngdu664.transmutatoria.block.entity;

import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractItemTransmutationScroll;
import com.linngdu664.transmutatoria.item.ItemEssenceMetal;
import com.linngdu664.transmutatoria.network.to_client.CrucibleSetFinishPayload;
import com.linngdu664.transmutatoria.network.to_client.CrucibleSetItemPayload;
import com.linngdu664.transmutatoria.network.to_client.CrucibleSetProcessTimerPayload;
import com.linngdu664.transmutatoria.network.to_client.CrucibleSetTargetTimerPayload;
import net.minecraft.core.*;
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
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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

public class BlockEntityTransmutationCrucible extends BlockEntity implements WorldlyContainer {
    private static final AABB SUCK_AABB = Block.column(16.0F, 11.0F, 32.0F).toAabbs().get(0);
    private static final int ESSENCE_INPUT_SLOT = 0;
    private static final int ESSENCE_OUTPUT_SLOT = 24;
    private static final int CATALYST_SLOT = 48;
    private static final int INPUT_SLOT = 49;
    private static final int OUTPUT_SLOT = 50;
    public static final int SLOT_COUNT = 51;
    // 源质输入 - 源质输出 - 催化剂 - 转化输入 - 转化输出
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
    private int polarity;
    private int selectedSlot;
    private int processTimer;
    private int targetTimer;
    private boolean isFinish;

    public BlockEntityTransmutationCrucible(BlockPos pos, BlockState state) {
        super(InitBlocks.TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY.get(), pos, state);
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level.isClientSide() || !(blockEntity instanceof BlockEntityTransmutationCrucible crucible)) {
            return;
        }
        if (crucible.targetTimer > 0) {
            // targetTimer > 0 说明正在运行
            if (crucible.processTimer + 1 >= crucible.targetTimer) {
                crucible.clearInputAndSetAllOutput();
            } else {
                crucible.syncProcessTimer(crucible.processTimer + 1);
            }
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items, true);
        long bitMap = 0;
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (items.get(i).isEmpty()) {
                bitMap |= (1L << i);
            }
        }
        output.putLong("EmptySlots", bitMap);
        output.putInt("Polarity", polarity);
        output.putInt("SelectedSlot", selectedSlot);
        output.putInt("ProcessTimer", processTimer);
        output.putInt("TargetTimer", targetTimer);
        output.putBoolean("isFinish", isFinish);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, items);
        long bitMap = input.getLongOr("EmptySlots", 0L);
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (((bitMap >> i) & 1) != 0) {
                items.set(i, ItemStack.EMPTY);
            }
        }
        polarity = input.getIntOr("Polarity", 0);
        selectedSlot = input.getIntOr("SelectedSlot", 0);
        processTimer = input.getIntOr("ProcessTimer", 0);
        targetTimer = input.getIntOr("TargetTimer", 0);
        isFinish = input.getBooleanOr("isFinish", false);
    }

    // 区块加载时会触发全量更新同步
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    // todo
    @Override
    public int[] getSlotsForFace(Direction direction) {
        return new int[0];
    }

    // todo
    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack itemStack, @Nullable Direction direction) {
        return false;
    }

    // todo
    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack itemStack, Direction direction) {
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
     */
    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(items, slot, count);
        if (!result.isEmpty()) {
            isFinish = hasAnyOutput();
            PacketDistributor.sendToPlayersTrackingChunk(
                    (ServerLevel) level,
                    new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                    new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(slot, items.get(slot)))),
                    new CrucibleSetFinishPayload(getBlockPos(), isFinish)
            );
            setChanged();
        }
        return result;
    }

    /**
     * 仅用于提供容器兼容，绝对禁止在自己的代码中调用该函数
     */
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    /**
     * 仅用于提供容器兼容，绝对禁止在自己的代码中调用该函数
     */
    @Override
    public void setItem(int slot, ItemStack itemStack) {
        items.set(slot, itemStack);
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(slot, itemStack)))
        );
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
        // 神了，由于 items 有默认值，这里的 clear 是重置而不是传统 clear
        items.clear();
        isFinish = false;
        ArrayList<ItemStackWithSlot> itemStackWithSlots = new ArrayList<>(SLOT_COUNT);
        for (int i = 0; i < SLOT_COUNT; i++) {
            itemStackWithSlots.add(new ItemStackWithSlot(i, ItemStack.EMPTY));
        }
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                new CrucibleSetItemPayload(getBlockPos(), itemStackWithSlots),
                new CrucibleSetFinishPayload(getBlockPos(), false)
        );
        setChanged();
    }

    // todo
    public void entityInside(Level level, BlockPos pos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity && entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(SUCK_AABB)) {
            if (tryAddCatalystFromItemEntity(itemEntity) || tryAddInputFromItemEntity(itemEntity) || tryAddEssenceFromItemEntity(itemEntity)) {
                setChanged();
            }
        }
    }

    private boolean tryAddCatalystFromItemEntity(ItemEntity entity) {
        if (!canAddCatalyst()) {
            return false;
        }
        ItemStack itemStack = entity.getItem();
        if (itemStack.isEmpty()) {
            return false;
        }
        if (itemStack.is(Items.ENDER_EYE) || itemStack.is(InitItems.TRANSMUTATION_CRYSTAL)
                || itemStack.getItem() instanceof ItemEssenceMetal
                || itemStack.getItem() instanceof AbstractItemTransmutationScroll) {
            suckOneAndSync(entity, CATALYST_SLOT);
        }
        return true;
    }

    private boolean tryAddInputFromItemEntity(ItemEntity entity) {
        if (!canAddInput()) {
            return false;
        }
        ItemStack itemStack = entity.getItem();
        if (itemStack.isEmpty()) {
            return false;
        }
        ItemStack catalyst = getCatalyst();
        if (catalyst.is(Items.ENDER_EYE)) {
            if (itemStack.is(InitItems.TRANSMUTATION_CRYSTAL)) {
                suckOneAndSync(entity, INPUT_SLOT);
                syncTargetTimer(10);
                return true;
            }
            return false;
        }
        if (catalyst.is(InitItems.TRANSMUTATION_CRYSTAL) || catalyst.getItem() instanceof ItemEssenceMetal) {
            return false;
        }
        // AbstractItemTransmutationScroll
        // todo
        return false;
    }

    private boolean tryAddEssenceFromItemEntity(ItemEntity entity) {
        if (!canAddEssence()) {
            return false;
        }
        ItemStack itemStack = entity.getItem();
        if (itemStack.isEmpty()) {
            return false;
        }
        // todo
        return false;
    }

    private void suckOneAndSync(ItemEntity entity, int slot) {
        ItemStack itemStack = entity.getItem();
        ItemStack copy = itemStack.copyWithCount(1);
        items.set(slot, copy);
        // sync
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(slot, copy)))
        );
        if (itemStack.getCount() == 1) {
            entity.setItem(ItemStack.EMPTY);
            entity.discard();
        } else {
            itemStack.setCount(itemStack.getCount() - 1);
        }
    }

    private void syncProcessTimer(int processTimer) {
        this.processTimer = processTimer;
        // sync
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                new CrucibleSetProcessTimerPayload(getBlockPos(), processTimer)
        );
    }

    private void syncTargetTimer(int targetTimer) {
        this.targetTimer = targetTimer;
        // sync
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                new CrucibleSetTargetTimerPayload(getBlockPos(), targetTimer)
        );
    }

    private void setItemAndRecordChange(int slot, ItemStack itemStack, List<ItemStackWithSlot> itemStackWithSlots) {
        items.set(slot, itemStack);
        itemStackWithSlots.add(new ItemStackWithSlot(slot, itemStack));
    }

    public boolean canAddCatalyst() {
        return !isFinish && getCatalyst().isEmpty();
    }

    public boolean canAddInput() {
        return !isFinish && !getCatalyst().isEmpty() && getInput().isEmpty();
    }

    public boolean canAddEssence() {
        return !isFinish && (getCatalyst().is(InitItems.TRANSMUTATION_CRYSTAL) || getCatalyst().getItem() instanceof ItemEssenceMetal || getCatalyst().getItem() instanceof AbstractItemTransmutationScroll);
    }

    private void clearInputAndSetAllOutput() {
        ArrayList<ItemStackWithSlot> itemStackWithSlotsUpdate = new ArrayList<>();
        if (getCatalyst().is(Items.ENDER_EYE)) {
            if (getInput().is(InitItems.TRANSMUTATION_CRYSTAL)) {
                RandomSource randomSource = level.getRandom();
                if (randomSource.nextFloat() < 0.01f) {
                    setItemAndRecordChange(OUTPUT_SLOT, InitItems.PANDEMONIUM.toStack(), itemStackWithSlotsUpdate);
                } else {
                    setItemAndRecordChange(OUTPUT_SLOT, InitItems.ESSENCE_METAL_ITEMS[randomSource.nextInt(InitItems.ESSENCE_METAL_ITEMS.length - 1)].toStack(), itemStackWithSlotsUpdate);
                }
            }
        } else if (getCatalyst().is(InitItems.TRANSMUTATION_CRYSTAL)) {
            // todo
        } else if (getCatalyst().getItem() instanceof ItemEssenceMetal) {
            // todo
        } else {
            // AbstractItemTransmutationScroll
            // todo
        }
        setItemAndRecordChange(INPUT_SLOT, ItemStack.EMPTY, itemStackWithSlotsUpdate);
        isFinish = true;
        // sync
        syncProcessTimer(0);
        syncTargetTimer(0);
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                new CrucibleSetItemPayload(getBlockPos(), itemStackWithSlotsUpdate),
                new CrucibleSetFinishPayload(getBlockPos(), true)
        );
        setChanged();
    }

    /**
     * to be removed
     */
    @Deprecated
    public NonNullList<ItemStack> getItems() {
        return items;
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
        // sync
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(CATALYST_SLOT, ItemStack.EMPTY)))
        );
        setChanged();
    }

    public void takeInput(Player player) {
        player.getInventory().placeItemBackInInventory(getInput(), true);
        items.set(INPUT_SLOT, ItemStack.EMPTY);
        // sync
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                new CrucibleSetItemPayload(getBlockPos(), List.of(new ItemStackWithSlot(INPUT_SLOT, ItemStack.EMPTY)))
        );
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
        // sync
        PacketDistributor.sendToPlayersTrackingChunk(
                (ServerLevel) level,
                new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                new CrucibleSetItemPayload(getBlockPos(), itemStackWithSlots)
        );
        setChanged();
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
                new ChunkPos(SectionPos.blockToSectionCoord(getBlockPos().getX()), SectionPos.blockToSectionCoord(getBlockPos().getZ())),
                new CrucibleSetItemPayload(getBlockPos(), itemStackWithSlots),
                new CrucibleSetFinishPayload(getBlockPos(), false)
        );
        setChanged();
    }

    public void setSelectedSlot(int selectedSlot) {
        // todo 需要一个配方等级接口

    }

    public void addEssence(ItemStack essence) {

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

//    public void notifyChanged() {
//        setChanged();
//        if (level != null) {
//            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
//        }
//    }
}
