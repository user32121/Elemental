package juniper.elemental.entities;

import juniper.elemental.spells.WandSpell;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker.Builder;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.world.World;

public class SpellEntity extends ProjectileEntity {
    private WandSpell spell;

    public SpellEntity(EntityType<? extends SpellEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(Builder builder) {
        //NO OP
    }

    public void setSpell(WandSpell spell) {
        this.spell = spell;
    }
}
