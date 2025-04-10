package juniper.elemental.widgets;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import juniper.elemental.Elemental;
import juniper.elemental.screens.WandScreen;
import juniper.elemental.spells.SpellTile;
import juniper.elemental.spells.SpellTile.Direction;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class SpellTileConfigWidget implements Widget, Drawable, Element {
    private static final Identifier ARROW_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/arrows");
    private static final Identifier HIGHLIGHT_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/highlights");

    private boolean focused;
    private int posX;
    private int posY;
    private SpellTile step;
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
        step = value;
        selected = 0;
        editing = false;
        setFocused(false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (step == null) {
            return;
        }
        context.drawGuiTexture(RenderLayer::getGuiTextured, step.type.texture(), 16, 16, 0, 0, getX() + 8, getY() + 8, 15, 15);
        context.drawGuiTexture(RenderLayer::getGuiTextured, ARROW_TEXTURE, 32, 128, 0, WandScreen.getArrowV(step.next), getX() + 4, getY() + 4, 23, 23);
        if (mouseX >= getX() + 8 && mouseY >= getY() + 8 && mouseX < getX() + 24 && mouseY < getY() + 24) {
            int dx = mouseX - (getX() + 16);
            int dy = mouseY - (getY() + 16);
            context.drawGuiTexture(RenderLayer::getGuiTexturedOverlay, HIGHLIGHT_TEXTURE, 16, 64, 0, WandScreen.getHighlightV(WandScreen.getHighlightDir(dx, dy)), getX() + 8, getY() + 8, 15, 15);
        }
        if (selected == 0) {
            context.drawBorder(getX(), getY(), 31, 31, editing ? 0xFFFFFFFF : 0xFFAAAAAA);
        }
        if (!isFocused()) {
            context.fill(RenderLayer.getGuiOverlay(), getX(), getY(), getX() + 32, getY() + 32, 0x7f000000);
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
        return 31;
    }

    @Override
    public int getHeight() {
        return 155;
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
        if (mouseX >= getX() + 8 && mouseY >= getY() + 8 && mouseX < getX() + 24 && mouseY < getY() + 24) {
            int dx = (int) mouseX - (getX() + 16);
            int dy = (int) mouseY - (getY() + 16);
            if (step != null) {
                step.next = WandScreen.getHighlightDir(dx, dy);
                return true;
            }
        }
        return Element.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) {
            return false;
        }
        if (step == null) {
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
                    step.next = Direction.RIGHT;
                } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
                    step.next = Direction.LEFT;
                } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                    step.next = Direction.DOWN;
                } else if (keyCode == GLFW.GLFW_KEY_UP) {
                    step.next = Direction.UP;
                }
            }
            return true;
        }
        return true;
    }
}
