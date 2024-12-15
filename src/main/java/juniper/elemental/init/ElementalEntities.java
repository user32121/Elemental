package juniper.elemental.init;

import java.util.function.UnaryOperator;

import juniper.elemental.Elemental;
import juniper.elemental.entities.CraftingEntity;
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
    public static final EntityType<CraftingEntity> CRAFTING = register("crafting", CraftingEntity::new, SpawnGroup.MISC, builder -> builder.dimensions(0.5f, 0.5f));

    public static void init() {
    }

    private static <T extends Entity> EntityType<T> register(String name, EntityFactory<T> factory, SpawnGroup spawnGroup, UnaryOperator<Builder<T>> builder) {
        return Registry.register(Registries.ENTITY_TYPE, Identifier.of(Elemental.MOD_ID, name),
                builder.apply(Builder.create(factory, spawnGroup)).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of(name))));
    }
}
