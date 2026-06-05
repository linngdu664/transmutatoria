package com.linngdu664.transmutatoria.block.entity;

import com.linngdu664.transmutatoria.block.AlchemistStorageBoxBlock;
import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.inventory.AlchemistStorageBoxMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class AlchemistStorageBoxBlockEntity extends BaseContainerBlockEntity implements LidBlockEntity {
    private NonNullList<ItemStack> items = NonNullList.withSize(AlchemistStorageBoxMenu.CONTAINER_SLOTS, ItemStack.EMPTY);
    private final ChestLidController lidController = new ChestLidController();
    private final ContainerOpenersCounter openersCounter = new ContainerOpenersCounter() {
        @Override
        protected void onOpen(Level level, BlockPos pos, BlockState state) {
            playSound(level, pos, SoundEvents.CHEST_OPEN);
        }

        @Override
        protected void onClose(Level level, BlockPos pos, BlockState state) {
            playSound(level, pos, SoundEvents.CHEST_CLOSE);
        }

        @Override
        protected void openerCountChanged(Level level, BlockPos pos, BlockState state, int previousCount, int currentCount) {
            level.blockEvent(pos, state.getBlock(), 1, currentCount);
        }

        @Override
        public boolean isOwnContainer(Player player) {
            return player.containerMenu instanceof AlchemistStorageBoxMenu menu && menu.isContainer(AlchemistStorageBoxBlockEntity.this);
        }
    };

    public AlchemistStorageBoxBlockEntity(BlockPos pos, BlockState state) {
        super(InitBlocks.ALCHEMIST_STORAGE_BOX_BLOCK_ENTITY.get(), pos, state);
    }

    public int getBoxState() {
        return getBlockState().getBlock() instanceof AlchemistStorageBoxBlock storageBox
                ? storageBox.getBoxState()
                : 0;
    }

    public ItemStack createItemStack() {
        ItemStack stack = new ItemStack(getBlockState().getBlock());
        stack.applyComponents(collectComponents());
        return stack;
    }

    public static void lidAnimateTick(
            Level level,
            BlockPos pos,
            BlockState state,
            AlchemistStorageBoxBlockEntity storageBox
    ) {
        storageBox.lidController.tickLid();
    }

    private static void playSound(Level level, BlockPos pos, SoundEvent sound) {
        level.playSound(
                null,
                pos,
                sound,
                SoundSource.BLOCKS,
                0.5F,
                level.getRandom().nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (id == 1) {
            lidController.shouldBeOpen(type > 0);
            return true;
        }
        return super.triggerEvent(id, type);
    }

    @Override
    public void startOpen(ContainerUser user) {
        if (!isRemoved() && !user.getLivingEntity().isSpectator()) {
            openersCounter.incrementOpeners(
                    user.getLivingEntity(),
                    getLevel(),
                    getBlockPos(),
                    getBlockState(),
                    user.getContainerInteractionRange());
        }
    }

    @Override
    public void stopOpen(ContainerUser user) {
        if (!isRemoved() && !user.getLivingEntity().isSpectator()) {
            openersCounter.decrementOpeners(user.getLivingEntity(), getLevel(), getBlockPos(), getBlockState());
        }
    }

    @Override
    public List<ContainerUser> getEntitiesWithContainerOpen() {
        return openersCounter.getEntitiesWithContainerOpen(getLevel(), getBlockPos());
    }

    @Override
    public float getOpenNess(float partialTicks) {
        return lidController.getOpenness(partialTicks);
    }

    public void recheckOpen() {
        if (!isRemoved()) {
            openersCounter.recheckOpeners(getLevel(), getBlockPos(), getBlockState());
        }
    }

    @Override
    public int getContainerSize() {
        return AlchemistStorageBoxMenu.CONTAINER_SLOTS;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return AlchemistStorageBoxMenu.canPlaceItem(slot, stack, getBoxState());
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable(getBlockState().getBlock().asItem().getDescriptionId());
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return new AlchemistStorageBoxMenu(containerId, inventory, this, getBoxState());
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        items = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, items, false);
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
    }
}
