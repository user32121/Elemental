package juniper.elemental.blocks;

import juniper.elemental.Elemental;
import juniper.elemental.blocks.ConduitSignalReactions.ConduitReaction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Waterloggable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class ConduitBlock extends TriAxisBlock implements Waterloggable {
    public static final EnumProperty<ConduitSignal> SIGNAL = EnumProperty.of("signal", ConduitSignal.class);

    public ConduitBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(SIGNAL, ConduitSignal.OFF).with(Properties.WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SIGNAL, Properties.WATERLOGGED);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);
        if (state.get(SIGNAL).is_active) {
            for (Direction dir : Direction.values()) {
                // reaction
                BlockPos pos2 = pos.offset(dir);
                BlockState state2 = world.getBlockState(pos2);
                if (!state2.contains(SIGNAL)) {
                    continue;
                }
                ConduitReaction reaction = ConduitSignalReactions.REACTIONS.get(state.get(SIGNAL))
                        .get(state2.get(SIGNAL));
                if (reaction == null) {
                    Elemental.LOGGER.info("{} + {} reaction not implemented", state.get(SIGNAL), state2.get(SIGNAL));
                    continue;
                }
                ConduitSignal signal = reaction.performReaction(world, pos2);
                if (signal == null) {
                    continue;
                }
                state2 = world.getBlockState(pos2);
                if (state2.contains(SIGNAL)) {
                    world.setBlockState(pos2, state2.with(SIGNAL, signal));
                }
            }
        }
        if (state.get(SIGNAL).is_transient) {
            world.setBlockState(pos, state.with(SIGNAL, ConduitSignalReactions.TRANSITIONS.get(state.get(SIGNAL))));
        }
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (state.get(SIGNAL).is_transient) {
            world.scheduleBlockTick(pos, state.getBlock(), 2);
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
        boolean bl = fluidState.getFluid() == Fluids.WATER;
        return super.getPlacementState(ctx).with(Properties.WATERLOGGED, bl);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, WorldView world, ScheduledTickView tickView,
            BlockPos pos, Direction direction, BlockPos neighborPos, BlockState neighborState, Random random) {
        if (state.get(Properties.WATERLOGGED).booleanValue()) {
            tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }
        return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState,
                random);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        if (state.get(Properties.WATERLOGGED).booleanValue()) {
            return Fluids.WATER.getStill(false);
        }
        return super.getFluidState(state);
    }
}
