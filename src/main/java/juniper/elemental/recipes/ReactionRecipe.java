package juniper.elemental.recipes;

import java.util.List;

import juniper.elemental.init.ElementalRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.IngredientPlacement;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.book.RecipeBookCategory;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.world.World;

public class ReactionRecipe implements Recipe<ReactionRecipeInput> {
    public final List<Ingredient> ingredients;
    public final float cost;
    public final ItemStack result;
    public final boolean isFireWater;
    private IngredientPlacement ingredientPlacement;

    public ReactionRecipe(List<Ingredient> ingredients, float cost, ItemStack result, boolean isFireWater) {
        this.ingredients = ingredients;
        this.cost = cost;
        this.result = result;
        this.isFireWater = isFireWater;
    }

    @Override
    public boolean matches(ReactionRecipeInput input, World world) {
        if (input.isFireWater != isFireWater) {
            return false;
        }
        if (input.reactionProgress < cost) {
            return false;
        }
        if (input.size() != ingredients.size()) {
            return false;
        }
        //check that each ingredient is satisfied
        //and that each input is used
        //this will still miss some edge cases (e.g. 1 and 2 satisfy A, and 3 satisfies B and C), but is likely sufficient
        for (Ingredient ingredient : ingredients) {
            boolean match = false;
            for (int i = 0; i < input.size(); i++) {
                if (ingredient.test(input.getStackInSlot(i))) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                return false;
            }
        }
        for (int i = 0; i < input.size(); i++) {
            boolean match = false;
            for (Ingredient ingredient : ingredients) {
                if (ingredient.test(input.getStackInSlot(i))) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack craft(ReactionRecipeInput input, WrapperLookup lookup) {
        return result.copy();
    }

    @Override
    public RecipeSerializer<? extends Recipe<ReactionRecipeInput>> getSerializer() {
        return ElementalRecipes.REACTION_SERAIZLIER;
    }

    @Override
    public RecipeType<? extends Recipe<ReactionRecipeInput>> getType() {
        return ElementalRecipes.REACTION;
    }

    @Override
    public IngredientPlacement getIngredientPlacement() {
        if (this.ingredientPlacement == null) {
            this.ingredientPlacement = IngredientPlacement.forShapeless(this.ingredients);
        }
        return this.ingredientPlacement;
    }

    @Override
    public RecipeBookCategory getRecipeBookCategory() {
        return ElementalRecipes.REACTION_CATEGORY;
    }
}
