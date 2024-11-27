package juniper.elemental.blocks;

import juniper.elemental.Elemental;
import juniper.elemental.blocks.ConduitSignalReactions.ConduitReaction;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class ConduitBlock extends TriAxisBlock {
    public static final EnumProperty<ConduitSignal> SIGNAL = EnumProperty.of("signal", ConduitSignal.class);

    public ConduitBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(SIGNAL, ConduitSignal.OFF));
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SIGNAL);
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
}
