package juniper.elemental.entities;

import juniper.elemental.entities.goals.FollowerAttackGoal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer.Builder;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class DarkFollowerEntity extends DarkGhostEntity {
    public DarkFollowerEntity(EntityType<? extends DarkGhostEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initGoals() {
        goalSelector.add(2, new FollowerAttackGoal(this, 1));
        targetSelector.add(2, new ActiveTargetGoal<>(this, PlayerEntity.class, false));
    }

    public static Builder createFollowerAttributes() {
        return createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35);
    }

    @Override
    public void onAttacking(Entity target) {
        super.onAttacking(target);
        remove(RemovalReason.KILLED);
    }
}
