package juniper.elemental.init;

import juniper.elemental.screens.CondenserScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ElementalScreens {
    public static void init() {
        HandledScreens.register(ElementalScreenHandlers.CONDENSER, CondenserScreen::new);
    }
}
