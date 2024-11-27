package juniper.elemental.blocks;

import java.util.EnumMap;
import java.util.Map;

import juniper.elemental.init.ElementalBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;

public class ConduitSignalReactions {
    public static final Map<ConduitSignal, Map<ConduitSignal, ConduitReaction>> REACTIONS;
    public static final Map<ConduitSignal, ConduitSignal> TRANSITIONS = new EnumMap<>(
            Map.of(ConduitSignal.COOLDOWN1, ConduitSignal.OFF,
                    ConduitSignal.COOLDOWN2, ConduitSignal.OFF,
                    ConduitSignal.EARTH1, ConduitSignal.COOLDOWN1,
                    ConduitSignal.EARTH2, ConduitSignal.COOLDOWN1,
                    ConduitSignal.WATER1, ConduitSignal.COOLDOWN1,
                    ConduitSignal.WATER2, ConduitSignal.COOLDOWN1,
                    ConduitSignal.AIR1, ConduitSignal.COOLDOWN1,
                    ConduitSignal.AIR2, ConduitSignal.COOLDOWN1,
                    ConduitSignal.FIRE1, ConduitSignal.COOLDOWN1,
                    ConduitSignal.FIRE2, ConduitSignal.COOLDOWN1));
    static {
        Map<ConduitSignal, Map<ConduitSignal, ConduitReaction>> allReactions = new EnumMap<>(ConduitSignal.class);

        ConduitReaction waterEarthReaction = (world, pos) -> {
            world.setBlockState(pos, ElementalBlocks.OVERGROWN_CONDUIT.getDefaultState());
            return null;
        };
        ConduitReaction airEarthReaction = (world, pos) -> {
            for (BlockPos pos2 : BlockPos.iterateRandomly(world.getRandom(), 5, pos, 3)) {
                ItemPlacementContext ctx = new ItemPlacementContext(world, null, null,
                        new ItemStack(ElementalBlocks.DUST),
                        new BlockHitResult(Vec3d.ofBottomCenter(pos2), Direction.UP, pos2, false));
                if (world.getBlockState(pos2).canReplace(ctx)) {
                    BlockState state = ElementalBlocks.DUST.getPlacementState(ctx);
                    if (state.canPlaceAt(world, pos2)) {
                        world.setBlockState(pos2, state);
                    }
                }
            }
            if (world.getRandom().nextFloat() < 0.1) {
                world.setBlockState(pos, ElementalBlocks.CLOGGED_CONDUIT.getDefaultState());
            }
            return null;
        };
        ConduitReaction fireAirReaction = (world, pos) -> {
            world.createExplosion(null, pos.getX(), pos.getY(),
                    pos.getZ(), 1, ExplosionSourceType.BLOCK);
            return null;
        };

        // earth
        Map<ConduitSignal, ConduitReaction> reactions = new EnumMap<>(ConduitSignal.class);
        reactions.put(ConduitSignal.OFF, ConduitReaction.basicReaction(ConduitSignal.EARTH1));
        reactions.put(ConduitSignal.COOLDOWN1, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN2));
        reactions.put(ConduitSignal.COOLDOWN2, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN1));
        reactions.put(ConduitSignal.EARTH1, ConduitReaction.basicReaction(ConduitSignal.EARTH2));
        reactions.put(ConduitSignal.EARTH2, ConduitReaction.basicReaction(ConduitSignal.OFF));
        reactions.put(ConduitSignal.WATER1, waterEarthReaction);
        reactions.put(ConduitSignal.WATER2, waterEarthReaction);
        reactions.put(ConduitSignal.AIR1, airEarthReaction);
        reactions.put(ConduitSignal.AIR2, airEarthReaction);
        allReactions.put(ConduitSignal.EARTH1, reactions);
        allReactions.put(ConduitSignal.EARTH2, reactions);
        // water
        reactions = new EnumMap<>(ConduitSignal.class);
        reactions.put(ConduitSignal.OFF, ConduitReaction.basicReaction(ConduitSignal.WATER1));
        reactions.put(ConduitSignal.COOLDOWN1, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN2));
        reactions.put(ConduitSignal.COOLDOWN2, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN1));
        reactions.put(ConduitSignal.WATER1, ConduitReaction.basicReaction(ConduitSignal.WATER2));
        reactions.put(ConduitSignal.WATER2, ConduitReaction.basicReaction(ConduitSignal.OFF));
        reactions.put(ConduitSignal.EARTH1, waterEarthReaction);
        reactions.put(ConduitSignal.EARTH2, waterEarthReaction);
        allReactions.put(ConduitSignal.WATER1, reactions);
        allReactions.put(ConduitSignal.WATER2, reactions);
        // air
        reactions = new EnumMap<>(ConduitSignal.class);
        reactions.put(ConduitSignal.OFF, ConduitReaction.basicReaction(ConduitSignal.AIR1));
        reactions.put(ConduitSignal.COOLDOWN1, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN2));
        reactions.put(ConduitSignal.COOLDOWN2, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN1));
        reactions.put(ConduitSignal.AIR1, ConduitReaction.basicReaction(ConduitSignal.AIR2));
        reactions.put(ConduitSignal.AIR2, ConduitReaction.basicReaction(ConduitSignal.OFF));
        reactions.put(ConduitSignal.FIRE1, fireAirReaction);
        reactions.put(ConduitSignal.FIRE2, fireAirReaction);
        reactions.put(ConduitSignal.EARTH1, airEarthReaction);
        reactions.put(ConduitSignal.EARTH2, airEarthReaction);
        allReactions.put(ConduitSignal.AIR1, reactions);
        allReactions.put(ConduitSignal.AIR2, reactions);
        // fire
        reactions = new EnumMap<>(ConduitSignal.class);
        reactions.put(ConduitSignal.OFF, ConduitReaction.basicReaction(ConduitSignal.FIRE1));
        reactions.put(ConduitSignal.COOLDOWN1, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN2));
        reactions.put(ConduitSignal.COOLDOWN2, ConduitReaction.basicReaction(ConduitSignal.COOLDOWN1));
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
