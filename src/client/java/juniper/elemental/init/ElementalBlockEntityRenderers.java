package juniper.elemental.init;

import juniper.elemental.render.block.LightBallBlockEntityRenderer;
import juniper.elemental.render.block.LightCrystalBlockEntityRenderer;
import juniper.elemental.render.block.PackedLightCrystalBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class ElementalBlockEntityRenderers {
    public static void init() {
        BlockEntityRendererFactories.register(ElementalBlockEntities.LIGHT_CRYSTAL, LightCrystalBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ElementalBlockEntities.PACKED_LIGHT_CRYSTAL, PackedLightCrystalBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(ElementalBlockEntities.LIGHT_BALL, LightBallBlockEntityRenderer::new);
    }
}
