package tech.alexnijjar.golemoverhaul.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class ClayGolemStatueBlock extends HorizontalDirectionalBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape SHAPE_NORTH = Stream.of(
        Block.box(9.6, 0, 3, 12.6, 3, 6),
        Block.box(3.5, 0, 3, 6.5, 3, 6),
        Block.box(4, 0, 4, 12, 9, 12),
        Block.box(2.5, 0, 6, 4.5, 6, 10),
        Block.box(11.5, 0, 6, 13.5, 6, 10),
        Block.box(6.5, 2, 2.2, 9.5, 7, 4.2)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_EAST = Stream.of(
        Block.box(10, 0, 9.6, 13, 3, 12.6),
        Block.box(10, 0, 3.5, 13, 3, 6.5),
        Block.box(4, 0, 4, 12, 9, 12),
        Block.box(6, 0, 2.5, 10, 6, 4.5),
        Block.box(6, 0, 11.5, 10, 6, 13.5),
        Block.box(11.8, 2, 6.5, 13.8, 7, 9.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_SOUTH = Stream.of(
        Block.box(3.4, 0, 10, 6.4, 3, 13),
        Block.box(9.5, 0, 10, 12.5, 3, 13),
        Block.box(4, 0, 4, 12, 9, 12),
        Block.box(11.5, -0, 6, 13.5, 6, 10),
        Block.box(2.5, -0, 6, 4.5, 6, 10),
        Block.box(6.5, 2, 11.8, 9.5, 7, 13.8)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public static final VoxelShape SHAPE_WEST = Stream.of(
        Block.box(3, 0, 3.4, 6, 3, 6.4),
        Block.box(3, 0, 9.5, 6, 3, 12.5),
        Block.box(4, 0, 4, 12, 9, 12),
        Block.box(6, 0, 11.5, 10, 6, 13.5),
        Block.box(6, 0, 2.5, 10, 6, 4.5),
        Block.box(2.2, 2, 6.5, 4.2, 7, 9.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public ClayGolemStatueBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
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
