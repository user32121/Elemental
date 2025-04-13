package juniper.elemental.widgets;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.glfw.GLFW;

import juniper.elemental.Elemental;
import juniper.elemental.screens.WandScreen;
import juniper.elemental.spells.SpellTile.Direction;
import juniper.elemental.spells.SpellTileType.SpellProperty;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public record SpellPropertyHandler(Renderer renderer, MouseHandler mouseHandler, KeyHandler keyHandler) {

    private static final Identifier ARROW_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/arrows");
    private static final Identifier HIGHLIGHT_TEXTURE = Identifier.of(Elemental.MOD_ID, "item/wand/highlights");

    public static Map<SpellProperty, SpellPropertyHandler> ALL = new HashMap<>();
    static {
        ALL.put(SpellProperty.NUMBER, new SpellPropertyHandler((context, textRenderer, posX, posY, mouseX, mouseY, value) -> {
            String string = Double.toString(value);
            context.drawText(textRenderer, string, posX + 16 - textRenderer.getWidth(string) / 2, posY + 16 - textRenderer.fontHeight / 2, 0xFFFFFFFF, false);
        }, (posX, posY, mouseX, mouseY, button, value) -> {
            return value;
        }, (keyCode, scanCode, modifiers, value) -> {
            if (keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9) {
                return fixDouble(value * 10 - getDigit(value * 10) + (keyCode - GLFW.GLFW_KEY_0) * (value < 0 ? -1 : 1));
            } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
                return fixDouble((value - getDigit(value)) / 10);
            } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
                return fixDouble(value / 10);
            } else if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                return fixDouble(value * 10);
            } else if (keyCode == GLFW.GLFW_KEY_PERIOD) {
                return 0;
            } else if (keyCode == GLFW.GLFW_KEY_MINUS) {
                return -value;
            }
            return value;
        }));
        ALL.put(SpellProperty.DIRECTION, new SpellPropertyHandler((context, textRenderer, posX, posY, mouseX, mouseY, value) -> {
            context.drawGuiTexture(RenderLayer::getGuiTextured, ARROW_TEXTURE, 32, 128, 0, WandScreen.getArrowV(Direction.fromId((int) value)), posX + 4, posY + 4, 23, 23);
            if (mouseX >= posX && mouseY >= posY && mouseX < posX + 32 && mouseY < posY + 32) {
                double dx = mouseX - (posX + 16);
                double dy = mouseY - (posY + 16);
                context.drawGuiTexture(RenderLayer::getGuiTexturedOverlay, HIGHLIGHT_TEXTURE, 32, 128, 0, WandScreen.getHighlightV(WandScreen.getHighlightDir(dx, dy)), posX, posY, 31, 31);
            }
        }, (posX, posY, mouseX, mouseY, button, value) -> {
            double dx = mouseX - (posX + 16);
            double dy = mouseY - (posY + 16);
            return WandScreen.getHighlightDir(dx, dy).id;
        }, (keyCode, scanCode, modifiers, value) -> {
            if (keyCode == GLFW.GLFW_KEY_RIGHT) {
                return Direction.RIGHT.id;
            } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
                return Direction.LEFT.id;
            } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
                return Direction.DOWN.id;
            } else if (keyCode == GLFW.GLFW_KEY_UP) {
                return Direction.UP.id;
            }
            return value;
        }));
    }

    private static double fixDouble(double value) {
        if (Double.isInfinite(value)) {
            return Math.copySign(Double.MAX_VALUE, value);
        } else if (Double.isNaN(value)) {
            return 0;
        }
        return value;
    }

    private static int getDigit(double value) {
        return (int) (value % 10);
    }

    @FunctionalInterface
    public interface Renderer {
        public void render(DrawContext context, TextRenderer textRenderer, int posX, int posY, int mouseX, int mouseY, double value);
    }

    @FunctionalInterface
    public interface MouseHandler {
        public double mouseClicked(double posX, double posY, double mouseX, double mouseY, int button, double value);
    }

    @FunctionalInterface
    public interface KeyHandler {
        public double keyPressed(int keyCode, int scanCode, int modifiers, double value);
    }
}
