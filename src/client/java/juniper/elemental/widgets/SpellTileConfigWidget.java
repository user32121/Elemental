package juniper.elemental.widgets;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import juniper.elemental.Elemental;
import juniper.elemental.screens.WandScreen;
import juniper.elemental.spells.SpellTile;
import juniper.elemental.spells.SpellTile.Direction;
import juniper.elemental.spells.SpellTileType.SpellProperty;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

public class SpellTileConfigWidget implements Widget, Drawable, Element {
    private static final Identifier ARROW_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/arrows");
    private static final Identifier HIGHLIGHT_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/highlights");

    public TextRenderer textRenderer;
    private boolean focused;
    private int posX;
    private int posY;
    private SpellTile tile;
    private int selected;
    private boolean editing;

    @Override
    public void setFocused(boolean value) {
        focused = value;
        if (!value) {
            editing = false;
        }
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    public void setTile(SpellTile value) {
        tile = value;
        selected = 0;
        editing = false;
        setFocused(false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (tile == null) {
            return;
        }
        //next tile property
        context.drawGuiTexture(RenderLayer::getGuiTextured, tile.type.texture(), 16, 16, 0, 0, getX() + 8, getY() + 8, 15, 15);
        context.drawGuiTexture(RenderLayer::getGuiTextured, ARROW_TEXTURE, 32, 128, 0, WandScreen.getArrowV(tile.next), getX() + 4, getY() + 4, 23, 23);
        if (mouseX >= getX() + 8 && mouseY >= getY() + 8 && mouseX < getX() + 24 && mouseY < getY() + 24) {
            int dx = mouseX - (getX() + 16);
            int dy = mouseY - (getY() + 16);
            context.drawGuiTexture(RenderLayer::getGuiTexturedOverlay, HIGHLIGHT_TEXTURE, 16, 64, 0, WandScreen.getHighlightV(WandScreen.getHighlightDir(dx, dy)), getX() + 8, getY() + 8, 15, 15);
        }
        //rest of properties
        int y = getY() + 32;
        for (Pair<String, SpellProperty> prop : tile.type.properties()) {
            context.drawText(textRenderer, prop.getLeft(), getX() + 16 - textRenderer.getWidth(prop.getLeft()) / 2, y, 0xFFFFFFFF, false);
            SpellPropertyHandler.ALL.get(prop.getRight()).renderer().render(context, textRenderer, getX(), y, mouseX, mouseY, tile.properties.getOrDefault(prop.getLeft(), 0.0));
            y += 32;
        }
        //selection outline
        context.drawBorder(getX(), getY() + selected * 32, 31, 31, editing ? 0xFFFFFFFF : 0xFFAAAAAA);
        //gray out if unfocused
        if (!isFocused()) {
            context.fill(RenderLayer.getGuiOverlay(), getX(), getY(), getX() + 32, getY() + 32 * (1 + tile.type.properties().size()), 0x7f000000);
        }
    }

    @Override
    public void setX(int value) {
        posX = value;
    }

    @Override
    public void setY(int value) {
        posY = value;
    }

    @Override
    public int getX() {
        return posX;
    }

    @Override
    public int getY() {
        return posY;
    }

    @Override
    public int getWidth() {
        return 32;
    }

    @Override
    public int getHeight() {
        if (tile == null) {
            return 32;
        } else {
            return 32 * (1 + tile.type.properties().size());
        }
    }

    @Override
    public void forEachChild(Consumer<ClickableWidget> consumer) {
        //NO OP
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return Widget.super.getNavigationFocus();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (tile == null) {
            return Element.super.mouseClicked(mouseX, mouseY, button);
        }
        if (mouseX >= getX() && mouseY >= getY() && mouseX < getX() + getWidth() && mouseY < getY() + getHeight()) {
            setFocused(true);
            selected = (int) ((mouseY - getY()) / 32);
            editing = true;
            if (selected == 0) {
                int dx = (int) mouseX - (getX() + 16);
                int dy = (int) mouseY - (getY() + 16);
                tile.next = WandScreen.getHighlightDir(dx, dy);
            } else {
                Pair<String, SpellProperty> prop = tile.type.properties().get(selected - 1);
                double value = SpellPropertyHandler.ALL.get(prop.getRight()).mouseHandler().mouseClicked(mouseX, mouseY, button, tile.properties.getOrDefault(prop.getLeft(), 0.0));
                tile.properties.put(prop.getLeft(), value);
            }
            return true;
        }
        return Element.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) {
            return false;
        }
        if (tile == null) {
            return false;
        }
        if (Element.super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_TAB || keyCode == GLFW.GLFW_KEY_ESCAPE) {
            setFocused(false);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
            editing = !editing;
            return true;
        } else if (editing) {
            if (selected == 0) {
                if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                    tile.next = Direction.RIGHT;
                } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
                    tile.next = Direction.LEFT;
                } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                    tile.next = Direction.DOWN;
                } else if (keyCode == GLFW.GLFW_KEY_UP) {
                    tile.next = Direction.UP;
                }
            } else {
                Pair<String, SpellProperty> prop = tile.type.properties().get(selected - 1);
                double value = SpellPropertyHandler.ALL.get(prop.getRight()).keyHandler().keyPressed(keyCode, scanCode, modifiers, tile.properties.getOrDefault(prop.getLeft(), 0.0));
                tile.properties.put(prop.getLeft(), value);
            }
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            selected = Math.min(selected + 1, tile.type.properties().size());
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            selected = Math.max(0, selected - 1);
            return true;
        }
        return false;
    }
}
