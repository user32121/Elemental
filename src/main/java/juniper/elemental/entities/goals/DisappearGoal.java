package juniper.elemental.entities.goals;

import java.util.List;

import juniper.elemental.init.ElementalDimensions;
import juniper.elemental.init.ElementalItems;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;

public class DisappearGoal extends Goal {
    private final MobEntity actor;

    public DisappearGoal(MobEntity actor) {
        this.actor = actor;
    }

    @Override
    public boolean canStart() {
        return actor.getWorld().getRegistryKey().getValue().equals(ElementalDimensions.DARK_ID);
    }

    @Override
    public void tick() {
        super.tick();
        //chance of disappearing each tick if a player is nearby and none have an active radar
        //should be high if close by, and decrease to 0 at max distance
        List<PlayerEntity> players = actor.getWorld().getEntitiesByClass(PlayerEntity.class, new Box(actor.getBlockPos()).expand(32), EntityPredicates.EXCEPT_SPECTATOR);
        //check for active radars
        for (PlayerEntity player : players) {
            float cooldown = player.getItemCooldownManager().getCooldownProgress(ElementalItems.RADAR, 0);
            //5 * 0.5 seconds at 20 blocks/tick means 50 blocks traveled, which is should be sufficiently far for the ghost to be out of the radar pulse
            if (cooldown >= 0.5) {
                return;
            }
        }
        for (PlayerEntity player : players) {
            float dist = player.distanceTo(actor);
            float vanishChance = 0.1f / dist;
            if (actor.getRandom().nextFloat() < vanishChance) {
                actor.remove(RemovalReason.KILLED);
                return;
            }
        }
    }
}
