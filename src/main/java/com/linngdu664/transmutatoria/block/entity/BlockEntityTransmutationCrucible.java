package com.linngdu664.transmutatoria.block.entity;

import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.AbstractItemTransmutationScroll;
import com.linngdu664.transmutatoria.item.ItemEssenceMetal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

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

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
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
     * 仅用于提供容器 IO 兼容，不建议在自己的代码中调用该函数
     */
    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    /**
     * 仅用于提供容器 IO 兼容，不建议在自己的代码中调用该函数
     */
    @Override
    public ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(items, slot, count);
        if (!result.isEmpty()) {
            isFinish = hasAnyOutput();
            notifyChanged();
        }
        return result;
    }

    /**
     * 仅用于提供容器 IO 兼容，不建议在自己的代码中调用该函数
     */
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(items, slot);
    }

    /**
     * 仅用于提供容器 IO 兼容，不建议在自己的代码中调用该函数
     */
    @Override
    public void setItem(int slot, ItemStack itemStack) {
        items.set(slot, itemStack);
        notifyChanged();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    /**
     * 仅用于提供容器 IO 兼容，禁止在自己的代码中调用该函数
     */
    @Override
    public void clearContent() {
        // 神了，由于 items 有默认值，这里的 clear 是重置而不是传统 clear
        items.clear();
        isFinish = false;
        notifyChanged();
    }

    public void entityInside(Level level, BlockPos pos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity && entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(SUCK_AABB)) {
            if (tryAddCatalystFromItemEntity(itemEntity) || tryAddInputFromItemEntity(itemEntity) || tryAddEssenceFromItemEntity(itemEntity)) {
                notifyChanged();
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
            trySuckOne(entity, CATALYST_SLOT);
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
                trySuckOne(entity, INPUT_SLOT);
                targetTimer = 10;
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

    private void trySuckOne(ItemEntity entity, int slot) {
        ItemStack itemStack = entity.getItem();
        ItemStack copy = itemStack.copyWithCount(1);
        items.set(slot, copy);
        if (itemStack.getCount() == 1) {
            entity.setItem(ItemStack.EMPTY);
            entity.discard();
        } else {
            itemStack.setCount(itemStack.getCount() - 1);
        }
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
        if (getCatalyst().is(Items.ENDER_EYE)) {
            if (getInput().is(InitItems.TRANSMUTATION_CRYSTAL)) {
                RandomSource randomSource = level.getRandom();
                if (randomSource.nextFloat() < 0.01f) {
                    items.set(OUTPUT_SLOT, InitItems.PANDEMONIUM.toStack());
                } else {
                    items.set(OUTPUT_SLOT, InitItems.ESSENCE_METAL_ITEMS[randomSource.nextInt(InitItems.ESSENCE_METAL_ITEMS.length - 1)].toStack());
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
        items.set(INPUT_SLOT, ItemStack.EMPTY);
    }

    /**
     * to be removed
     */
    @Deprecated
    public NonNullList<ItemStack> getItems() {
        return items;
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

    public void takeCatalyst(Player player) {
        player.getInventory().placeItemBackInInventory(getCatalyst(), true);
        items.set(CATALYST_SLOT, ItemStack.EMPTY);
        notifyChanged();
    }

    public void takeInput(Player player) {
        player.getInventory().placeItemBackInInventory(getInput(), true);
        items.set(INPUT_SLOT, ItemStack.EMPTY);
        notifyChanged();
    }

    public void takeEssenceInput(Player player) {
        Inventory inventory = player.getInventory();
        for (int i = ESSENCE_INPUT_SLOT; i < ESSENCE_OUTPUT_SLOT; i++) {
            if (!items.get(i).isEmpty()) {
                inventory.placeItemBackInInventory(items.get(i));
                items.set(i, ItemStack.EMPTY);
            }
        }
        notifyChanged();
    }

    public void takeAllOutput(Player player) {
        Inventory inventory = player.getInventory();
        for (int i = ESSENCE_OUTPUT_SLOT; i < CATALYST_SLOT; i++) {
            if (!items.get(i).isEmpty()) {
                inventory.placeItemBackInInventory(items.get(i));
                items.set(i, ItemStack.EMPTY);
            }
        }
        inventory.placeItemBackInInventory(getOutput(), true);
        items.set(OUTPUT_SLOT, ItemStack.EMPTY);
        isFinish = false;
        notifyChanged();
    }

    public void setSelectedSlot(int selectedSlot) {
        // todo 需要一个配方等级接口
        if (selectedSlot >= ESSENCE_INPUT_SLOT && selectedSlot < ESSENCE_OUTPUT_SLOT) {
            this.selectedSlot = selectedSlot;
            notifyChanged();
        }
    }

    public void addEssence(ItemStack essence) {
        items.set(selectedSlot, essence.copyWithCount(1));
        // todo 需要一个配方等级接口
        if (selectedSlot < ESSENCE_OUTPUT_SLOT - 1) {
            selectedSlot++;
        }
        notifyChanged();
    }

    public void notifyChanged() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if (level.isClientSide() || !(blockEntity instanceof BlockEntityTransmutationCrucible crucible)) {
            return;
        }
        if (crucible.targetTimer > 0) {
            crucible.processTimer++;
            if (crucible.processTimer >= crucible.targetTimer) {
                crucible.clearInputAndSetAllOutput();
                crucible.processTimer = 0;
                crucible.targetTimer = 0;
            }
            crucible.notifyChanged();
        }
    }
}
