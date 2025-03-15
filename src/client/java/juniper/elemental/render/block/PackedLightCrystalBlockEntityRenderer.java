package juniper.elemental.render.block;

import juniper.elemental.Elemental;
import juniper.elemental.blockEntities.PackedLightCrystalBlockEntity;
import juniper.elemental.init.ElementalModelLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class PackedLightCrystalBlockEntityRenderer implements BlockEntityRenderer<PackedLightCrystalBlockEntity> {
    private static final Identifier TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/block/light_crystal.png");
    private static final RenderLayer RENDER_LAYER = RenderLayer.getEntityCutout(TEXTURE);
    private final PackedLightCrystalBlockEntityModel model;

    public PackedLightCrystalBlockEntityRenderer(BlockEntityRendererFactory.Context context) {
        this.model = new PackedLightCrystalBlockEntityModel(context.getLayerModelPart(ElementalModelLayers.PACKED_LIGHT_CRYSTAL));
    }

    @Override
    public void render(PackedLightCrystalBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        model.setAngles(entity, tickDelta);
        model.render(matrices, vertexConsumers.getBuffer(RENDER_LAYER), light, OverlayTexture.DEFAULT_UV);
    }
}
