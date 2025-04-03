package juniper.elemental.screens;

import juniper.elemental.init.ElementalItems;
import juniper.elemental.init.ElementalScreenHandlers;
import juniper.elemental.spells.WandSpell;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class WandScreenHandler extends ScreenHandler {
    WandSpell spell;

    public WandScreenHandler(int syncId, PlayerInventory playerInventory, WandSpell spell) {
        super(ElementalScreenHandlers.WAND, syncId);
        this.spell = spell;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.getInventory().getStack(player.getInventory().selectedSlot).isOf(ElementalItems.WAND);
    }
}
