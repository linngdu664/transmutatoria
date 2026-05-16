package com.linngdu664.transmutatoria.block.entity;

import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.init.InitItems;
import com.linngdu664.transmutatoria.item.ItemEssenceMetal;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
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

public class BlockEntityTransmutationCrucible extends BlockEntity {
    private static final AABB SUCK_AABB = Block.column(16.0F, 11.0F, 32.0F).toAabbs().get(0);
    public static final int SLOT_COUNT = 26;
    private static final int CATALYST_SLOT = 24;
    private static final int OUTPUT_SLOT = 25;
    // 0~23 是金属输入，24 是催化剂，25 是产物
    private final NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);

    public BlockEntityTransmutationCrucible(BlockPos pos, BlockState state) {
        super(InitBlocks.TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        ContainerHelper.saveAllItems(output, items, true);
        IntArrayList list = new IntArrayList();
        for (int i = 0; i < SLOT_COUNT; i++) {
            if (items.get(i).isEmpty()) {
                list.add(i);
            }
        }
        output.store("EmptySlots", Codec.INT_STREAM, list.intStream());
    }

    @Override
    public void loadAdditional(ValueInput input) {
        ContainerHelper.loadAllItems(input, items);
        input.read("EmptySlots", Codec.INT_STREAM).ifPresent(stream -> stream.forEach(i -> items.set(i, ItemStack.EMPTY)));
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveWithoutMetadata(registries);
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //    @Override
//    public Component getDisplayName() {
//        return Component.translatable("block.transmutatoria.transmutation_crucible");
//    }

//    @Override
//    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
//        return new MenuTransmutationCrucible(containerId, playerInventory, this);
//    }

    public void entityInside(Level level, BlockPos pos, Entity entity) {
        if (entity instanceof ItemEntity itemEntity && entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ()).intersects(SUCK_AABB)) {
            boolean b1 = tryAddCatalyst(itemEntity);
            tryDecomposition(itemEntity);
            if (b1) {
                notifyChanged();
            }
        }
    }

    private boolean tryAddCatalyst(ItemEntity entity) {
        if (!getCatalyst().isEmpty()) {
            return false;
        }
        ItemStack itemStack = entity.getItem();
        if (itemStack.isEmpty()) {
            return false;
        }
        // todo 更多催化剂
        if (itemStack.is(Items.ENDER_EYE) || itemStack.is(InitItems.TRANSMUTATION_CRYSTAL) || itemStack.getItem() instanceof ItemEssenceMetal) {
            ItemStack copy = itemStack.copy();
            copy.setCount(1);
            setCatalyst(copy);
            if (itemStack.getCount() == 1) {
                entity.setItem(ItemStack.EMPTY);
                entity.discard();
            } else {
                itemStack.setCount(itemStack.getCount() - 1);
            }
        }
        return true;
    }

    private void tryDecomposition(ItemEntity entity) {
        if (getCatalyst().getItem() != Items.ENDER_EYE) {
            return;
        }

        ItemStack itemStack = entity.getItem();
        if (itemStack.isEmpty()) {
            return;
        }
        if (itemStack.is(InitItems.TRANSMUTATION_CRYSTAL)) {
            if (itemStack.getCount() == 1) {
                entity.setItem(ItemStack.EMPTY);
                entity.discard();
            } else {
                itemStack.setCount(itemStack.getCount() - 1);
            }

            // todo 有多低的概率出无相源金？
            RandomSource randomSource = level.getRandom();
            if (randomSource.nextFloat() < 0.01f) {
                level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), InitItems.PANDEMONIUM.toStack()));
            } else {
                level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), InitItems.ESSENCE_METAL_ITEMS[randomSource.nextInt(11)].toStack()));
            }
        }
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public boolean hasEssenceMetals() {
        for (int i = 0; i < CATALYST_SLOT; i++) {
            if (!items.get(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public void takeEssenceMetals(Player player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < CATALYST_SLOT; i++) {
            if (!items.get(i).isEmpty()) {
                inventory.placeItemBackInInventory(items.get(i));
                items.set(i, ItemStack.EMPTY);
            }
        }
        notifyChanged();
    }

    public ItemStack getCatalyst() {
        return items.get(CATALYST_SLOT);
    }

    private void setCatalyst(ItemStack catalyst) {
        items.set(CATALYST_SLOT, catalyst);
    }

    public void takeCatalyst(Player player) {
        player.getInventory().placeItemBackInInventory(getCatalyst(), true);
        setCatalyst(ItemStack.EMPTY);
        notifyChanged();
    }

    // todo 之后需要返回卷轴上的物品
    public ItemStack getInput() {
        return ItemStack.EMPTY;
    }

    public ItemStack getOutput() {
        return items.get(OUTPUT_SLOT);
    }

    private void setOutput(ItemStack output) {
        items.set(OUTPUT_SLOT, output);
    }

    public void notifyChanged() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }
}
