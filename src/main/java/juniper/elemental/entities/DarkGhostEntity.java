package juniper.elemental.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.world.World;

//doesn't move, disappears when player gets near
public class DarkGhostEntity extends ArmorStandEntity {
    public DarkGhostEntity(EntityType<? extends DarkGhostEntity> entityType, World world) {
        super(entityType, world);
    }
}
