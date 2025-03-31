package juniper.elemental.screens;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class WandScreen extends HandledScreen<WandScreenHandler> {
    public WandScreen(WandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext var1, float var2, int var3, int var4) {
        // TODO Auto-generated method stub
    }
}
