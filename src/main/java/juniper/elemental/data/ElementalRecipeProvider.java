package juniper.elemental.data;

import java.util.concurrent.CompletableFuture;

import juniper.elemental.Elemental;
import juniper.elemental.init.ElementalBlocks;
import juniper.elemental.init.ElementalItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeGenerator;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class ElementalRecipeProvider extends FabricRecipeProvider {
    public ElementalRecipeProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public String getName() {
        return Elemental.MOD_ID + " Recipes";
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                offer2x2CompactingRecipe(RecipeCategory.MISC, ElementalItems.EARTH_SHARD, ElementalItems.EARTH_FRAGMENT);
                offer2x2CompactingRecipe(RecipeCategory.MISC, ElementalItems.WATER_SHARD, ElementalItems.WATER_FRAGMENT);
                offer2x2CompactingRecipe(RecipeCategory.MISC, ElementalItems.AIR_SHARD, ElementalItems.AIR_FRAGMENT);
                offer2x2CompactingRecipe(RecipeCategory.MISC, ElementalItems.FIRE_SHARD, ElementalItems.FIRE_FRAGMENT);
                offer2x2CompactingRecipe(RecipeCategory.MISC, ElementalItems.DARK_SHARD, ElementalBlocks.DARK_BLOCK);
            }
        };
    }
}
