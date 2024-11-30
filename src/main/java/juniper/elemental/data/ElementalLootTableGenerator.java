package juniper.elemental.data;

import java.util.concurrent.CompletableFuture;

import juniper.elemental.init.ElementalBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

public class ElementalLootTableGenerator extends FabricBlockLootTableProvider {
    public ElementalLootTableGenerator(FabricDataOutput dataOutput,
            CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ElementalBlocks.CONDUIT);
        addDrop(ElementalBlocks.OVERGROWN_CONDUIT);
        addDrop(ElementalBlocks.CLOGGED_CONDUIT);
        addDrop(ElementalBlocks.DUST);
        addDrop(ElementalBlocks.MELTED_CONDUIT);
        addDrop(ElementalBlocks.BLOWN_OUT_CONDUIT);
    }
}
