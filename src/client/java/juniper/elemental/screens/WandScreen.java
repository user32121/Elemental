package juniper.elemental.screens;

import juniper.elemental.Elemental;
import juniper.elemental.init.ElementalItems;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WandScreen extends HandledScreen<WandScreenHandler> {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/gui/item/wand.png");

    private int offsetX;
    private int offsetY;

    public WandScreen(WandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = this.backgroundWidth = 176;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, x, y, 0.0f, 0.0f, backgroundWidth, backgroundHeight, 256, 256);
        String spell = ElementalItems.WAND.getSpell(handler.getSlot(0).getStack());
        context.drawText(textRenderer, spell, x + backgroundWidth / 2 - textRenderer.getWidth(spell) / 2, y + backgroundHeight / 2 - textRenderer.fontHeight / 2, 0, false);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        for (int x = (offsetX + 15) & 0xF; x < 160; x += 16) {
            context.drawVerticalLine(x + 8, 7, 167, 0xFF373737);
        }
        for (int y = (offsetY + 15) & 0xF; y < 160; y += 16) {
            context.drawHorizontalLine(8, 167, y + 8, 0xFF373737);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        if (button == 2) {
            offsetX += deltaX;
            offsetY += deltaY;
        }
        return true;
    }
}
