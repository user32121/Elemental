package juniper.elemental.blocks;

import java.util.EnumMap;
import java.util.Map;

import juniper.elemental.init.ElementalBlocks;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;
import net.minecraft.world.WorldEvents;

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
        ConduitReaction airWaterReaction = (world, pos) -> {
            extinguishFire(world, pos);
            for (Direction dir : Direction.values()) {
                extinguishFire(world, pos.offset(dir));
            }
            return null;
        };
        ConduitReaction fireEarthReaction = (world, pos) -> {
            for (BlockPos pos2 : BlockPos.iterateRandomly(world.getRandom(), 5, pos, 3)) {
                ItemPlacementContext ctx = new ItemPlacementContext(world, null, null, new ItemStack(Blocks.FIRE),
                        new BlockHitResult(Vec3d.ofBottomCenter(pos2), Direction.UP, pos2, false));
                if (world.getBlockState(pos2).canReplace(ctx)) {
                    BlockState state = Blocks.FIRE.getPlacementState(ctx);
                    if (state.canPlaceAt(world, pos2)) {
                        world.setBlockState(pos2, state);
                    }
                }
            }
            if (world.getRandom().nextFloat() < 0.1) {
                world.setBlockState(pos, ElementalBlocks.MELTED_CONDUIT.getDefaultState());
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
        reactions.put(ConduitSignal.FIRE1, fireEarthReaction);
        reactions.put(ConduitSignal.FIRE2, fireEarthReaction);
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
        reactions.put(ConduitSignal.AIR1, airWaterReaction);
        reactions.put(ConduitSignal.AIR2, airWaterReaction);
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
        reactions.put(ConduitSignal.WATER1, airWaterReaction);
        reactions.put(ConduitSignal.WATER2, airWaterReaction);
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
        reactions.put(ConduitSignal.EARTH1, fireEarthReaction);
        reactions.put(ConduitSignal.EARTH2, fireEarthReaction);
        allReactions.put(ConduitSignal.FIRE1, reactions);
        allReactions.put(ConduitSignal.FIRE2, reactions);

        REACTIONS = new EnumMap<>(allReactions);
    }

    private static void extinguishFire(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isIn(BlockTags.FIRE)) {
            world.breakBlock(pos, false, null);
        } else if (AbstractCandleBlock.isLitCandle(blockState)) {
            AbstractCandleBlock.extinguish(null, blockState, world, pos);
        } else if (CampfireBlock.isLitCampfire(blockState)) {
            world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, pos, 0);
            CampfireBlock.extinguish(null, world, pos, blockState);
            world.setBlockState(pos, (BlockState) blockState.with(CampfireBlock.LIT, false));
        }
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
        public ConduitSignal performReaction(ServerWorld world, BlockPos pos);

        public static ConduitReaction basicReaction(ConduitSignal signal) {
            return (world, pos) -> signal;
        }
    }
}
