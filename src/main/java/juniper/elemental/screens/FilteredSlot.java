package juniper.elemental.screens;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public class FilteredSlot extends Slot {
    private Item[] whitelist;

    public FilteredSlot(Inventory inventory, int index, int x, int y, Item... whitelist) {
        super(inventory, index, x, y);
        this.whitelist = whitelist;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        for (Item item : whitelist) {
            if (stack.isOf(item)) {
                return true;
            }
        }
        return false;
    }
}
