package juniper.elemental.screens;

import org.apache.commons.lang3.NotImplementedException;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

public class DrawingUtil {
    public enum Direction {
        TOP, BOTTOM, LEFT, RIGHT
    }

    /**
     * Helper function for drawing a portion of an image such as the progress bar in a furnace
     * @param textureWidth width of the texture
     * @param textureHeight height of the texture
     * @param start the direction from which the image will "grow"
     * @param u starting x of the texture
     * @param v starting y of the texture
     * @param width width of the item to draw
     * @param height height of the item to draw
     * @param x screen space x to draw at
     * @param y screen space y to draw at
     * @param progress portion out of progressMax to draw
     */
    public static void drawGuiPartial(DrawContext context, Identifier texture, Direction start, int textureWidth,
            int textureHeight, int u, int v, int width, int height, int x, int y, int progress, int progressMax) {
        switch (start) {
            case TOP:
                height = height * progress / progressMax;
                break;
            case BOTTOM:
                int newHeight = height * progress / progressMax;
                y += height - newHeight;
                v += height - newHeight;
                height = newHeight;
                break;
            case LEFT:
                width = width * progress / progressMax;
                break;
            case RIGHT:
                int newWidth = width * progress / progressMax;
                x += width - newWidth;
                u += width - newWidth;
                width = newWidth;
                break;
            default:
                throw new NotImplementedException("Unhandled enum: " + start);
        }
        context.drawGuiTexture(RenderLayer::getGuiTextured, texture, textureWidth, textureHeight, u, v, x, y, width,
                height);
    }
}
