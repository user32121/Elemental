package juniper.elemental.blocks;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class ConduitBlock extends Block {
    public static final BooleanProperty AXIS_X = BooleanProperty.of("axis_x");
    public static final BooleanProperty AXIS_Y = BooleanProperty.of("axis_y");
    public static final BooleanProperty AXIS_Z = BooleanProperty.of("axis_z");
    public static final EnumProperty<ConduitSignal> SIGNAL = EnumProperty.of("signal", ConduitSignal.class);

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
        this.setDefaultState(getDefaultState().with(AXIS_X, false).with(AXIS_Y, false).with(AXIS_Z, false).with(SIGNAL,
                ConduitSignal.OFF));
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
        super.appendProperties(builder);
        builder.add(AXIS_X, AXIS_Y, AXIS_Z, SIGNAL);
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
        BlockState state = super.getPlacementState(ctx);
        return getStateWithConnections(state, ctx.getWorld(), ctx.getBlockPos());
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView,
            BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        state = super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState,
                random);
        return getStateWithConnections(state, world, pos);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.get(SIGNAL).equals(ConduitSignal.ON)) {
            for (Direction dir : Direction.values()) {
                BlockState state2 = world.getBlockState(pos.offset(dir));
                if (state2.contains(SIGNAL) && state2.get(SIGNAL).equals(ConduitSignal.OFF)) {
                    world.setBlockState(pos.offset(dir), state2.with(SIGNAL, ConduitSignal.ON));
                }
            }
            state = state.with(SIGNAL, ConduitSignal.COOLDOWN);
            world.setBlockState(pos, state);
            world.scheduleBlockTick(pos, state.getBlock(), 2);
        } else if (state.get(SIGNAL).equals(ConduitSignal.COOLDOWN)) {
            world.setBlockState(pos, state.with(SIGNAL, ConduitSignal.OFF));
        }
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (state.get(SIGNAL).equals(ConduitSignal.ON)) {
            world.scheduleBlockTick(pos, state.getBlock(), 2);
        }
    }
}
