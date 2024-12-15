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
    private static final TrackedData<Float> CRAFT_PROGRESS = DataTracker.registerData(CraftingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final String CRAFT_PROGRESS_KEY = "CraftProgress";

    public CraftingEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected void initDataTracker(Builder builder) {
        builder.add(CRAFT_PROGRESS, 0f);
    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        if (this.isAlwaysInvulnerableTo(source)) {
            return false;
        }
        if (!this.isRemoved()) {
            this.kill(world);
        }
        return true;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        getDataTracker().set(CRAFT_PROGRESS, nbt.getFloat(CRAFT_PROGRESS_KEY));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        nbt.putFloat(CRAFT_PROGRESS_KEY, getDataTracker().get(CRAFT_PROGRESS));
    }
}
