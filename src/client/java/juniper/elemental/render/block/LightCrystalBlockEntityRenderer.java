package juniper.elemental.render.block;

import juniper.elemental.Elemental;
import juniper.elemental.blockEntities.LightCrystalBlockEntity;
import juniper.elemental.init.ElementalModelLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class LightCrystalBlockEntityRenderer implements BlockEntityRenderer<LightCrystalBlockEntity> {
    private static final Identifier TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/entity/light_crystal.png");
    private static final RenderLayer RENDER_LAYER = RenderLayer.getEntityCutout(TEXTURE);
    private final LightCrystalBlockEntityModel model;

    public LightCrystalBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.model = new LightCrystalBlockEntityModel(context.getLayerModelPart(ElementalModelLayers.LIGHT_CRYSTAL));
    }

    @Override
    public void render(LightCrystalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        model.setAngles(entity, tickDelta);
        model.render(matrices, vertexConsumers.getBuffer(RENDER_LAYER), light, OverlayTexture.DEFAULT_UV);
    }
}
