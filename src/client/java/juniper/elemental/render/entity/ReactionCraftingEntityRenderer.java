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
import net.minecraft.util.math.MathHelper;

public class ReactionCraftingEntityRenderer extends EntityRenderer<ReactionCraftingEntity, ReactionCraftingEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/entity/reaction_crafting/reaction_crafting.png");
    private static final RenderLayer RENDER_LAYER = RenderLayer.getEntityCutout(TEXTURE);
    private final ReactionCraftingEntityModel model;

    public ReactionCraftingEntityRenderer(Context context) {
        super(context);
        this.model = new ReactionCraftingEntityModel(context.getPart(ElementalModelLayers.REACTION_CRAFTING));
    }

    @Override
    public ReactionCraftingEntityRenderState createRenderState() {
        return new ReactionCraftingEntityRenderState();
    }

    @Override
    public void updateRenderState(ReactionCraftingEntity entity, ReactionCraftingEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.craftProgress = MathHelper.lerp(tickDelta, entity.prevCraftProgress, entity.craftProgress);
        state.isFireWater = entity.isFireWater;
    }

    @Override
    public void render(ReactionCraftingEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        light = 255;
        super.render(state, matrices, vertexConsumers, light);
        model.setAngles(state);
        model.render(matrices, vertexConsumers.getBuffer(RENDER_LAYER), light, OverlayTexture.DEFAULT_UV);
    }
}
