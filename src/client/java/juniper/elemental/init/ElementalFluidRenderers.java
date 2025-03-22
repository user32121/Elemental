package juniper.elemental.init;

import juniper.elemental.Elemental;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.util.Identifier;

public class ElementalFluidRenderers {
    public static void init() {
        FluidRenderHandlerRegistry.INSTANCE.register(ElementalFluids.ALKAHEST,
                new SimpleFluidRenderHandler(Identifier.of(Elemental.MOD_ID, "block/alkahest"), Identifier.of(Elemental.MOD_ID, "block/alkahest")));
    }
}
