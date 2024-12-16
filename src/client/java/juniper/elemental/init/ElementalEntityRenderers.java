package juniper.elemental.init;

import juniper.elemental.render.entity.CraftingEntityRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ElementalEntityRenderers {
    public static void init() {
        EntityRendererRegistry.register(ElementalEntities.CRAFTING_AIR_EARTH, CraftingEntityRenderer::new);
        EntityRendererRegistry.register(ElementalEntities.CRAFTING_FIRE_WATER, CraftingEntityRenderer::new);
    }
}
