package tech.alexnijjar.golemoverhaul.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractCandleBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import tech.alexnijjar.golemoverhaul.common.entities.candle.CandleGolem;
import tech.alexnijjar.golemoverhaul.common.registry.ModEntityTypes;

import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class CandleGolemBlock extends AbstractCandleBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final Vec3 OFFSET = new Vec3(0.5, 0.5625, 0.5);

    public static final VoxelShape SHAPE_NORTH = Stream.of(
        Block.box(5, 5, 5, 11, 8, 11),
        Block.box(5.5, 0, 5.5, 10.5, 5, 10.5),
        Block.box(10.5, 0, 6.5, 11.5, 3, 9.5),
        Block.box(4.5, 0, 6.5, 5.5, 3, 9.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_EAST = Stream.of(
        Block.box(5, 5, 5, 11, 8, 11),
        Block.box(5.5, 0, 5.5, 10.5, 5, 10.5),
        Block.box(6.5, 0, 10.5, 9.5, 3, 11.5),
        Block.box(6.5, 0, 4.5, 9.5, 3, 5.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_SOUTH = Stream.of(
        Block.box(5, 5, 5, 11, 8, 11),
        Block.box(5.5, 0, 5.5, 10.5, 5, 10.5),
        Block.box(4.5, 0, 6.5, 5.5, 3, 9.5),
        Block.box(10.5, 0, 6.5, 11.5, 3, 9.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_WEST = Stream.of(
        Block.box(5, 5, 5, 11, 8, 11),
        Block.box(5.5, 0, 5.5, 10.5, 5, 10.5),
        Block.box(6.5, 0, 4.5, 9.5, 3, 5.5),
        Block.box(6.5, 0, 10.5, 9.5, 3, 11.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public CandleGolemBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(LIT, false)
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, WATERLOGGED);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (level.isClientSide()) return;
        if (level.hasNeighborSignal(pos)) {
            spawnGolem(level, pos);
            level.destroyBlock(pos, false);
        }
    }

    @Override
    protected Iterable<Vec3> getParticleOffsets(BlockState state) {
        return List.of(OFFSET);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        var stack = player.getItemInHand(hand);
        if (!state.getValue(WATERLOGGED) && !state.getValue(LIT) && !stack.isEmpty()) {
            boolean isFlintAndSteel = stack.is(Items.FLINT_AND_STEEL);
            if (isFlintAndSteel || stack.is(Items.FIRE_CHARGE)) {
                if (!level.isClientSide()) {
                    level.setBlockAndUpdate(pos, state.setValue(LIT, true));
                    level.playSound(null, pos, isFlintAndSteel ? SoundEvents.FLINTANDSTEEL_USE : SoundEvents.FIRECHARGE_USE, player.getSoundSource(), 1.0f, level.random.nextFloat() * 0.4f + 0.8f);
                    if (isFlintAndSteel) {
                        stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
                    }
                }
                return InteractionResult.sidedSuccess(level.isClientSide());
            }
        }

        spawnGolem(level, pos);
        level.destroyBlock(pos, false);
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    private void spawnGolem(Level level, BlockPos pos) {
        var golem = createGolem(level, level.random.nextInt(3));
        golem.setLit(level.getBlockState(pos).getValue(LIT));
        golem.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        level.addFreshEntity(golem);
    }

    public static CandleGolem createGolem(Level level, int id) {
        return switch (id) {
            case 1 -> ModEntityTypes.CANDLE_GOLEM.get().create(level);
            case 2 -> ModEntityTypes.MEDIUM_CANDLE_GOLEM.get().create(level);
            default -> ModEntityTypes.MELTED_CANDLE_GOLEM.get().create(level);
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite())
            .setValue(WATERLOGGED, fluidState.getType().equals(Fluids.WATER));
    }
}
