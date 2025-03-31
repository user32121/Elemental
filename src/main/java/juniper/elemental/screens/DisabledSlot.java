package juniper.elemental.screens;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.slot.Slot;

public class DisabledSlot extends Slot {
    public DisabledSlot(Inventory inventory, int index, int x, int y) {
        super(inventory, index, x, y);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
