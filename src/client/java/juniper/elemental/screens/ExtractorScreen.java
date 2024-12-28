package juniper.elemental.screens;

import juniper.elemental.Elemental;
import juniper.elemental.blockEntities.ExtractorBlockEntity;
import juniper.elemental.screens.DrawingUtil.Direction;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExtractorScreen extends HandledScreen<ExtractorScreenHandler> {
    private static final String INVALID_STRING = "Invalid Multiblock";
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/gui/container/extractor.png");
    private static final Identifier INVALID_TEXTURE = Identifier.of(Elemental.MOD_ID, "container/extractor/invalid");
    private static final Identifier ELEMENTS_TEXTURE = Identifier.of(Elemental.MOD_ID, "container/extractor/elements");
    private static final Identifier EXTRACT_PROGRESS_TEXTURE = Identifier.of(Elemental.MOD_ID, "container/extractor/extract_progress");

    public ExtractorScreen(ExtractorScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, this.x, this.y, 0.0f, 0.0f, this.backgroundWidth, this.backgroundHeight, 256, 256);
        int flags = handler.propertyDelegate.get(0);
        if ((flags & 1) == 0) {
            context.drawGuiTexture(RenderLayer::getGuiTextured, INVALID_TEXTURE, 22, 15, 0, 0, this.x + 86, this.y + 35, 22, 15);
            context.drawText(textRenderer, INVALID_STRING, this.x + backgroundWidth / 2 - textRenderer.getWidth(INVALID_STRING) / 2, this.y + 64, 0xFF6060, false);
        }
        for (int i = 0; i < 4; ++i) {
            if ((flags & (1 << (i + 1))) != 0) {
                context.drawGuiTexture(RenderLayer::getGuiTextured, ELEMENTS_TEXTURE, 16, 64, 0, 16 * i, this.x + 42 + i % 2 * 18, this.y + 26 + i / 2 * 18, 16, 16);
            }
        }
        int progress = handler.propertyDelegate.get(1);
        DrawingUtil.drawGuiPartial(context, EXTRACT_PROGRESS_TEXTURE, Direction.LEFT, 22, 16, 0, 0, 22, 16, this.x + 86, this.y + 35, progress, ExtractorBlockEntity.MAX_PROGRESS);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
