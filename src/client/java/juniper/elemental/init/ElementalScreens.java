package juniper.elemental.init;

import juniper.elemental.screens.CondenserScreen;
import juniper.elemental.screens.ExtractorScreen;
import juniper.elemental.screens.WandScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ElementalScreens {
    public static void init() {
        HandledScreens.register(ElementalScreenHandlers.CONDENSER, CondenserScreen::new);
        HandledScreens.register(ElementalScreenHandlers.EXTRACTOR, ExtractorScreen::new);
        HandledScreens.register(ElementalScreenHandlers.WAND, WandScreen::new);
    }
}
