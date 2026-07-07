package com.linngdu664.transmutatoria.block;

import com.linngdu664.transmutatoria.block.entity.AlchemistStorageBoxBlockEntity;
import com.linngdu664.transmutatoria.init.InitBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class AlchemistStorageBoxBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape NORTH_SOUTH_SHAPE = Block.box(1.9, 0.0, 3.9, 14.1, 5.2, 12.1);
    private static final VoxelShape EAST_WEST_SHAPE = Block.box(3.9, 0.0, 1.9, 12.1, 5.2, 14.1);

    private final MapCodec<AlchemistStorageBoxBlock> codec;
    private final int boxState;

    public AlchemistStorageBoxBlock(Identifier id, int boxState) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .strength(2.5F)
                .noOcclusion());
        this.boxState = boxState;
        this.codec = MapCodec.unit(this);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public int getBoxState() {
        return boxState;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return codec;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AlchemistStorageBoxBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        return level.isClientSide()
                ? createTickerHelper(
                        type,
                        InitBlocks.ALCHEMIST_STORAGE_BOX_BLOCK_ENTITY.get(),
                        AlchemistStorageBoxBlockEntity::lidAnimateTick)
                : null;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof AlchemistStorageBoxBlockEntity storageBox) {
            storageBox.recheckOpen();
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (level.getBlockEntity(pos) instanceof AlchemistStorageBoxBlockEntity storageBox) {
            storageBox.applyComponentsFromItemStack(stack);
            storageBox.setChanged();
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return interact(level, pos, player);
    }

    @Override
    protected InteractionResult useItemOn(
            ItemStack stack,
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        return player.isShiftKeyDown() ? interact(level, pos, player) : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private InteractionResult interact(Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(pos) instanceof AlchemistStorageBoxBlockEntity storageBox)) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            ItemStack stack = storageBox.createItemStack();
            if (!player.getInventory().add(stack)) {
                return InteractionResult.FAIL;
            }
            level.removeBlock(pos, false);
            return InteractionResult.SUCCESS_SERVER;
        }
        player.openMenu(storageBox);
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        BlockEntity blockEntity = params.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        return blockEntity instanceof AlchemistStorageBoxBlockEntity storageBox
                ? List.of(storageBox.createItemStack())
                : List.of(new ItemStack(this));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(FACING).getAxis() == Direction.Axis.X ? EAST_WEST_SHAPE : NORTH_SOUTH_SHAPE;
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }
}
