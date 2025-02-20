package juniper.elemental.recipes;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.input.RecipeInput;

public class ReactionRecipeInput implements RecipeInput {
    private final List<ItemStack> stacks;
    public final float reactionProgress;
    public final boolean isFireWater;

    public ReactionRecipeInput(List<ItemStack> stacks, float reactionProgress, boolean isFireWater) {
        this.stacks = stacks;
        this.reactionProgress = reactionProgress;
        this.isFireWater = isFireWater;
    }

    @Override
    public ItemStack getStackInSlot(int idx) {
        return stacks.get(idx);
    }

    @Override
    public int getSize() {
        return stacks.size();
    }
}
