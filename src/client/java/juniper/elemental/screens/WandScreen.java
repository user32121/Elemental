package juniper.elemental.screens;

import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import juniper.elemental.Elemental;
import juniper.elemental.network.SaveSpellPayload;
import juniper.elemental.spells.SpellStep;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class WandScreen extends HandledScreen<WandScreenHandler> {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/gui/item/wand.png");
    private static final Identifier ARROW_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/arrows");
    private static final Identifier SELECT_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/select");
    private static final Identifier NOP_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/nop");

    private double offsetX = 72;
    private double offsetY = 72;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isHovering = false;
    private int hoverTileX;
    private int hoverTileY;
    private int selectTileX = 0;
    private int selectTileY = 0;

    public WandScreen(WandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = this.backgroundWidth = 176;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, x, y, 0.0f, 0.0f, backgroundWidth, backgroundHeight, 256, 256);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        //lines
        for (double x = MathHelper.floorMod(offsetX, 16) + 8; x < 168; x += 16) {
            context.drawVerticalLine((int) x, 7, 168, 0xFF373737);
        }
        for (double y = MathHelper.floorMod(offsetY, 16) + 8; y < 168; y += 16) {
            context.drawHorizontalLine(8, 167, (int) y, 0xFF373737);
        }
        //actual tiles
        for (double x = MathHelper.floorMod(offsetX, 16) - 8; x < 168; x += 16) {
            for (double y = MathHelper.floorMod(offsetY, 16) - 8; y < 168; y += 16) {
                int tileX = posToTileX(x);
                int tileY = posToTileY(y);
                if (handler.spell.steps.containsKey(new Vector2i(tileX, tileY))) {
                    DrawingUtil.drawGuiBounded(context, NOP_TEXTURE, 16, 16, 0, 0, 15, 15, MathHelper.floor(x) + 1, MathHelper.floor(y) + 1, 8, 167, 8, 167);
                }
            }
        }
        for (double x = MathHelper.floorMod(offsetX, 16) - 8; x < 168; x += 16) {
            for (double y = MathHelper.floorMod(offsetY, 16) - 8; y < 168; y += 16) {
                int tileX = posToTileX(x);
                int tileY = posToTileY(y);
                SpellStep step = handler.spell.steps.get(new Vector2i(tileX, tileY));
                if (step != null) {
                    int v;
                    switch (step.next) {
                        case UP:
                            v = 23;
                            break;
                        case DOWN:
                            v = 69;
                            break;
                        case LEFT:
                            v = 46;
                            break;
                        case RIGHT:
                            v = 0;
                            break;
                        default:
                            throw new NotImplementedException("Unhandled enum: " + step.next);
                    }
                    DrawingUtil.drawGuiBounded(context, ARROW_TEXTURE, 32, 128, 0, v, 23, 23, MathHelper.floor(x) - 3, MathHelper.floor(y) - 3, 8, 167, 8, 167);
                }
            }
        }
        if (selectTileX >= posToTileX(8) && selectTileX <= posToTileX(168) && selectTileY >= posToTileY(8) && selectTileY <= posToTileY(168)) {
            double x = tileToPosX(selectTileX);
            double y = tileToPosY(selectTileY);
            DrawingUtil.drawGuiBounded(context, SELECT_TEXTURE, 16, 16, 0, 0, 15, 15, MathHelper.floor(x) + 1, MathHelper.floor(y) + 1, 8, 167, 8, 167);
        }
        if (isHovering && hoverTileX >= posToTileX(8) && hoverTileX <= posToTileX(168) && hoverTileY >= posToTileY(8) && hoverTileY <= posToTileY(168)) {
            double x = tileToPosX(hoverTileX);
            double y = tileToPosY(hoverTileY);
            context.fill(RenderLayer.getGuiOverlay(), Math.max(MathHelper.floor(x) + 1, 8), Math.max(MathHelper.floor(y) + 1, 8), Math.min(MathHelper.floor(x) + 16, 168),
                    Math.min(MathHelper.floor(y) + 16, 168), 0x52FFFFFF);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 2) {
            return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        offsetX += deltaX;
        offsetY += deltaY;
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        checkHover(mouseX, mouseY);
    }

    private void checkHover(double mouseX, double mouseY) {
        lastMouseX = mouseX;
        lastMouseY = mouseY;
        checkHover();
    }

    private void checkHover() {
        double mouseX2 = lastMouseX - x;
        double mouseY2 = lastMouseY - y;
        if (mouseX2 < 8 || mouseX2 >= 168 || mouseY2 < 8 || mouseY2 >= 168) {
            isHovering = false;
            return;
        }
        isHovering = true;
        hoverTileX = posToTileX(mouseX2);
        hoverTileY = posToTileY(mouseY2);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0 && button != 1) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        checkHover(mouseX, mouseY);
        if (!isHovering) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        selectTileX = hoverTileX;
        selectTileY = hoverTileY;
        if (button == 1) {
            //TODO
        }
        return true;
    }

    public int posToTileX(double posX) {
        return MathHelper.floorDiv((int) (posX - offsetX - 8), 16);
    }

    public int posToTileY(double posY) {
        return MathHelper.floorDiv((int) (posY - offsetY - 8), 16);
    }

    public double tileToPosX(int tileX) {
        return tileX * 16 + offsetX + 8;
    }

    public double tileToPosY(int tileY) {
        return tileY * 16 + offsetY + 8;
    }

    @Override
    public void close() {
        ClientPlayNetworking.send(new SaveSpellPayload(handler.spell));
        super.close();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                ++selectTileX;
                offsetX -= Math.max(0, tileToPosX(selectTileX) + 16 - 151);
            } else {
                offsetX -= 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                --selectTileX;
                offsetX -= Math.min(0, tileToPosX(selectTileX) - 23);
            } else {
                offsetX += 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                ++selectTileY;
                offsetY -= Math.max(0, tileToPosY(selectTileY) + 16 - 151);
            } else {
                offsetY -= 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                --selectTileY;
                offsetY -= Math.min(0, tileToPosY(selectTileY) - 23);
            } else {
                offsetY += 16;
            }
            checkHover();
            return true;
        } else {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }
    }
}
