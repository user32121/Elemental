package juniper.elemental.render.entity;

import juniper.elemental.Elemental;
import juniper.elemental.entities.SpellEntity;
import juniper.elemental.init.ElementalModelLayers;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class SpellEntityRenderer extends EntityRenderer<SpellEntity, SpellEntityRenderState> {
    private static final Identifier TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/entity/spell/spell.png");
    private static final RenderLayer RENDER_LAYER = RenderLayer.getEntityCutout(TEXTURE);
    private final SpellEntityModel model;

    public SpellEntityRenderer(Context context) {
        super(context);
        this.model = new SpellEntityModel(context.getPart(ElementalModelLayers.SPELL));
    }

    @Override
    public SpellEntityRenderState createRenderState() {
        return new SpellEntityRenderState();
    }

    @Override
    public void updateRenderState(SpellEntity entity, SpellEntityRenderState state, float tickDelta) {
        super.updateRenderState(entity, state, tickDelta);
        state.pitch = entity.getPitch(tickDelta);
        state.yaw = entity.getYaw(tickDelta);
    }

    @Override
    public void render(SpellEntityRenderState state, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(state, matrices, vertexConsumers, light);
        model.setAngles(state);
        model.render(matrices, vertexConsumers.getBuffer(RENDER_LAYER), light, OverlayTexture.DEFAULT_UV);
    }
}
