package juniper.elemental.entities;

import java.util.List;

import juniper.elemental.init.ElementalDimensions;
import juniper.elemental.init.ElementalItems;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

//doesn't move, disappears when player gets near
public class DarkGhostEntity extends MobEntity {
    public static final String ARM_ANGLE_KEY = "ArmAngle";
    public static final TrackedData<Float> ARM_ANGLE = DataTracker.registerData(DarkGhostEntity.class,
            TrackedDataHandlerRegistry.FLOAT);

    public DarkGhostEntity(EntityType<? extends DarkGhostEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
            EntityData entityData) {
        getDataTracker().set(ARM_ANGLE, world.getRandom().nextFloat() * 2 * MathHelper.PI);
        return super.initialize(world, difficulty, spawnReason, entityData);
    }

    @Override
    protected void initDataTracker(Builder builder) {
        super.initDataTracker(builder);
        builder.add(ARM_ANGLE, 0.0f);
    }

    @Override
    public void tick() {
        super.tick();
        if (!(getWorld() instanceof ServerWorld world)) {
            return;
        }
        if (!world.getRegistryKey().getValue().equals(ElementalDimensions.DARK_ID)) {
            return;
        }
        //chance of disappearing each tick if a player is nearby and none have an active radar
        //should be high if close by, and decrease to 0 at max distance
        List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, new Box(getBlockPos()).expand(32), EntityPredicates.EXCEPT_SPECTATOR);
        //check for active radars
        ItemStack stack = new ItemStack(ElementalItems.RADAR);
        for (PlayerEntity player : players) {
            float cooldown = player.getItemCooldownManager().getCooldownProgress(stack, 0);
            //5 * 0.5 seconds at 20 blocks/tick means 50 blocks traveled, which is should be sufficiently far for the ghost to be out of the radar pulse
            if (cooldown >= 0.5) {
                return;
            }
        }
        for (PlayerEntity player : players) {
            float dist = player.distanceTo(this);
            float vanishChance = 0.1f / dist;
            if (getRandom().nextFloat() < vanishChance) {
                remove(RemovalReason.KILLED);
                return;
            }
        }
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
