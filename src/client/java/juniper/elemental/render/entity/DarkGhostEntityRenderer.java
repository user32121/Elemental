package juniper.elemental.render.entity;

import juniper.elemental.Elemental;
import juniper.elemental.entities.DarkGhostEntity;
import juniper.elemental.init.ElementalModelLayers;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.util.Identifier;

public class DarkGhostEntityRenderer extends BipedEntityRenderer<DarkGhostEntity, DarkGhostEntityRenderState, DarkGhostEntityModel> {
    public static final Identifier TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/entity/dark_ghost/dark_ghost.png");

    public DarkGhostEntityRenderer(Context ctx) {
        super(ctx, new DarkGhostEntityModel(ctx.getPart(ElementalModelLayers.DARK_GHOST)), 0.5f);
        addFeature(new ArmorFeatureRenderer<>(this, new ArmorEntityModel<>(ctx.getPart(ElementalModelLayers.DARK_GHOST_INNER_ARMOR)),
                new ArmorEntityModel<>(ctx.getPart(ElementalModelLayers.DARK_GHOST_OUTER_ARMOR)), ctx.getEquipmentRenderer()));
    }

    @Override
    public Identifier getTexture(DarkGhostEntityRenderState var1) {
        return TEXTURE;
    }

    @Override
    public DarkGhostEntityRenderState createRenderState() {
        return new DarkGhostEntityRenderState();
    }
}
