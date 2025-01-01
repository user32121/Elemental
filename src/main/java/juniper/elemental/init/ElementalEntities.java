package juniper.elemental.init;

import java.util.function.UnaryOperator;

import juniper.elemental.Elemental;
import juniper.elemental.entities.DarkFollowerEntity;
import juniper.elemental.entities.DarkGhostEntity;
import juniper.elemental.entities.ReactionCraftingEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EntityType.Builder;
import net.minecraft.entity.EntityType.EntityFactory;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ElementalEntities {
    public static final EntityType<ReactionCraftingEntity> REACTION_CRAFTING_AIR_EARTH = register("reaction_crafting_air_earth", ReactionCraftingEntity::new, SpawnGroup.MISC,
            builder -> builder.dimensions(0.5f, 0.5f));
    public static final EntityType<ReactionCraftingEntity> REACTION_CRAFTING_FIRE_WATER = register("reaction_crafting_fire_water", ReactionCraftingEntity::new, SpawnGroup.MISC,
            builder -> builder.dimensions(0.5f, 0.5f));
    public static final EntityType<DarkGhostEntity> DARK_GHOST = register("dark_ghost", DarkGhostEntity::new, SpawnGroup.MONSTER, builder -> builder.dimensions(0.5f, 1.975f));
    public static final EntityType<DarkFollowerEntity> DARK_FOLLOWER = register("dark_follower", DarkFollowerEntity::new, SpawnGroup.MONSTER, builder -> builder.dimensions(0.5f, 1.975f));

    public static void init() {
        FabricDefaultAttributeRegistry.register(DARK_GHOST, DarkGhostEntity.createMobAttributes());
        FabricDefaultAttributeRegistry.register(DARK_FOLLOWER, DarkFollowerEntity.createFollowerAttributes());
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityFactory<T> factory, SpawnGroup spawnGroup, UnaryOperator<Builder<T>> builder) {
        return Registry.register(Registries.ENTITY_TYPE, Identifier.of(Elemental.MOD_ID, name),
                builder.apply(Builder.create(factory, spawnGroup)).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(name))));
    }
}
