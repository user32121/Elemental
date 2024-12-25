package juniper.elemental.init;

import juniper.elemental.Elemental;
import juniper.elemental.screens.CondenserScreenHandler;
import juniper.elemental.screens.ExtractorScreenHandler;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.ScreenHandlerType.Factory;
import net.minecraft.util.Identifier;

public class ElementalScreenHandlers {
    public static final ScreenHandlerType<CondenserScreenHandler> CONDENSER = register("condenser", CondenserScreenHandler::new);
    public static final ScreenHandlerType<ExtractorScreenHandler> EXTRACTOR = register("extractor", ExtractorScreenHandler::new);

    public static void init() {
    }

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, Identifier.of(Elemental.MOD_ID, name),
                new ScreenHandlerType<>(factory, FeatureSet.empty()));
    }
}
