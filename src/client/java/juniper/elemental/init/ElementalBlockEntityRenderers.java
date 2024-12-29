package juniper.elemental.init;

import juniper.elemental.render.block.LightCrystalBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ElementalBlockEntityRenderers {
    public static void init() {
        BlockEntityRendererFactories.register(ElementalBlockEntities.LIGHT_CRYSTAL, LightCrystalBlockEntityRenderer::new);
    }
}
