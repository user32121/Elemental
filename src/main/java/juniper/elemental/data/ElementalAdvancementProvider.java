package juniper.elemental.data;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import juniper.elemental.Elemental;
import juniper.elemental.init.ElementalBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.item.ItemConvertible;
import net.minecraft.predicate.entity.LocationPredicate;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.util.Identifier;

public class ElementalAdvancementProvider extends FabricAdvancementProvider {
    protected ElementalAdvancementProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
        generateItemAdvancement(consumer, ElementalBlocks.CONDUIT, "conduit");
        generateItemAdvancement(consumer, ElementalBlocks.CONDENSER, "condenser");
        //TODO Missing element ResourceKey[minecraft:worldgen/structure / elemental:house]
        // generateStructureAdvancement(registryLookup, consumer, "house");
        // generateStructureAdvancement(registryLookup, consumer, "lab");
    }

    private void generateItemAdvancement(Consumer<AdvancementEntry> consumer, ItemConvertible item, String name) {
        Advancement.Builder.createUntelemetered().criterion("item", InventoryChangedCriterion.Conditions.items(item)).build(consumer, Identifier.of(Elemental.MOD_ID, "items/" + name).toString());
    }

    private void generateStructureAdvancement(WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer, String name) {
        Advancement.Builder.createUntelemetered()
                .criterion("structure",
                        TickCriterion.Conditions.createLocation(LocationPredicate.Builder
                                .createStructure(registryLookup.getWrapperOrThrow(RegistryKeys.STRUCTURE).getOrThrow(RegistryKey.of(RegistryKeys.STRUCTURE, Identifier.of(Elemental.MOD_ID, name))))))
                .build(consumer, Identifier.of(Elemental.MOD_ID, "structures/" + name).toString());
    }
}
