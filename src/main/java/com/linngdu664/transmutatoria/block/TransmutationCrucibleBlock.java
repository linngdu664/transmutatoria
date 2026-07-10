package com.linngdu664.transmutatoria.block;

import com.linngdu664.transmutatoria.block.entity.TransmutationCrucibleBlockEntity;
import com.linngdu664.transmutatoria.init.InitBlocks;
import com.linngdu664.transmutatoria.init.InitItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.FluidStacksResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidUtil;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class TransmutationCrucibleBlock extends HorizontalDirectionalBlock implements EntityBlock {
    protected static final VoxelShape NORTH_AABB = makeShape();
    protected static final VoxelShape SOUTH_AABB = rotateShape(Direction.SOUTH, Direction.NORTH, NORTH_AABB);
    protected static final VoxelShape EAST_AABB = rotateShape(Direction.SOUTH, Direction.EAST, NORTH_AABB);
    protected static final VoxelShape WEST_AABB = rotateShape(Direction.NORTH, Direction.EAST, NORTH_AABB);
    protected static final MapCodec<TransmutationCrucibleBlock> CODEC = simpleCodec(TransmutationCrucibleBlock::new);

    public TransmutationCrucibleBlock(Identifier id) {
        super(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(2.0F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    public TransmutationCrucibleBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TransmutationCrucibleBlockEntity crucible) {
                if (crucible.hasAnyOutput()) {
                    crucible.takeAllOutput(player);
                    return InteractionResult.SUCCESS_SERVER;
                }
                if (player.isShiftKeyDown()) {
                    if (crucible.hasInputEssenceMetals()) {
                        crucible.takeEssenceInput(player);
                        return InteractionResult.SUCCESS_SERVER;
                    }
                    if (crucible.hasInput()) {
                        crucible.takeInput(player);
                        return InteractionResult.SUCCESS_SERVER;
                    }
                    if (crucible.hasCatalyst()) {
                        crucible.takeCatalyst(player);
                        return InteractionResult.SUCCESS_SERVER;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof TransmutationCrucibleBlockEntity crucible)) {
            return InteractionResult.FAIL;
        }

        if (stack.is(InitItems.PHILOSOPHERS_STONE)) {
            if (!level.isClientSide()) {
                crucible.adjustPolarityWithPhilosophersStone();
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }

        // todo 特判水桶不满也能倒，如有更好的做法可替换
        if (stack.is(Items.WATER_BUCKET)) {
            try (var tx = Transaction.openRoot()) {
                FluidStacksResourceHandler waterHandler = crucible.getWaterHandler();
                FluidResource resource = FluidResource.of(Fluids.WATER);
                int space = waterHandler.getCapacityAsInt(0, resource) - waterHandler.getAmountAsInt(0);
                if (space > 0) {
                    waterHandler.insert(0, resource, FluidType.BUCKET_VOLUME, tx);
                    tx.commit();
                    if (!player.isCreative()) {
                        stack.shrink(1);
                        player.getInventory().placeItemBackInInventory(new ItemStack(Items.BUCKET), false);
                    }
                    Vec3 position = Vec3.atCenterOf(pos);
                    var soundEvent = resource.getFluidType().getSound(resource.toStack(FluidType.BUCKET_VOLUME), SoundActions.BUCKET_EMPTY);
                    if (soundEvent != null) {
                        level.playSound(null, position.x, position.y, position.z, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }
                    level.gameEvent(player, GameEvent.FLUID_PLACE, position);
                    return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
                }
            }
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (FluidUtil.interactWithFluidHandler(player, hand, level, pos, hit.getDirection(), null)) {
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TransmutationCrucibleBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> type) {
        return type == InitBlocks.TRANSMUTATION_CRUCIBLE_BLOCK_ENTITY.get() ? TransmutationCrucibleBlockEntity::tick : null;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity, InsideBlockEffectApplier effectApplier, boolean isPrecise) {
        if (!level.isClientSide()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof TransmutationCrucibleBlockEntity crucibleEntity) {
                crucibleEntity.entityInside(entity);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        switch (direction) {
            case SOUTH -> {
                return SOUTH_AABB;
            }
            case EAST -> {
                return EAST_AABB;
            }
            case WEST -> {
                return WEST_AABB;
            }
            default -> {
                return NORTH_AABB;
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected @NonNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    private static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.ordinal() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
                    buffer[1] = Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }
        return buffer[0];
    }

    private static VoxelShape makeShape() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Block.box(0, 0, 0, 2, 15, 16), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(2, 0, 0, 16, 15, 2), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(14, 0, 0, 16, 15, 16), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(2, 0, 14, 14, 15, 16), BooleanOp.OR);
        shape = Shapes.join(shape, Block.box(2, 0, 2, 14, 5, 14), BooleanOp.OR);
        return shape;
    }
}
