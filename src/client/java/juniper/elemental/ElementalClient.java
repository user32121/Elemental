package juniper.elemental;

import juniper.elemental.init.ElementalModelLayers;
import juniper.elemental.init.ElementalBlockEntityRenderers;
import juniper.elemental.init.ElementalEntityRenderers;
import juniper.elemental.init.ElementalFluidRenderers;
import juniper.elemental.init.ElementalScreens;
import net.fabricmc.api.ClientModInitializer;

public class ElementalClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as
		// rendering.
		Elemental.LOGGER.info(Elemental.MOD_ID + " client init");
		ElementalScreens.init();
        ElementalEntityRenderers.init();
        ElementalModelLayers.init();
        ElementalBlockEntityRenderers.init();
        ElementalFluidRenderers.init();
	}
}