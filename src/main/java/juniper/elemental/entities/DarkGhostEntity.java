package juniper.elemental.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

//doesn't move, disappears when player gets near
public class DarkGhostEntity extends MobEntity {
    public DarkGhostEntity(EntityType<? extends DarkGhostEntity> entityType, World world) {
        super(entityType, world);
    }
}
