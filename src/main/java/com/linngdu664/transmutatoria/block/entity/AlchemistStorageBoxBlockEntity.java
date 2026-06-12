package com.linngdu664.transmutatoria.block.entity;

import com.linngdu664.transmutatoria.block.AlchemistStorageBoxBlock;
import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.inventory.AlchemistStorageBoxMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestLidController;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AlchemistStorageBoxBlockEntity extends BlockEntity implements LidBlockEntity, MenuProvider, Nameable {
    private final StorageBoxItemHandler itemHandler = new StorageBoxItemHandler();
    private NonNullList<ItemStack> items = itemHandler.getStacks();
    private final Container menuContainer = new StorageBoxMenuContainer();
    private final ChestLidController lidController = new ChestLidController();
    private LockCode lockKey = LockCode.NO_LOCK;
    private @Nullable Component name;
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
            return player.containerMenu instanceof AlchemistStorageBoxMenu menu && menu.isContainer(menuContainer);
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

    public ItemStacksResourceHandler getItemHandler() {
        return itemHandler;
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

    public void stopOpen(ContainerUser user) {
        if (!isRemoved() && !user.getLivingEntity().isSpectator()) {
            openersCounter.decrementOpeners(user.getLivingEntity(), getLevel(), getBlockPos(), getBlockState());
        }
    }

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
    public Component getName() {
        return name != null ? name : Component.translatable(getBlockState().getBlock().asItem().getDescriptionId());
    }

    @Override
    public Component getDisplayName() {
        return getName();
    }

    @Override
    public @Nullable Component getCustomName() {
        return name;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        if (!lockKey.canUnlock(player)) {
            BaseContainerBlockEntity.sendChestLockedNotifications(getBlockPos().getCenter(), player, getDisplayName());
            return null;
        }
        return new AlchemistStorageBoxMenu(containerId, inventory, menuContainer, getBoxState());
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        lockKey = LockCode.fromTag(input);
        name = parseCustomNameSafe(input, "CustomName");
        NonNullList<ItemStack> loadedItems = NonNullList.withSize(AlchemistStorageBoxMenu.CONTAINER_SLOTS, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, loadedItems);
        itemHandler.replaceStacks(loadedItems);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        lockKey.addToTag(output);
        output.storeNullable("CustomName", ComponentSerialization.CODEC, name);
        ContainerHelper.saveAllItems(output, items, false);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        name = components.get(DataComponents.CUSTOM_NAME);
        lockKey = components.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
        components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.CUSTOM_NAME, name);
        if (!lockKey.equals(LockCode.NO_LOCK)) {
            components.set(DataComponents.LOCK, lockKey);
        }
        components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        output.discard("CustomName");
        output.discard("lock");
        output.discard("Items");
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
    }

    private class StorageBoxItemHandler extends ItemStacksResourceHandler {
        private StorageBoxItemHandler() {
            super(AlchemistStorageBoxMenu.CONTAINER_SLOTS);
        }

        @Override
        public boolean isValid(int index, ItemResource resource) {
            return AlchemistStorageBoxMenu.canPlaceItem(index, resource.toStack(), getBoxState());
        }

        @Override
        protected void onContentsChanged(int index, ItemStack previousContents) {
            setChanged();
        }

        private NonNullList<ItemStack> getStacks() {
            return stacks;
        }

        private void replaceStacks(NonNullList<ItemStack> newStacks) {
            setStacks(newStacks);
            items = stacks;
        }
    }

    private class StorageBoxMenuContainer implements Container {
        @Override
        public int getContainerSize() {
            return AlchemistStorageBoxMenu.CONTAINER_SLOTS;
        }

        @Override
        public boolean isEmpty() {
            return items.stream().allMatch(ItemStack::isEmpty);
        }

        @Override
        public ItemStack getItem(int slot) {
            return items.get(slot);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack result = ContainerHelper.removeItem(items, slot, amount);
            if (!result.isEmpty()) {
                setChanged();
            }
            return result;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ContainerHelper.takeItem(items, slot);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            items.set(slot, stack);
            stack.limitSize(getMaxStackSize(stack));
            setChanged();
        }

        @Override
        public void setChanged() {
            AlchemistStorageBoxBlockEntity.this.setChanged();
        }

        @Override
        public boolean canPlaceItem(int slot, ItemStack stack) {
            return AlchemistStorageBoxMenu.canPlaceItem(slot, stack, getBoxState());
        }

        @Override
        public boolean stillValid(Player player) {
            return Container.stillValidBlockEntity(AlchemistStorageBoxBlockEntity.this, player);
        }

        @Override
        public void startOpen(ContainerUser user) {
            AlchemistStorageBoxBlockEntity.this.startOpen(user);
        }

        @Override
        public void stopOpen(ContainerUser user) {
            AlchemistStorageBoxBlockEntity.this.stopOpen(user);
        }

        @Override
        public void clearContent() {
            items.clear();
            setChanged();
        }
    }
}
