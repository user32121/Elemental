package juniper.elemental.screens;

import org.apache.commons.lang3.NotImplementedException;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFW;

import juniper.elemental.Elemental;
import juniper.elemental.network.SaveSpellPayload;
import juniper.elemental.spells.SpellStep;
import juniper.elemental.spells.SpellStep.Direction;
import juniper.elemental.widgets.StepConfigWidget;
import juniper.elemental.widgets.StepSelectWidget;
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

    private double offsetX = 72;
    private double offsetY = 72;
    private double lastMouseX;
    private double lastMouseY;
    private boolean isHovering = false;
    private int hoverTileX;
    private int hoverTileY;
    private int selectTileX = 0;
    private int selectTileY = 0;
    private StepSelectWidget stepSelectWidget = new StepSelectWidget();
    private StepConfigWidget stepConfigWidget = new StepConfigWidget();

    public WandScreen(WandScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = this.backgroundWidth = 176;
    }

    @Override
    protected void init() {
        super.init();
        addDrawable(stepSelectWidget);
        stepConfigWidget.setX(x - 32);
        stepConfigWidget.setY(y);
        updateConfigWidgetStep();
        addDrawable(stepConfigWidget);
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
                SpellStep step = handler.spell.steps.get(new Vector2i(tileX, tileY));
                if (step != null) {
                    DrawingUtil.drawGuiBounded(context, step.type.texture(), 16, 16, 0, 0, 15, 15, MathHelper.floor(x2) + 1 + x, MathHelper.floor(y2) + 1 + y, 8 + x, 167 + x, 8 + y, 167 + y);
                }
            }
        }
        for (double x2 = MathHelper.floorMod(offsetX, 16) - 8; x2 < 168; x2 += 16) {
            for (double y2 = MathHelper.floorMod(offsetY, 16) - 8; y2 < 168; y2 += 16) {
                int tileX = posToTileX(x2);
                int tileY = posToTileY(y2);
                SpellStep step = handler.spell.steps.get(new Vector2i(tileX, tileY));
                if (step != null) {
                    int v = getArrowV(step.next);
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

    public static Direction getHighlightDir(int dx, int dy) {
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
                v = 15;
                break;
            case DOWN:
                v = 45;
                break;
            case LEFT:
                v = 30;
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
        stepSelectWidget.mouseMoved(mouseX, mouseY);
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
        if (stepSelectWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        } else {
            stepSelectWidget.setFocused(false);
        }
        if (stepConfigWidget.mouseClicked(mouseX, mouseY, button)) {
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
        updateConfigWidgetStep();
        if (button == 1) {
            openSelectSpell();
        }
        return true;
    }

    private void updateConfigWidgetStep() {
        stepConfigWidget.setStep(handler.spell.steps.get(new Vector2i(selectTileX, selectTileY)));
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
        if (stepSelectWidget.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (stepConfigWidget.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                ++selectTileX;
                offsetX -= Math.max(0, tileToPosX(selectTileX) + 16 - 151);
                updateConfigWidgetStep();
            } else {
                offsetX -= 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                --selectTileX;
                offsetX -= Math.min(0, tileToPosX(selectTileX) - 23);
                updateConfigWidgetStep();
            } else {
                offsetX += 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_DOWN) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                ++selectTileY;
                offsetY -= Math.max(0, tileToPosY(selectTileY) + 16 - 151);
                updateConfigWidgetStep();
            } else {
                offsetY -= 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_UP) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) == 0) {
                --selectTileY;
                offsetY -= Math.min(0, tileToPosY(selectTileY) - 23);
                updateConfigWidgetStep();
            } else {
                offsetY += 16;
            }
            checkHover();
            return true;
        } else if (keyCode == GLFW.GLFW_KEY_ENTER) {
            openSelectSpell();
        } else if (keyCode == GLFW.GLFW_KEY_TAB) {
            stepConfigWidget.setFocused(true);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void openSelectSpell() {
        stepSelectWidget.setPosition((int) tileToPosX(selectTileX + 1) + x, (int) tileToPosY(selectTileY) + y);
        stepSelectWidget.setFocused(true);
        Vector2i pos = new Vector2i(selectTileX, selectTileY);
        SpellStep step = handler.spell.steps.get(pos);
        stepSelectWidget.setSelected(step);
        stepSelectWidget.setCallback(type -> {
            if (type == null) {
                handler.spell.steps.remove(pos);
            } else if (step == null || type != step.type) {
                handler.spell.steps.put(pos, new SpellStep(selectTileX, selectTileY, Direction.RIGHT, type));
            }
            updateConfigWidgetStep();
        });
    }
}
