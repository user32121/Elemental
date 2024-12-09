package juniper.elemental;

import juniper.elemental.init.ElementalScreens;
import net.fabricmc.api.ClientModInitializer;

public class ElementalClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as
		// rendering.
		Elemental.LOGGER.info(Elemental.MOD_ID + " client init");
		ElementalScreens.init();
	}
}