package juniper.elemental.items;

import juniper.elemental.Elemental;
import juniper.elemental.init.ElementalSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class RadarItem extends Item {
    public final int MAX_COOLDOWN = 5 * 20;

    public RadarItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClient()) {
            return;
        }
        if (!selected) {
            return;
        }
        if (!world.getDimensionEntry().matchesId(Identifier.of(Elemental.MOD_ID, "dark"))) {
            return;
        }
        if (entity instanceof PlayerEntity pe && pe.getItemCooldownManager().getCooldownProgress(stack, 0) > 0) {
            return;
        }
        world.playSound(null, entity.getBlockPos(), ElementalSounds.RADAR, SoundCategory.BLOCKS, 1, 1);
        if (entity instanceof PlayerEntity pe) {
            pe.getItemCooldownManager().set(stack, MAX_COOLDOWN);
        }
    }
}