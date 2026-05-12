package com.linngdu664.transmutatoria.block;

import com.linngdu664.transmutatoria.init.InitBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class BlockEntityTransmutationCrucible extends BlockEntity implements MenuProvider {
    public static final int SLOT_COUNT = 2;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public BlockEntityTransmutationCrucible(BlockPos pos, BlockState state) {
        super(InitBlocks.TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.transmutatoria.transmutation_crucible");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new MenuTransmutationCrucible(containerId, playerInventory, this);
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items, true);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, items);
    }

    public void notifyChanged() {
        setChanged();
        Level lvl = level;
        if (lvl != null) {
            lvl.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }
}
