package juniper.elemental.render.entity;

import juniper.elemental.Elemental;
import juniper.elemental.entities.ReactionCraftingEntity;
import juniper.elemental.init.ElementalModelLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class ReactionCraftingEntityRenderer extends EntityRenderer<ReactionCraftingEntity> {
    private static final Identifier TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/entity/reaction_crafting/reaction_crafting.png");
    private static final RenderLayer RENDER_LAYER = RenderLayer.getEntityCutout(TEXTURE);
    private final ReactionCraftingEntityModel model;

    public ReactionCraftingEntityRenderer(Context context) {
        super(context);
        this.model = new ReactionCraftingEntityModel(context.getPart(ElementalModelLayers.REACTION_CRAFTING));
    }

    @Override
    public void render(ReactionCraftingEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        light = 255;
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        model.setAngles(entity, 0, 0, 0, yaw, 0);
        model.render(matrices, vertexConsumers.getBuffer(RENDER_LAYER), light, OverlayTexture.DEFAULT_UV);
    }

    @Override
    public Identifier getTexture(ReactionCraftingEntity entity) {
        return TEXTURE;
    }
}
