package com.linngdu664.transmutatoria.item;

import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.init.InitDataComponents;
import com.linngdu664.transmutatoria.inventory.AlchemistStorageBoxMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AlchemistStorageBoxItem extends Item {
    private final int state;

    public AlchemistStorageBoxItem(Identifier id, int state) {
        super(new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .stacksTo(1));
        this.state = state;
    }

    public int getBoxState() {
        return state;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            player.openMenu(new SimpleMenuProvider(
                    (containerId, playerInventory, p) ->
                            new AlchemistStorageBoxMenu(containerId, playerInventory, stack, state),
                    stack.getHoverName()));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);

        if (blockState.is(InitBlocks.TRANSMUTATION_CRUCIBLE)) {
            ItemStack stack = context.getItemInHand();
            if (!level.isClientSide()) {
                int rotation = stack.getOrDefault(InitDataComponents.ROTATION, 0);
                ItemContainerContents container = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
                if (rotation < container.getSlots()) {
                    ItemStack slotStack = container.getStackInSlot(rotation);
                    if (!slotStack.isEmpty()) {
                        ItemStack toDrop = slotStack.copyWithCount(1);
                        NonNullList<ItemStack> items = NonNullList.withSize(container.getSlots(), ItemStack.EMPTY);
                        container.copyInto(items);
                        items.get(rotation).shrink(1);
                        stack.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));

                        Vec3 dropPos = pos.getCenter().add(0, 0.5, 0);
                        ItemEntity itemEntity = new ItemEntity(level, dropPos.x, dropPos.y, dropPos.z, toDrop,0,0,0);
                        level.addFreshEntity(itemEntity);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }

        return use(level, context.getPlayer(), context.getHand());
    }
}
