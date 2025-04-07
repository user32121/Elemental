package juniper.elemental.widgets;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import juniper.elemental.Elemental;
import juniper.elemental.spells.SpellStep;
import juniper.elemental.spells.SpellStepType;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class StepSelectWidget implements Widget, Drawable, Element {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/gui/item/spells.png");
    private static final Identifier SELECT_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/select");

    private boolean focused;
    private int posX;
    private int posY;
    private int selected;
    private boolean isHovering;
    private Consumer<SpellStepType> callback;

    @Override
    public void setFocused(boolean value) {
        focused = value;
        if (value) {
            selected = 0;
        }
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!isFocused()) {
            return;
        }
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, getX(), getY(), 0.0f, 0.0f, getWidth(), getHeight(), 128, 128);
        for (int i = 0; i < SpellStepType.ALL.length + 1; i++) {
            int x = i % 4;
            int y = i / 4;
            context.drawBorder(getX() + 3 + x * 16, getY() + 3 + y * 16, 17, 17, 0xFF373737);
            if (i > 0) {
                context.drawGuiTexture(RenderLayer::getGuiTextured, SpellStepType.ALL[i - 1].texture(), 16, 16, 0, 0, getX() + 4 + x * 16, getY() + 4 + y * 16, 15, 15);
            }
            if (selected == i) {
                context.drawGuiTexture(RenderLayer::getGuiTextured, SELECT_TEXTURE, 16, 16, 0, 0, getX() + 4 + x * 16, getY() + 4 + y * 16, 15, 15);
            }
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
        return 72;
    }

    @Override
    public int getHeight() {
        return 88;
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isFocused()) {
            return false;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            selected = Math.min(selected + 1, SpellStepType.ALL.length);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
            selected = Math.max(selected - 1, 0);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            selected = Math.min(selected + 4, SpellStepType.ALL.length);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            selected = Math.max(selected - 4, 0);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            setFocused(false);
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
            selectStep();
            return true;
        }
        return Element.super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void selectStep() {
        if (selected == 0) {
            callback.accept(null);
        } else {
            callback.accept(SpellStepType.ALL[selected - 1]);
        }
        setFocused(false);
    }

    //called when a spell is selected, which may be null
    public void setCallback(Consumer<SpellStepType> value) {
        this.callback = value;
    }

    public int posToTileX(double posX) {
        return MathHelper.floorDiv((int) (posX - 4), 16);
    }

    public int posToTileY(double posY) {
        return MathHelper.floorDiv((int) (posY - 4), 16);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (!isFocused()) {
            return;
        }
        Element.super.mouseMoved(mouseX, mouseY);
        checkHover(mouseX, mouseY);
    }

    private void checkHover(double mouseX, double mouseY) {
        double mouseX2 = mouseX - getX();
        double mouseY2 = mouseY - getY();
        if (mouseX2 < 4 || mouseX2 >= 68 || mouseY2 < 4 || mouseY2 >= 84) {
            isHovering = false;
            return;
        }
        isHovering = true;
        int hoverTileX = posToTileX(mouseX2);
        int hoverTileY = posToTileY(mouseY2);
        int newSelected = hoverTileY * 4 + hoverTileX;
        if (newSelected >= 0 && newSelected <= SpellStepType.ALL.length) {
            selected = newSelected;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isFocused()) {
            return false;
        }
        if (isHovering) {
            selectStep();
            return true;
        }
        return Element.super.mouseClicked(mouseX, mouseY, button);
    }

    public void setSelected(SpellStep step) {
        if (step == null) {
            selected = 0;
            return;
        }
        for (int i = 0; i < SpellStepType.ALL.length; ++i) {
            if (step.type == SpellStepType.ALL[i]) {
                selected = i + 1;
                return;
            }
        }
    }
}
