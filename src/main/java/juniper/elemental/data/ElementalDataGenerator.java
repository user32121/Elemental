package juniper.elemental.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class ElementalDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ElementalModelProvider::new);
        pack.addProvider(ElementalLootTableProvider::new);
        pack.addProvider(ElementalBlockTagProvider::new);
        pack.addProvider(ElementalRecipeProvider::new);
        pack.addProvider(ElementalAdvancementProvider::new);
    }
}
