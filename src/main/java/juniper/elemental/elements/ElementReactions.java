package juniper.elemental.elements;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import juniper.elemental.blocks.TriAxisBlock;
import juniper.elemental.entities.ReactionCraftingEntity;
import juniper.elemental.init.ElementalBlocks;
import juniper.elemental.init.ElementalEntities;
import net.minecraft.block.AbstractCandleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.World.ExplosionSourceType;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.explosion.AdvancedExplosionBehavior;
import net.minecraft.world.explosion.Explosion;

public class ElementReactions {
    public static final Map<ElementSignal, Map<ElementSignal, ConduitReaction>> DEFAULT_REACTIONS;
    public static final Map<ElementSignal, Map<ElementSignal, ConduitReaction>> CATALYST_REACTIONS;
    public static final Map<ElementSignal, ElementSignal> TRANSITIONS = new EnumMap<>(
            Map.of(ElementSignal.COOLDOWN1, ElementSignal.OFF, ElementSignal.COOLDOWN2, ElementSignal.OFF,
                    ElementSignal.EARTH1, ElementSignal.COOLDOWN1, ElementSignal.EARTH2, ElementSignal.COOLDOWN1,
                    ElementSignal.WATER1, ElementSignal.COOLDOWN1, ElementSignal.WATER2, ElementSignal.COOLDOWN1,
                    ElementSignal.AIR1, ElementSignal.COOLDOWN1, ElementSignal.AIR2, ElementSignal.COOLDOWN1,
                    ElementSignal.FIRE1, ElementSignal.COOLDOWN1, ElementSignal.FIRE2, ElementSignal.COOLDOWN1));
    static {
        Map<ElementSignal, Map<ElementSignal, ConduitReaction>> allReactions = new EnumMap<>(ElementSignal.class);
        ConduitReaction waterEarthReaction = (world, pos) -> {
            float pitch = 0.9f + world.getRandom().nextFloat() * 0.2f;
            world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 3, pitch);
            if (world.getBlockState(pos).getBlock() instanceof TriAxisBlock) {
                world.setBlockState(pos, ElementalBlocks.OVERGROWN_CONDUIT.getDefaultState());
            } else {
                Block.dropStacks(world.getBlockState(pos), world, pos, world.getBlockEntity(pos));
                world.removeBlock(pos, false);
            }
            return ElementSignal.OFF;
        };
        ConduitReaction airEarthReaction = (world, pos) -> {
            float pitch = 0.9f + world.getRandom().nextFloat() * 0.2f;
            world.playSound(null, pos, SoundEvents.ENTITY_WIND_CHARGE_WIND_BURST.value(), SoundCategory.BLOCKS, 1,
                    pitch);
            world.spawnParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20,
                    0.1, 0.1, 0.1, 1);
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
                if (world.getBlockState(pos).getBlock() instanceof TriAxisBlock) {
                    world.setBlockState(pos, ElementalBlocks.CLOGGED_CONDUIT.getDefaultState());
                } else {
                    Block.dropStacks(world.getBlockState(pos), world, pos, world.getBlockEntity(pos));
                    world.removeBlock(pos, false);
                }
            }
            return ElementSignal.OFF;
        };
        ConduitReaction airWaterReaction = (world, pos) -> {
            float pitch = 0.9f + world.getRandom().nextFloat() * 0.2f;
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.1f, pitch);
            world.spawnParticles(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 30, 0.5,
                    0.5, 0.5, 1);
            extinguishFire(world, pos);
            for (BlockPos pos2 : BlockPos.iterate(pos.add(-1, -1, -1), pos.add(1, 1, 1))) {
                extinguishFire(world, pos2);
            }
            List<Entity> entities = world.getOtherEntities(null, Box.of(pos.toCenterPos(), 3, 3, 3));
            for (Entity entity : entities) {
                entity.extinguish();
            }
            if (world.getRandom().nextFloat() < 0.1) {
                if (world.getBlockState(pos).getBlock() instanceof TriAxisBlock) {
                    world.setBlockState(pos, ElementalBlocks.BLOWN_OUT_CONDUIT.getDefaultState());
                } else {
                    Block.dropStacks(world.getBlockState(pos), world, pos, world.getBlockEntity(pos));
                    world.removeBlock(pos, false);
                }
            }
            return ElementSignal.OFF;
        };
        ConduitReaction fireEarthReaction = (world, pos) -> {
            float pitch = 0.9f + world.getRandom().nextFloat() * 0.2f;
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, pitch);
            world.spawnParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20, 0, 0, 0,
                    0.05);
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
                if (world.getBlockState(pos).getBlock() instanceof TriAxisBlock) {
                    world.setBlockState(pos, ElementalBlocks.MELTED_CONDUIT.getDefaultState());
                } else {
                    Block.dropStacks(world.getBlockState(pos), world, pos, world.getBlockEntity(pos));
                    world.removeBlock(pos, false);
                }
            }
            return ElementSignal.OFF;
        };
        ConduitReaction fireWaterReaction = (world, pos) -> {
            float pitch = 0.9f + world.getRandom().nextFloat() * 0.2f;
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, pitch);
            world.spawnParticles(ParticleTypes.WHITE_SMOKE, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 20,
                    0.1, 0.1, 0.1, 1);
            List<Entity> entities = world.getOtherEntities(null, Box.of(pos.toCenterPos(), 10, 10, 10));
            for (Entity entity : entities) {
                float damage = (float) (1 / (1 + entity.getPos().subtract(pos.toCenterPos()).distanceTo(Vec3d.ZERO)));
                entity.damage(world, world.getDamageSources().inFire(), damage);
            }
            if (world.getRandom().nextFloat() < 0.1) {
                if (world.getBlockState(pos).getBlock() instanceof TriAxisBlock) {
                    world.setBlockState(pos, ElementalBlocks.BLOWN_OUT_CONDUIT.getDefaultState());
                } else {
                    Block.dropStacks(world.getBlockState(pos), world, pos, world.getBlockEntity(pos));
                    world.removeBlock(pos, false);
                }
            }
            return ElementSignal.OFF;
        };
        ConduitReaction fireAirReaction = (world, pos) -> {
            world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, ExplosionSourceType.BLOCK);
            return ElementSignal.OFF;
        };
        // earth
        Map<ElementSignal, ConduitReaction> reactions = new EnumMap<>(ElementSignal.class);
        reactions.put(ElementSignal.OFF, ConduitReaction.basicReaction(ElementSignal.EARTH1));
        reactions.put(ElementSignal.COOLDOWN1, ConduitReaction.basicReaction(ElementSignal.COOLDOWN2));
        reactions.put(ElementSignal.COOLDOWN2, ConduitReaction.basicReaction(ElementSignal.COOLDOWN1));
        reactions.put(ElementSignal.EARTH1, ConduitReaction.basicReaction(ElementSignal.EARTH2));
        reactions.put(ElementSignal.EARTH2, ConduitReaction.basicReaction(ElementSignal.OFF));
        reactions.put(ElementSignal.WATER1, waterEarthReaction);
        reactions.put(ElementSignal.WATER2, waterEarthReaction);
        reactions.put(ElementSignal.AIR1, airEarthReaction);
        reactions.put(ElementSignal.AIR2, airEarthReaction);
        reactions.put(ElementSignal.FIRE1, fireEarthReaction);
        reactions.put(ElementSignal.FIRE2, fireEarthReaction);
        allReactions.put(ElementSignal.EARTH1, reactions);
        allReactions.put(ElementSignal.EARTH2, reactions);
        // water
        reactions = new EnumMap<>(ElementSignal.class);
        reactions.put(ElementSignal.OFF, ConduitReaction.basicReaction(ElementSignal.WATER1));
        reactions.put(ElementSignal.COOLDOWN1, ConduitReaction.basicReaction(ElementSignal.COOLDOWN2));
        reactions.put(ElementSignal.COOLDOWN2, ConduitReaction.basicReaction(ElementSignal.COOLDOWN1));
        reactions.put(ElementSignal.WATER1, ConduitReaction.basicReaction(ElementSignal.WATER2));
        reactions.put(ElementSignal.WATER2, ConduitReaction.basicReaction(ElementSignal.OFF));
        reactions.put(ElementSignal.EARTH1, waterEarthReaction);
        reactions.put(ElementSignal.EARTH2, waterEarthReaction);
        reactions.put(ElementSignal.AIR1, airWaterReaction);
        reactions.put(ElementSignal.AIR2, airWaterReaction);
        reactions.put(ElementSignal.FIRE1, fireWaterReaction);
        reactions.put(ElementSignal.FIRE2, fireWaterReaction);
        allReactions.put(ElementSignal.WATER1, reactions);
        allReactions.put(ElementSignal.WATER2, reactions);
        // air
        reactions = new EnumMap<>(ElementSignal.class);
        reactions.put(ElementSignal.OFF, ConduitReaction.basicReaction(ElementSignal.AIR1));
        reactions.put(ElementSignal.COOLDOWN1, ConduitReaction.basicReaction(ElementSignal.COOLDOWN2));
        reactions.put(ElementSignal.COOLDOWN2, ConduitReaction.basicReaction(ElementSignal.COOLDOWN1));
        reactions.put(ElementSignal.AIR1, ConduitReaction.basicReaction(ElementSignal.AIR2));
        reactions.put(ElementSignal.AIR2, ConduitReaction.basicReaction(ElementSignal.OFF));
        reactions.put(ElementSignal.FIRE1, fireAirReaction);
        reactions.put(ElementSignal.FIRE2, fireAirReaction);
        reactions.put(ElementSignal.EARTH1, airEarthReaction);
        reactions.put(ElementSignal.EARTH2, airEarthReaction);
        reactions.put(ElementSignal.WATER1, airWaterReaction);
        reactions.put(ElementSignal.WATER2, airWaterReaction);
        allReactions.put(ElementSignal.AIR1, reactions);
        allReactions.put(ElementSignal.AIR2, reactions);
        // fire
        reactions = new EnumMap<>(ElementSignal.class);
        reactions.put(ElementSignal.OFF, ConduitReaction.basicReaction(ElementSignal.FIRE1));
        reactions.put(ElementSignal.COOLDOWN1, ConduitReaction.basicReaction(ElementSignal.COOLDOWN2));
        reactions.put(ElementSignal.COOLDOWN2, ConduitReaction.basicReaction(ElementSignal.COOLDOWN1));
        reactions.put(ElementSignal.FIRE1, ConduitReaction.basicReaction(ElementSignal.FIRE2));
        reactions.put(ElementSignal.FIRE2, ConduitReaction.basicReaction(ElementSignal.OFF));
        reactions.put(ElementSignal.AIR1, fireAirReaction);
        reactions.put(ElementSignal.AIR2, fireAirReaction);
        reactions.put(ElementSignal.EARTH1, fireEarthReaction);
        reactions.put(ElementSignal.EARTH2, fireEarthReaction);
        reactions.put(ElementSignal.WATER1, fireWaterReaction);
        reactions.put(ElementSignal.WATER2, fireWaterReaction);
        allReactions.put(ElementSignal.FIRE1, reactions);
        allReactions.put(ElementSignal.FIRE2, reactions);
        DEFAULT_REACTIONS = new EnumMap<>(allReactions);
        //TODO catalyst reactions
        waterEarthReaction = (world, pos) -> {
            float pitch = 0.9f + world.getRandom().nextFloat() * 0.2f;
            world.playSound(null, pos, SoundEvents.ITEM_BONE_MEAL_USE, SoundCategory.BLOCKS, 3, pitch);
            for (BlockPos pos2 : BlockPos.iterateRandomly(world.getRandom(), 2, pos, 3)) {
                BlockState state2 = world.getBlockState(pos2);
                if (state2.isOf(Blocks.DIRT) || state2.isOf(Blocks.GRASS_BLOCK)) {
                    world.setBlockState(pos2, ElementalBlocks.RICH_SOIL.getDefaultState());
                }
            }
            return ElementSignal.OFF;
        };
        airWaterReaction = (world, pos) -> {
            float pitch = 0.9f + world.getRandom().nextFloat() * 0.2f;
            world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_SPLASH, SoundCategory.BLOCKS, 0.5f, pitch);
            world.spawnParticles(ParticleTypes.SPLASH, pos.getX() + 0.5, pos.getY() + 0.5 + 2.5, pos.getZ() + 0.5, 100, 0.1, 5, 0.1, 1);
            if (world.isRaining() || world.isThundering()) {
                world.setWeather(world.getRandom().nextBetween(1 * 20 * 60, 2 * 20 * 60), 0, false, false);
            } else {
                world.setWeather(0, world.getRandom().nextBetween(1 * 20 * 60, 2 * 20 * 60), true, false);
            }
            return ElementSignal.OFF;
        };
        fireEarthReaction = (world, pos) -> {
            for (Direction dir : Direction.values()) {
                BlockPos targetPos = pos.offset(dir);
                if (world.getBlockState(targetPos).isReplaceable()) {
                    float pitch = 0.9f + world.getRandom().nextFloat() * 0.2f;
                    world.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY_LAVA, SoundCategory.BLOCKS, 1, pitch);
                    world.setBlockState(targetPos, Blocks.LAVA.getDefaultState());
                    break;
                }
            }
            return ElementSignal.OFF;
        };
        fireAirReaction = (world, pos) -> {
            world.createExplosion(null, Explosion.createDamageSource(world, null), new AdvancedExplosionBehavior(false, true, Optional.empty(), Optional.empty()), pos.toCenterPos(), 5, false,
                    ExplosionSourceType.BLOCK);
            return ElementSignal.OFF;
        };
        airEarthReaction = craftingReaction(ElementalEntities.REACTION_CRAFTING_AIR_EARTH);
        fireWaterReaction = craftingReaction(ElementalEntities.REACTION_CRAFTING_FIRE_WATER);
        //earth
        reactions = new EnumMap<>(allReactions.get(ElementSignal.EARTH1));
        reactions.put(ElementSignal.WATER1, waterEarthReaction);
        reactions.put(ElementSignal.WATER2, waterEarthReaction);
        reactions.put(ElementSignal.FIRE1, fireEarthReaction);
        reactions.put(ElementSignal.FIRE2, fireEarthReaction);
        reactions.put(ElementSignal.AIR1, airEarthReaction);
        reactions.put(ElementSignal.AIR2, airEarthReaction);
        allReactions.put(ElementSignal.EARTH1, reactions);
        allReactions.put(ElementSignal.EARTH2, reactions);
        //water
        reactions = new EnumMap<>(allReactions.get(ElementSignal.WATER1));
        reactions.put(ElementSignal.EARTH1, waterEarthReaction);
        reactions.put(ElementSignal.EARTH2, waterEarthReaction);
        reactions.put(ElementSignal.AIR1, airWaterReaction);
        reactions.put(ElementSignal.AIR2, airWaterReaction);
        reactions.put(ElementSignal.FIRE1, fireWaterReaction);
        reactions.put(ElementSignal.FIRE2, fireWaterReaction);
        allReactions.put(ElementSignal.WATER1, reactions);
        allReactions.put(ElementSignal.WATER2, reactions);
        //air
        reactions = new EnumMap<>(allReactions.get(ElementSignal.AIR1));
        reactions.put(ElementSignal.WATER1, airWaterReaction);
        reactions.put(ElementSignal.WATER2, airWaterReaction);
        reactions.put(ElementSignal.FIRE1, fireAirReaction);
        reactions.put(ElementSignal.FIRE2, fireAirReaction);
        reactions.put(ElementSignal.EARTH1, airEarthReaction);
        reactions.put(ElementSignal.EARTH2, airEarthReaction);
        allReactions.put(ElementSignal.AIR1, reactions);
        allReactions.put(ElementSignal.AIR2, reactions);
        //fire
        reactions = new EnumMap<>(allReactions.get(ElementSignal.FIRE1));
        reactions.put(ElementSignal.EARTH1, fireEarthReaction);
        reactions.put(ElementSignal.EARTH2, fireEarthReaction);
        reactions.put(ElementSignal.AIR1, fireAirReaction);
        reactions.put(ElementSignal.AIR2, fireAirReaction);
        reactions.put(ElementSignal.WATER1, fireWaterReaction);
        reactions.put(ElementSignal.WATER2, fireWaterReaction);
        allReactions.put(ElementSignal.FIRE1, reactions);
        allReactions.put(ElementSignal.FIRE2, reactions);
        CATALYST_REACTIONS = new EnumMap<>(allReactions);
    }

    private static ConduitReaction craftingReaction(EntityType<ReactionCraftingEntity> craftingEntity) {
        return (world, pos) -> {
            List<ReactionCraftingEntity> entities = world.getEntitiesByType(craftingEntity, new Box(pos.up()), Entity::isAlive);
            ReactionCraftingEntity entity;
            if (entities.isEmpty()) {
                entity = new ReactionCraftingEntity(craftingEntity, world);
                entity.setPosition(pos.up().toBottomCenterPos());
                world.spawnEntity(entity);
            } else {
                entity = entities.get(0);
            }
            entity.incrementCraftProgress();
            return ElementSignal.OFF;
        };
    }

    private static void extinguishFire(World world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        if (blockState.isIn(BlockTags.FIRE)) {
            world.breakBlock(pos, false, null);
            float pitch = 0.9f + world.getRandom().nextFloat() * 0.2f;
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1, pitch);
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
        public ElementSignal performReaction(ServerWorld world, BlockPos pos);

        public static ConduitReaction basicReaction(ElementSignal signal) {
            return (world, pos) -> signal;
        }
    }
}
