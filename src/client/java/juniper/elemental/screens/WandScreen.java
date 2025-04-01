package juniper.elemental.screens;

import org.joml.Vector2i;

import juniper.elemental.Elemental;
import juniper.elemental.init.ElementalItems;
import juniper.elemental.spells.SpellStep;
import juniper.elemental.spells.SpellStep.Direction;
import juniper.elemental.spells.WandSpell;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WandScreen extends HandledScreen<WandScreenHandler> {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/gui/item/wand.png");

    private double offsetX = -1;
    private double offsetY = -1;
    private boolean isHovering = false;
    private int hoverTileX;
    private int hoverTileY;
    private WandSpell spell;

    public WandScreen(WandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = this.backgroundWidth = 176;
        this.spell = ElementalItems.WAND.getSpell(handler.getSlot(0).getStack());
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, x, y, 0.0f, 0.0f, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        for (double x = MathHelper.floorMod(offsetX, 16) + 8; x < 168; x += 16) {
            context.drawVerticalLine((int) x, 7, 168, 0xFF373737);
        }
        for (double y = MathHelper.floorMod(offsetY, 16) + 8; y < 168; y += 16) {
            context.drawHorizontalLine(8, 167, (int) y, 0xFF373737);
        }
        for (double x = MathHelper.floorMod(offsetX, 16) - 8; x < 168; x += 16) {
            for (double y = MathHelper.floorMod(offsetY, 16) - 8; y < 168; y += 16) {
                int tileX = getTileX(x);
                int tileY = getTileY(y);
                //TODO stuff going off edge of screen
                if (spell.steps.containsKey(new Vector2i(tileX, tileY))) {
                    context.drawText(textRenderer, "S", (int) x, (int) y, 0xFFFFFFFF, false);
                }
                if (isHovering && hoverTileX == tileX && hoverTileY == tileY) {
                    context.fill(RenderLayer.getGuiOverlay(), (int) x + 1, (int) y + 1, (int) x + 16, (int) y + 16, 0x52FFFFFF);
                }
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 2) {
            offsetX += deltaX;
            offsetY += deltaY;
        }
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        double mouseX2 = mouseX - x;
        double mouseY2 = mouseY - y;
        if (mouseX2 < 8 || mouseX2 >= 168 || mouseY2 < 8 || mouseY2 >= 168) {
            isHovering = false;
            return;
        }
        isHovering = true;
        hoverTileX = getTileX(mouseX2);
        hoverTileY = getTileY(mouseY2);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        //TODO save back to itemstack
        if (button != 0 && button != 1) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (!isHovering) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        if (button == 0) {
            spell.steps.put(new Vector2i(hoverTileX, hoverTileY), new SpellStep(hoverTileX, hoverTileY, Direction.RIGHT));
        } else if (button == 1) {
            spell.steps.remove(new Vector2i(hoverTileX, hoverTileY));
        }
        return true;
    }

    public int getTileX(double posX) {
        return MathHelper.floorDiv((int) (posX - offsetX - 8), 16);
    }

    public int getTileY(double posY) {
        return MathHelper.floorDiv((int) (posY - offsetY - 8), 16);
    }
}
