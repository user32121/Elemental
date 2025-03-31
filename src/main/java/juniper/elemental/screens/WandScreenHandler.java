package juniper.elemental.screens;

import juniper.elemental.init.ElementalScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class WandScreenHandler extends ScreenHandler {
    public WandScreenHandler(int syncId, PlayerInventory playerInventory, int selectedSlot) {
        super(ElementalScreenHandlers.WAND, syncId);
        this.addSlot(new DisabledSlot(playerInventory, selectedSlot, 0, 0));
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return !getSlot(0).getStack().isEmpty();
    }
}
