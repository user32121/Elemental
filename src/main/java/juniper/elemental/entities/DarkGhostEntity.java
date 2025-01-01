package juniper.elemental.entities;

import juniper.elemental.entities.goals.DisappearGoal;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

//doesn't move, disappears when player gets near
public class DarkGhostEntity extends PathAwareEntity {
    public static final String ARM_ANGLE_KEY = "ArmAngle";
    public static final TrackedData<Float> ARM_ANGLE = DataTracker.registerData(DarkGhostEntity.class, TrackedDataHandlerRegistry.FLOAT);

    public DarkGhostEntity(EntityType<? extends DarkGhostEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData) {
        getDataTracker().set(ARM_ANGLE, world.getRandom().nextFloat() * 2 * MathHelper.PI);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void initDataTracker(Builder builder) {
        super.initDataTracker(builder);
        builder.add(ARM_ANGLE, 0.0f);
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        goalSelector.add(2, new DisappearGoal(this));
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putFloat(ARM_ANGLE_KEY, getDataTracker().get(ARM_ANGLE));
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        getDataTracker().set(ARM_ANGLE, nbt.getFloat(ARM_ANGLE_KEY));
    }
}
