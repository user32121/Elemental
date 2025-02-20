package juniper.elemental.render.entity;

import juniper.elemental.Elemental;
import juniper.elemental.entities.DarkGhostEntity;
import juniper.elemental.init.ElementalModelLayers;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.util.Identifier;

public class DarkGhostEntityRenderer extends BipedEntityRenderer<DarkGhostEntity, DarkGhostEntityModel> {
    public static final Identifier TEXTURE = Identifier.of(Elemental.MOD_ID, "textures/entity/dark_ghost/dark_ghost.png");

    public DarkGhostEntityRenderer(Context ctx) {
        super(ctx, new DarkGhostEntityModel(ctx.getPart(ElementalModelLayers.DARK_GHOST)), 0.5f);
    }

    @Override
    public Identifier getTexture(DarkGhostEntity entity) {
        return TEXTURE;
    }
}
