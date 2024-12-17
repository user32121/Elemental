package juniper.elemental.init;

import juniper.elemental.Elemental;
import juniper.elemental.recipes.ReactionRecipe;
import juniper.elemental.recipes.ReactionRecipeSerializer;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ElementalRecipes {
    public static final RecipeType<ReactionRecipe> REACTION = register("reaction");
    public static final RecipeBookCategory REACTION_CATEGORY = registerCategory("reaction");
    public static final RecipeSerializer<ReactionRecipe> REACTION_SERAIZLIER = registerSeriazlier("reaction", new ReactionRecipeSerializer());

    public static void init() {
    }

    private static <T extends Recipe<?>> RecipeType<T> register(String id) {
        return Registry.register(Registries.RECIPE_TYPE, Identifier.of(Elemental.MOD_ID, id), new RecipeType<T>() {
            public String toString() {
                return id;
            }
        });
    }

    private static RecipeBookCategory registerCategory(String id) {
        return Registry.register(Registries.RECIPE_BOOK_CATEGORY, Identifier.of(Elemental.MOD_ID, id), new RecipeBookCategory());
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerSeriazlier(String id, S serializer) {
        return (S) Registry.register(Registries.RECIPE_SERIALIZER, Identifier.of(Elemental.MOD_ID, id), serializer);
    }
}
