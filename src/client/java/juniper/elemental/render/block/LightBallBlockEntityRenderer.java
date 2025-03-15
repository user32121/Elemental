package juniper.elemental.render.block;

import juniper.elemental.Elemental;
import juniper.elemental.blockEntities.LightBallBlockEntity;
import juniper.elemental.init.ElementalModelLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class LightBallBlockEntityRenderer implements BlockEntityRenderer<LightBallBlockEntity> {
    private static final Identifier TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/block/light_crystal.png");
    private static final RenderLayer RENDER_LAYER = RenderLayer.getEntityCutout(TEXTURE);
    private final LightBallBlockEntityModel model;

    public LightBallBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.model = new LightBallBlockEntityModel(context.getLayerModelPart(ElementalModelLayers.LIGHT_BALL));
    }

    @Override
    public void render(LightBallBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        model.setAngles(entity, tickDelta);
        model.render(matrices, vertexConsumers.getBuffer(RENDER_LAYER), light, OverlayTexture.DEFAULT_UV);
    }
}
