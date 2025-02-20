package juniper.elemental.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import juniper.elemental.init.ElementalEntities;
import juniper.elemental.init.ElementalRecipes;
import juniper.elemental.recipes.ReactionRecipe;
import juniper.elemental.recipes.ReactionRecipeInput;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class ReactionCraftingEntity extends Entity {
    public static final float PROGRESS_DECAY_RATE = 0.99f;
    public static final float MINIMUM_PROGRESS = (float) Math.pow(PROGRESS_DECAY_RATE, 20 * 5);
    private static final TrackedData<Float> CRAFT_PROGRESS = DataTracker.registerData(ReactionCraftingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final String CRAFT_PROGRESS_KEY = "CraftProgress";
    public float craftProgress, prevCraftProgress;
    public final boolean isFireWater;

    public ReactionCraftingEntity(EntityType<?> type, World world) {
        super(type, world);
        isFireWater = type.equals(ElementalEntities.REACTION_CRAFTING_FIRE_WATER);
    }

    @Override
    protected void initDataTracker(Builder builder) {
        builder.add(CRAFT_PROGRESS, 0f);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (isInvulnerableTo(source)) {
            return false;
        }
        if (!isRemoved()) {
            kill();
        }
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        float progress = nbt.getFloat(CRAFT_PROGRESS_KEY);
        getDataTracker().set(CRAFT_PROGRESS, progress);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat(CRAFT_PROGRESS_KEY, getCraftProgress());
    }

    public float getCraftProgress() {
        return getDataTracker().get(CRAFT_PROGRESS);
    }

    public void incrementCraftProgress() {
        getDataTracker().set(CRAFT_PROGRESS, getCraftProgress() + 1);
    }

    @Override
    public void tick() {
        super.tick();
        prevCraftProgress = craftProgress;
        craftProgress = getCraftProgress();
        if (craftProgress > prevCraftProgress) {
            ++prevCraftProgress;
        }
        if (getWorld() instanceof ServerWorld world) {
            //check for item crafting
            List<ItemStack> stacks = new ArrayList<>();
            List<ItemEntity> entities = world.getEntitiesByType(EntityType.ITEM, new Box(getBlockPos()), Entity::isAlive);
            for (ItemEntity entity : entities) {
                stacks.add(entity.getStack());
            }
            ReactionRecipeInput input = new ReactionRecipeInput(stacks, craftProgress, isFireWater);
            Optional<RecipeEntry<ReactionRecipe>> match = world.getRecipeManager().getFirstMatch(ElementalRecipes.REACTION, input, world);
            if (match.isPresent()) {
                for (ItemEntity entity : entities) {
                    ItemStack stack = entity.getStack();
                    stack.decrement(1);
                    entity.setStack(stack);
                }
                ItemScatterer.spawn(world, getBlockX(), getBlockY(), getBlockZ(), match.get().value().result.copy());
                kill();
            }
            //decay
            craftProgress *= PROGRESS_DECAY_RATE;
            if (craftProgress < MINIMUM_PROGRESS) {
                kill();
            }
            getDataTracker().set(CRAFT_PROGRESS, craftProgress);
        }
    }
}
