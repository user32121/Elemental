package juniper.elemental.blocks;

import juniper.elemental.Elemental;
import juniper.elemental.elements.ElementSignal;
import juniper.elemental.elements.ElementReactions;
import juniper.elemental.elements.ElementReactions.ConduitReaction;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public interface ElementHolder {
    public static final EnumProperty<ElementSignal> SIGNAL = EnumProperty.of("signal", ElementSignal.class);

    public default void signalOnBlockAdded(BlockState state, World world, BlockPos pos) {
        if (state.get(SIGNAL).is_transient) {
            world.scheduleBlockTick(pos, state.getBlock(), 2);
        }
    }

    public default void signalScheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(SIGNAL).is_active) {
            for (Direction dir : Direction.values()) {
                // reaction
                BlockPos pos2 = pos.offset(dir);
                BlockState state2 = world.getBlockState(pos2);
                if (!state2.contains(SIGNAL)) {
                    continue;
                }
                ConduitReaction reaction = getSignalReaction(state, state2);
                if (reaction == null) {
                    Elemental.LOGGER.info("{} + {} reaction not implemented", state.get(SIGNAL), state2.get(SIGNAL));
                    continue;
                }
                ElementSignal signal = reaction.performReaction(world, pos2);
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
            world.setBlockState(pos, state.with(SIGNAL, ElementReactions.TRANSITIONS.get(state.get(SIGNAL))));
        }
    }

    public default ConduitReaction getSignalReaction(BlockState state, BlockState state2) {
        if (state.getBlock() instanceof CatalystBlock) {
            return ElementReactions.CATALYST_REACTIONS.get(state.get(SIGNAL)).get(state2.get(SIGNAL));
        }
        return ElementReactions.DEFAULT_REACTIONS.get(state.get(SIGNAL)).get(state2.get(SIGNAL));
    }
}
