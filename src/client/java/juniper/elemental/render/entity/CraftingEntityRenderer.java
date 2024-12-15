package juniper.elemental.render.entity;

import juniper.elemental.Elemental;
import juniper.elemental.entities.CraftingEntity;
import juniper.elemental.init.ElementalModelLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class CraftingEntityRenderer extends EntityRenderer<CraftingEntity, CraftingEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/entity/crafting/crafting.png");
    private static final RenderLayer RENDER_LAYER = RenderLayer.getEntityCutout(TEXTURE);
    private final CraftingEntityModel model;

    public CraftingEntityRenderer(Context context) {
        super(context);
        this.model = new CraftingEntityModel(context.getPart(ElementalModelLayers.CRAFTING));
    }

    @Override
    public CraftingEntityRenderState createRenderState() {
        return new CraftingEntityRenderState();
    }

    @Override
    public void render(CraftingEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        light = 255;
        super.render(state, matrices, vertexConsumers, light);
        model.setAngles(state);
        model.render(matrices, vertexConsumers.getBuffer(RENDER_LAYER), light, OverlayTexture.DEFAULT_UV);
    }
}
