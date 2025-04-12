package juniper.elemental.widgets;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import juniper.elemental.spells.SpellTileType.SpellProperty;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public record SpellPropertyHandler(Renderer renderer, MouseHandler mouseHandler, KeyHandler keyHandler) {

    public static Map<SpellProperty, SpellPropertyHandler> ALL = new HashMap<>();
    static {
        ALL.put(SpellProperty.INTEGER, new SpellPropertyHandler((context, textRenderer, posX, posY, mouseX, mouseY, value) -> {
            String string = Integer.toString(value);
            context.drawText(textRenderer, string, posX + 16 - textRenderer.getWidth(string) / 2, posY + 16 - textRenderer.fontHeight / 2, 0xFFFFFFFF, false);
        }, (mouseX, mouseY, button, value) -> {
            return value;
        }, (keyCode, scanCode, modifiers, value) -> {
            if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
                return value * 10 + (keyCode - GLFW.GLFW_KEY_0) * (value < 0 ? -1 : 1);
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                return value / 10;
            } else if (keyCode == GLFW.GLFW_KEY_MINUS) {
                return -value;
            }
            return value;
        }));
    }

    @FunctionalInterface
    public interface Renderer {
        public void render(DrawContext context, TextRenderer textRenderer, int posX, int posY, int mouseX, int mouseY, int value);
    }

    @FunctionalInterface
    public interface MouseHandler {
        public int mouseClicked(double mouseX, double mouseY, int button, int value);
    }

    @FunctionalInterface
    public interface KeyHandler {
        public int keyPressed(int keyCode, int scanCode, int modifiers, int value);
    }
}
