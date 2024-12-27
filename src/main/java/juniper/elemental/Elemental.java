package juniper.elemental;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import juniper.elemental.init.ElementalBlockEntities;
import juniper.elemental.init.ElementalBlocks;
import juniper.elemental.init.ElementalComponents;
import juniper.elemental.init.ElementalEntities;
import juniper.elemental.init.ElementalItemGroups;
import juniper.elemental.init.ElementalItems;
import juniper.elemental.init.ElementalRecipes;
import juniper.elemental.init.ElementalSounds;

public class Elemental implements ModInitializer {
	public static final String MOD_ID = "elemental";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info(MOD_ID + " init");
		ElementalItemGroups.init();
        ElementalItems.init();
		ElementalBlocks.init();
		ElementalBlockEntities.init();
        ElementalEntities.init();
        ElementalRecipes.init();
        ElementalComponents.init();
        ElementalSounds.init();
	}
}