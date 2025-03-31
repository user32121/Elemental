package juniper.elemental.screens;

import juniper.elemental.Elemental;
import juniper.elemental.init.ElementalScreenHandlers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class WandScreenHandler extends ScreenHandler {
    public WandScreenHandler(int syncId, PlayerInventory playerInventory, String data) {
        super(ElementalScreenHandlers.WAND, syncId);
        Elemental.LOGGER.info(data);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
