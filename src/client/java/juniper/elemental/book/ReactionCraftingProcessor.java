package juniper.elemental.book;

import juniper.elemental.recipes.ReactionRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

public class ReactionCraftingProcessor implements IComponentProcessor {
    private ReactionRecipe recipe;

    @Override
    public void setup(World world, IVariableProvider variables) {
        String recipeId = variables.get("recipe", world.getRegistryManager()).asString();
        Recipe<?> recipe = world.getRecipeManager().get(Identifier.of(recipeId)).orElseThrow(IllegalArgumentException::new).value();
        if (!(recipe instanceof ReactionRecipe rr)) {
            throw new IllegalArgumentException("Not a reaction recipe");
        }
        this.recipe = rr;
    }

    @Override
    public IVariable process(World world, String key) {
        if (key.startsWith("item")) {
            int i = Integer.parseInt(key.substring(("item").length()));
            DefaultedList<Ingredient> ingredients = recipe.getIngredients();
            ItemStack[] stacks;
            ItemStack stack = ingredients.size() <= i ? ItemStack.EMPTY : (stacks = ingredients.get(i).getMatchingStacks()).length == 0 ? ItemStack.EMPTY : stacks[0];
            return IVariable.from(stack, world.getRegistryManager());
        } else if (key.equals("title")) {
            return IVariable.from(recipe.getResult(world.getRegistryManager()).getName(), world.getRegistryManager());
        } else if (key.equals("output")) {
            return IVariable.from(recipe.getResult(world.getRegistryManager()), world.getRegistryManager());
        } else {
            return null;
        }
    }
}
