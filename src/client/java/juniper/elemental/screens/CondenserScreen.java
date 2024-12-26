package juniper.elemental.screens;

import juniper.elemental.Elemental;
import juniper.elemental.blockEntities.CondenserBlockEntity;
import juniper.elemental.elements.ElementSignal;
import juniper.elemental.screens.DrawingUtil.Direction;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CondenserScreen extends HandledScreen<CondenserScreenHandler> {
    private static final Identifier BACKGROUND_TEXTURE = Identifier.of(Elemental.MOD_ID,
            "textures/gui/container/condenser.png");
    private static final Identifier CONDENSE_PROGRESS_TEXTURE = Identifier.of(Elemental.MOD_ID,
            "container/condenser/condense_progress");
    private static final Identifier FUEL_PROGRESS_TEXTURE = Identifier.of(Elemental.MOD_ID,
            "container/condenser/fuel_progress");

    public CondenserScreen(CondenserScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    private int propertyTypeToIndex(int propertyType) {
        ElementSignal type = ElementSignal.VALUES[propertyType];
        return type.elementOrdinal;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(RenderLayer::getGuiTextured, BACKGROUND_TEXTURE, this.x, this.y, 0.0f, 0.0f,
                this.backgroundWidth, this.backgroundHeight, 256, 256);
        int progress = handler.propertyDelegate.get(0);
        int idx = propertyTypeToIndex(handler.propertyDelegate.get(1));
        DrawingUtil.drawGuiPartial(context, FUEL_PROGRESS_TEXTURE, Direction.BOTTOM, 16, 19, idx * 4, 0, 4, 19,
                this.x + 99, this.y + 34, progress, CondenserBlockEntity.FUEL_TIME_MAX);
        progress = handler.propertyDelegate.get(2);
        idx = propertyTypeToIndex(handler.propertyDelegate.get(3));
        DrawingUtil.drawGuiPartial(context, CONDENSE_PROGRESS_TEXTURE, Direction.LEFT, 22, 64, 0, idx * 16, 22, 16,
                this.x + 111, this.y + 35, progress, CondenserBlockEntity.CONDENSE_TIME_MAX);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
