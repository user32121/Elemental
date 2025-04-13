package juniper.elemental.screens;

import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import juniper.elemental.Elemental;
import juniper.elemental.network.SaveSpellPayload;
import juniper.elemental.spells.SpellTile;
import juniper.elemental.spells.SpellTile.Direction;
import juniper.elemental.widgets.SpellTileConfigWidget;
import juniper.elemental.widgets.SpellTileSelectWidget;
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
    private static final Identifier ARROW_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/next_arrows");
    private static final Identifier SELECT_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/select");

    private double offsetX = 72;
    private double offsetY = 72;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isHovering = false;
    private int hoverTileX;
    private int hoverTileY;
    private int selectTileX = 0;
    private int selectTileY = 0;
    private SpellTileSelectWidget tileSelectWidget = new SpellTileSelectWidget();
    private SpellTileConfigWidget tileConfigWidget = new SpellTileConfigWidget();

    public WandScreen(WandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = this.backgroundWidth = 176;
    }

    @Override
    protected void init() {
        super.init();
        addDrawable(tileSelectWidget);
        tileConfigWidget.setX(x - 32);
        tileConfigWidget.setY(y);
        tileConfigWidget.textRenderer = textRenderer;
        updateTileConfigWidget();
        addDrawable(tileConfigWidget);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, x, y, 0.0f, 0.0f, backgroundWidth, backgroundHeight, 256, 256);
        //lines
        for (double x2 = MathHelper.floorMod(offsetX, 16) + 8; x2 < 168; x2 += 16) {
            context.drawVerticalLine((int) x2 + x, 7 + y, 168 + y, 0xFF373737);
        }
        for (double y2 = MathHelper.floorMod(offsetY, 16) + 8; y2 < 168; y2 += 16) {
            context.drawHorizontalLine(8 + x, 167 + x, (int) y2 + y, 0xFF373737);
        }
        //actual tiles
        for (double x2 = MathHelper.floorMod(offsetX, 16) - 8; x2 < 168; x2 += 16) {
            for (double y2 = MathHelper.floorMod(offsetY, 16) - 8; y2 < 168; y2 += 16) {
                int tileX = posToTileX(x2);
                int tileY = posToTileY(y2);
                SpellTile tile = handler.spell.tiles.get(new Vector2i(tileX, tileY));
                if (tile != null) {
                    DrawingUtil.drawGuiBounded(context, tile.type.texture(), 16, 16, 0, 0, 15, 15, MathHelper.floor(x2) + 1 + x, MathHelper.floor(y2) + 1 + y, 8 + x, 167 + x, 8 + y, 167 + y);
                }
            }
        }
        for (double x2 = MathHelper.floorMod(offsetX, 16) - 8; x2 < 168; x2 += 16) {
            for (double y2 = MathHelper.floorMod(offsetY, 16) - 8; y2 < 168; y2 += 16) {
                int tileX = posToTileX(x2);
                int tileY = posToTileY(y2);
                SpellTile tile = handler.spell.tiles.get(new Vector2i(tileX, tileY));
                if (tile != null) {
                    int v = getArrowV(tile.next);
                    DrawingUtil.drawGuiBounded(context, ARROW_TEXTURE, 32, 128, 0, v, 23, 23, MathHelper.floor(x2) - 3 + x, MathHelper.floor(y2) - 3 + y, 8 + x, 167 + x, 8 + y, 167 + y);
                }
            }
        }
        if (selectTileX >= posToTileX(8) && selectTileX <= posToTileX(168) && selectTileY >= posToTileY(8) && selectTileY <= posToTileY(168)) {
            double x2 = tileToPosX(selectTileX);
            double y2 = tileToPosY(selectTileY);
            DrawingUtil.drawGuiBounded(context, SELECT_TEXTURE, 16, 16, 0, 0, 15, 15, MathHelper.floor(x2) + 1 + x, MathHelper.floor(y2) + 1 + y, 8 + x, 167 + x, 8 + y, 167 + y);
        }
        if (isHovering && hoverTileX >= posToTileX(8) && hoverTileX <= posToTileX(168) && hoverTileY >= posToTileY(8) && hoverTileY <= posToTileY(168)) {
            double x2 = tileToPosX(hoverTileX);
            double y2 = tileToPosY(hoverTileY);
            context.fill(RenderLayer.getGuiOverlay(), Math.max(MathHelper.floor(x2) + 1, 8) + x, Math.max(MathHelper.floor(y2) + 1, 8) + y, Math.min(MathHelper.floor(x2) + 16, 168) + x,
                    Math.min(MathHelper.floor(y2) + 16, 168) + y, 0x52FFFFFF);
        }
    }

    public static int getArrowV(Direction dir) {
        int v;
        switch (dir) {
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
                throw new NotImplementedException("Unhandled enum: " + dir);
        }
        return v;
    }

    public static Direction getHighlightDir(double dx, double dy) {
        if (dy > dx) {
            if (dy > -dx) {
                return Direction.DOWN;
            } else {
                return Direction.LEFT;
            }
        } else {
            if (dy > -dx) {
                return Direction.RIGHT;
            } else {
                return Direction.UP;
            }
        }
    }

    public static int getHighlightV(Direction dir) {
        int v;
        switch (dir) {
            case UP:
                v = 31;
                break;
            case DOWN:
                v = 93;
                break;
            case LEFT:
                v = 62;
                break;
            case RIGHT:
                v = 0;
                break;
            default:
                throw new NotImplementedException("Unhandled enum: " + dir);
        }
        return v;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        //NO OP
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
        tileSelectWidget.mouseMoved(mouseX, mouseY);
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
        if (tileSelectWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            tileSelectWidget.setFocused(false);
        }
        if (tileConfigWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        if (button != 0 && button != 1) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        checkHover(mouseX, mouseY);
        if (!isHovering) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        selectTileX = hoverTileX;
        selectTileY = hoverTileY;
        updateTileConfigWidget();
        if (button == 1) {
            openSelectSpell();
        }
        return true;
    }

    private void updateTileConfigWidget() {
        tileConfigWidget.setTile(handler.spell.tiles.get(new Vector2i(selectTileX, selectTileY)));
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

    //TODO ctrl+c, ctrl+v, etc
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (tileSelectWidget.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (tileConfigWidget.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                ++selectTileX;
                offsetX -= Math.max(0, tileToPosX(selectTileX) + 16 - 151);
                updateTileConfigWidget();
            } else {
                offsetX -= 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                --selectTileX;
                offsetX -= Math.min(0, tileToPosX(selectTileX) - 23);
                updateTileConfigWidget();
            } else {
                offsetX += 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                ++selectTileY;
                offsetY -= Math.max(0, tileToPosY(selectTileY) + 16 - 151);
                updateTileConfigWidget();
            } else {
                offsetY -= 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                --selectTileY;
                offsetY -= Math.min(0, tileToPosY(selectTileY) - 23);
                updateTileConfigWidget();
            } else {
                offsetY += 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
            openSelectSpell();
        } else if (keyCode == GLFW.GLFW_KEY_TAB) {
            tileConfigWidget.setFocused(true);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void openSelectSpell() {
        tileSelectWidget.setPosition((int) tileToPosX(selectTileX + 1) + x, (int) tileToPosY(selectTileY) + y);
        tileSelectWidget.setFocused(true);
        Vector2i pos = new Vector2i(selectTileX, selectTileY);
        SpellTile tile = handler.spell.tiles.get(pos);
        tileSelectWidget.setSelected(tile);
        tileSelectWidget.setCallback(type -> {
            if (type == null) {
                handler.spell.tiles.remove(pos);
            } else if (tile == null || type != tile.type) {
                handler.spell.tiles.put(pos, new SpellTile(selectTileX, selectTileY, Direction.RIGHT, type));
            }
            updateTileConfigWidget();
        });
    }
}
