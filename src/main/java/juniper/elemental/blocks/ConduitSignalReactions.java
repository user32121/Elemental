package juniper.elemental.blocks;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class ConduitSignalReactions {
    public static final Map<ConduitSignal, Map<ConduitSignal, ConduitReaction>> REACTIONS;
    static {
        Map<ConduitSignal, Map<ConduitSignal, ConduitReaction>> allReactions = new EnumMap<>(ConduitSignal.class);

        ConduitReaction fireAirReaction = (world, pos) -> {
            world.createExplosion(null, pos.getX(), pos.getY(),
                    pos.getZ(), 1, ExplosionSourceType.BLOCK);
            return null;
        };

        // earth
        Map<ConduitSignal, ConduitReaction> reactions = new EnumMap<>(ConduitSignal.class);
        reactions.put(ConduitSignal.OFF, ConduitReaction.basicReaction(ConduitSignal.EARTH1));
        reactions.put(ConduitSignal.COOLDOWN, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN));
        reactions.put(ConduitSignal.EARTH1, ConduitReaction.basicReaction(ConduitSignal.EARTH2));
        reactions.put(ConduitSignal.EARTH2, ConduitReaction.basicReaction(ConduitSignal.OFF));
        allReactions.put(ConduitSignal.EARTH1, reactions);
        allReactions.put(ConduitSignal.EARTH2, reactions);
        // water
        reactions = new EnumMap<>(ConduitSignal.class);
        reactions.put(ConduitSignal.OFF, ConduitReaction.basicReaction(ConduitSignal.WATER1));
        reactions.put(ConduitSignal.COOLDOWN, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN));
        reactions.put(ConduitSignal.WATER1, ConduitReaction.basicReaction(ConduitSignal.WATER2));
        reactions.put(ConduitSignal.WATER2, ConduitReaction.basicReaction(ConduitSignal.OFF));
        allReactions.put(ConduitSignal.WATER1, reactions);
        allReactions.put(ConduitSignal.WATER2, reactions);
        // air
        reactions = new EnumMap<>(ConduitSignal.class);
        reactions.put(ConduitSignal.OFF, ConduitReaction.basicReaction(ConduitSignal.AIR1));
        reactions.put(ConduitSignal.COOLDOWN, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN));
        reactions.put(ConduitSignal.AIR1, ConduitReaction.basicReaction(ConduitSignal.AIR2));
        reactions.put(ConduitSignal.AIR2, ConduitReaction.basicReaction(ConduitSignal.OFF));
        reactions.put(ConduitSignal.FIRE1, fireAirReaction);
        reactions.put(ConduitSignal.FIRE2, fireAirReaction);
        allReactions.put(ConduitSignal.AIR1, reactions);
        allReactions.put(ConduitSignal.AIR2, reactions);
        // fire
        reactions = new EnumMap<>(ConduitSignal.class);
        reactions.put(ConduitSignal.OFF, ConduitReaction.basicReaction(ConduitSignal.FIRE1));
        reactions.put(ConduitSignal.COOLDOWN, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN));
        reactions.put(ConduitSignal.FIRE1, ConduitReaction.basicReaction(ConduitSignal.FIRE2));
        reactions.put(ConduitSignal.FIRE2, ConduitReaction.basicReaction(ConduitSignal.OFF));
        reactions.put(ConduitSignal.AIR1, fireAirReaction);
        reactions.put(ConduitSignal.AIR2, fireAirReaction);
        allReactions.put(ConduitSignal.FIRE1, reactions);
        allReactions.put(ConduitSignal.FIRE2, reactions);

        REACTIONS = new EnumMap<>(allReactions);
    }

    @FunctionalInterface
    public interface ConduitReaction {
        /**
         * Called when an element "moves" onto another one and reacts. The reaction may
         * modify the block containing the reaction, so callers should check that the
         * blockstate is still valid before using the return value.
         * 
         * @return the new signal produced by the reaction, or `null` if the reaction
         *         produces no signal
         */
        public ConduitSignal performReaction(World world, BlockPos pos);

        public static ConduitReaction basicReaction(ConduitSignal signal) {
            return (world, pos) -> signal;
        }
    }
}
