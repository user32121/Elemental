package juniper.elemental.render.entity;

import juniper.elemental.Elemental;
import juniper.elemental.entities.DarkGhostEntity;
import juniper.elemental.init.ElementalModelLayers;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.util.Identifier;

public class DarkGhostEntityRenderer extends LivingEntityRenderer<DarkGhostEntity, DarkGhostEntityRenderState, DarkGhostEntityModel> {
    public static final Identifier TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/entity/dark_ghost/dark_ghost.png");

    public DarkGhostEntityRenderer(Context ctx) {
        super(ctx, new DarkGhostEntityModel(ctx.getPart(ElementalModelLayers.DARK_GHOST)), 0.5f);
    }

    @Override
    public Identifier getTexture(DarkGhostEntityRenderState var1) {
        return TEXTURE;
    }

    @Override
    public DarkGhostEntityRenderState createRenderState() {
        return new DarkGhostEntityRenderState();
    }

    @Override
    protected boolean hasLabel(DarkGhostEntity livingEntity, double d) {
        return super.hasLabel(livingEntity, d) && (livingEntity.shouldRenderName() || livingEntity.hasCustomName() && livingEntity == this.dispatcher.targetedEntity);
    }
}
