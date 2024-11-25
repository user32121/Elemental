package juniper.elemental.blocks;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class ConduitBlock extends Block {
    public static final BooleanProperty AXIS_X = BooleanProperty.of("axis_x");
    public static final BooleanProperty AXIS_Y = BooleanProperty.of("axis_y");
    public static final BooleanProperty AXIS_Z = BooleanProperty.of("axis_z");

    public static final Map<Axis, BooleanProperty> AXIS_TO_PROPERTY = Maps
            .newEnumMap(ImmutableMap.of(Axis.X, AXIS_X, Axis.Y, AXIS_Y, Axis.Z, AXIS_Z));

    public static final VoxelShape SHAPE_CORE = VoxelShapes.cuboid(3 / 8.0, 3 / 8.0, 3 / 8.0, 5 / 8.0, 5 / 8.0,
            5 / 8.0);
    public static final VoxelShape SHAPE_AXIS_X = VoxelShapes.cuboid(0, 3 / 8.0, 3 / 8.0, 1, 5 / 8.0, 5 / 8.0);
    public static final VoxelShape SHAPE_AXIS_Y = VoxelShapes.cuboid(3 / 8.0, 0, 3 / 8.0, 5 / 8.0, 1, 5 / 8.0);
    public static final VoxelShape SHAPE_AXIS_Z = VoxelShapes.cuboid(3 / 8.0, 3 / 8.0, 0, 5 / 8.0, 5 / 8.0, 1);

    public static final Map<Axis, VoxelShape> AXIS_TO_SHAPE = Maps
            .newEnumMap(ImmutableMap.of(Axis.X, SHAPE_AXIS_X, Axis.Y, SHAPE_AXIS_Y, Axis.Z, SHAPE_AXIS_Z));

    public ConduitBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(AXIS_X, false).with(AXIS_Y, false).with(AXIS_Z, false));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = SHAPE_CORE;
        for (Axis axis : Axis.VALUES) {
            if (state.get(AXIS_TO_PROPERTY.get(axis))) {
                shape = VoxelShapes.union(shape, AXIS_TO_SHAPE.get(axis));
            }
        }
        return shape;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(AXIS_X, AXIS_Y, AXIS_Z);
    }

    private BlockState getStateWithConnections(BlockState baseState, WorldView world, BlockPos pos) {
        BlockState state = baseState;
        for (Axis axis : Axis.VALUES) {
            boolean b = world.getBlockState(pos.offset(axis, 1))
                    .getBlock() instanceof ConduitBlock
                    || world.getBlockState(pos.offset(axis, -1))
                            .getBlock() instanceof ConduitBlock;
            state = state.with(AXIS_TO_PROPERTY.get(axis), b);
        }
        return state;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getStateWithConnections(getDefaultState(), ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView,
            BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        return getStateWithConnections(state, world, pos);
    }
}
