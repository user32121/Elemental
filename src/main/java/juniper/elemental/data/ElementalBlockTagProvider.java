package juniper.elemental.data;

import java.util.concurrent.CompletableFuture;

import juniper.elemental.init.ElementalBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.BlockTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.BlockTags;

public class ElementalBlockTagProvider extends BlockTagProvider {
    public ElementalBlockTagProvider(FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE).add(ElementalBlocks.CONDUIT,
                ElementalBlocks.OVERGROWN_CONDUIT, ElementalBlocks.CLOGGED_CONDUIT,
                ElementalBlocks.MELTED_CONDUIT, ElementalBlocks.BLOWN_OUT_CONDUIT, ElementalBlocks.CONDENSER, ElementalBlocks.CATALYST);
        getOrCreateTagBuilder(BlockTags.SHOVEL_MINEABLE).add(ElementalBlocks.DUST, ElementalBlocks.RICH_SOIL);
        getOrCreateTagBuilder(BlockTags.DIRT).add(ElementalBlocks.RICH_SOIL);
    }
}
