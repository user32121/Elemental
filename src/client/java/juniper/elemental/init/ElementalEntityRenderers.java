package juniper.elemental.init;

import juniper.elemental.render.entity.ReactionCraftingEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ElementalEntityRenderers {
    public static void init() {
        EntityRendererRegistry.register(ElementalEntities.REACTION_CRAFTING_AIR_EARTH, ReactionCraftingEntityRenderer::new);
        EntityRendererRegistry.register(ElementalEntities.REACTION_CRAFTING_FIRE_WATER, ReactionCraftingEntityRenderer::new);
    }
}
