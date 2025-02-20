package juniper.elemental.entities.goals;

import java.util.EnumSet;
import java.util.List;

import juniper.elemental.init.ElementalDimensions;
import juniper.elemental.init.ElementalItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;

public class FollowerAttackGoal extends Goal {
    private final MobEntity actor;
    private final double speed;

    public FollowerAttackGoal(MobEntity actor, double speed) {
        this.actor = actor;
        this.speed = speed;
        setControls(EnumSet.of(Goal.Control.MOVE));
    }

    @Override
    public boolean canStart() {
        return actor.getTarget() != null && actor.getWorld().getRegistryKey().getValue().equals(ElementalDimensions.DARK_ID);
    }

    @Override
    public void tick() {
        super.tick();
        //move near player when radar is off, and attack when radar is on
        List<PlayerEntity> players = actor.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(actor.getBlockPos()).expand(32), EntityPredicates.EXCEPT_SPECTATOR);
        float maxCooldown = 0;
        //check for active radars
        for (PlayerEntity player : players) {
            float cooldown = player.getItemCooldownManager().getCooldownProgress(ElementalItems.RADAR, 0);
            if (cooldown > maxCooldown) {
                maxCooldown = cooldown;
            }
        }
        if (maxCooldown > 0.5) {
            //radar on, stop moving
            actor.getNavigation().stop();
            //target pinged, attack
            LivingEntity target = actor.getTarget();
            if (actor.getWorld() instanceof ServerWorld && actor.isInAttackRange(target)
                    && (!(target instanceof PlayerEntity pe) || pe.getItemCooldownManager().getCooldownProgress(ElementalItems.RADAR, 0) >= 0.9)) {
                actor.tryAttack(target);
            }
        } else {
            //radar off, continue moving
            actor.getNavigation().startMovingTo(actor.getTarget(), speed);
        }
    }
}
