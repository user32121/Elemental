package juniper.elemental.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class CraftingEntity extends Entity {
    public static final float PROGRESS_DECAY_RATE = 0.99f;
    public static final float MINIMUM_PROGRESS = (float) Math.pow(PROGRESS_DECAY_RATE, 20 * 5);
    private static final TrackedData<Float> CRAFT_PROGRESS = DataTracker.registerData(CraftingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final String CRAFT_PROGRESS_KEY = "CraftProgress";
    public float craftProgress, prevCraftProgress;
    public CraftingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(Builder builder) {
        builder.add(CRAFT_PROGRESS, 0f);
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (isAlwaysInvulnerableTo(source)) {
            return false;
        }
        if (!isRemoved()) {
            kill(world);
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
            //TODO check for item crafting
            //decay
            craftProgress *= PROGRESS_DECAY_RATE;
            if (craftProgress < MINIMUM_PROGRESS) {
                kill(world);
            }
            getDataTracker().set(CRAFT_PROGRESS, craftProgress);
        }
    }
}
