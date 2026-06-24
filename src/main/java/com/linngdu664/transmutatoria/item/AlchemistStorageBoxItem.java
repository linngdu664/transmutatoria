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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class AlchemistStorageBoxItem extends BlockItem {
    private final int state;

    public AlchemistStorageBoxItem(Identifier id, Block block, int state) {
        super(block, new Properties()
                .setId(ResourceKey.create(Registries.ITEM, id))
                .component(DataComponents.CONTAINER, ItemContainerContents.fromItems(
                        NonNullList.withSize(AlchemistStorageBoxMenu.CONTAINER_SLOTS, ItemStack.EMPTY)))
                .stacksTo(1));
        this.state = state;
    }

    public int getBoxState() {
        return state;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            SimpleMenuProvider provider = new SimpleMenuProvider(
                    (containerId, playerInventory, p) ->
                            new AlchemistStorageBoxMenu(containerId, playerInventory, stack, state, hand),
                    stack.getHoverName());
            serverPlayer.openMenu(provider, buffer -> buffer.writeEnum(hand));
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
            int rotation = (stack.getOrDefault(InitDataComponents.ROTATION, 0) + 6) % 12;
            ItemContainerContents container = stack.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY);
            if (rotation < container.getSlots()) {
                ItemStack slotStack = container.getStackInSlot(rotation);
                if (!slotStack.isEmpty()) {
                    if (level.isClientSide()) {
                        level.playLocalSound(pos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.UI, 0.25F, 1.0F, false);
                    } else {
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

        return super.useOn(context);
    }
}
