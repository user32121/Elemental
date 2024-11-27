package juniper.elemental.data;

import juniper.elemental.Elemental;
import juniper.elemental.blocks.TriAxisBlock;
import juniper.elemental.init.ElementalBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.data.client.When;
import net.minecraft.util.Identifier;

public class ElementalModelProvider extends FabricModelProvider {
    public ElementalModelProvider(FabricDataOutput fabricDataOutput) {
        super(fabricDataOutput);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        triAxisBlock(blockStateModelGenerator, ElementalBlocks.CLOGGED_CONDUIT, "clogged_conduit");
    }

    private void triAxisBlock(BlockStateModelGenerator bsmg, Block block, String name) {
        TextureMap textureMap = new TextureMap().put(TextureKey.TEXTURE, formatIdentifier(name, "_core"))
                .put(TextureKey.SIDE, formatIdentifier(name, "_axis"))
                .put(TextureKey.END, formatIdentifier(name, "_face"));
        Identifier idCore = ElementalModels.TriAxisBlockCore.upload(formatIdentifier(name, "_core"), textureMap,
                bsmg.modelCollector);
        Identifier idAxis = ElementalModels.TriAxisBlockAxis.upload(formatIdentifier(name, "_axis"), textureMap,
                bsmg.modelCollector);
        bsmg.blockStateCollector.accept(MultipartBlockStateSupplier.create(block)
                .with(BlockStateVariant.create().put(VariantSettings.MODEL, idCore))
                .with(When.create().set(TriAxisBlock.AXIS_X, true),
                        BlockStateVariant.create().put(VariantSettings.MODEL, idAxis).put(VariantSettings.Y,
                                VariantSettings.Rotation.R90))
                .with(When.create().set(TriAxisBlock.AXIS_Y, true),
                        BlockStateVariant.create().put(VariantSettings.MODEL, idAxis).put(VariantSettings.X,
                                VariantSettings.Rotation.R90))
                .with(When.create().set(TriAxisBlock.AXIS_Z, true),
                        BlockStateVariant.create().put(VariantSettings.MODEL, idAxis)));
        bsmg.registerParentedItemModel(block, idAxis);
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    }

    private Identifier formatIdentifier(String name, String suffix) {
        return Identifier.of(Elemental.MOD_ID, "block/" + name + suffix);
    }
}
