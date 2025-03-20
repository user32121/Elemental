package juniper.elemental.init;

import juniper.elemental.Elemental;
import juniper.elemental.fluids.AlkahestFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ElementalFluids {
    public static final Fluid ALKAHEST = register("alkahest", new AlkahestFluid());

    private static <T extends Fluid> T register(String name, T value) {
        return Registry.register(Registries.FLUID, Identifier.of(Elemental.MOD_ID, name), value);
    }
}
