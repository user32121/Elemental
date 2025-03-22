package juniper.elemental.data;

import java.util.concurrent.CompletableFuture;

import juniper.elemental.init.ElementalFluids;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider.FluidTagProvider;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;
import net.minecraft.registry.tag.FluidTags;

public class ElementalFluidTagProvider extends FluidTagProvider {
    public ElementalFluidTagProvider(FabricDataOutput output, CompletableFuture<WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(WrapperLookup wrapperLookup) {
        getOrCreateTagBuilder(FluidTags.WATER).add(ElementalFluids.ALKAHEST);
    }
}
