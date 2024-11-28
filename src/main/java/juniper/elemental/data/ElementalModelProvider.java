package juniper.elemental.data;

import java.util.Optional;

import juniper.elemental.blocks.TriAxisBlock;
import juniper.elemental.init.ElementalBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.BlockStateVariant;
import net.minecraft.data.client.BlockStateVariantMap;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Model;
import net.minecraft.data.client.ModelIds;
import net.minecraft.data.client.Models;
import net.minecraft.data.client.MultipartBlockStateSupplier;
import net.minecraft.data.client.TextureKey;
import net.minecraft.data.client.TextureMap;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.data.client.VariantsBlockStateSupplier;
import net.minecraft.data.client.When;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

public class ElementalModelProvider extends FabricModelProvider {
    public ElementalModelProvider(FabricDataOutput fabricDataOutput) {
        super(fabricDataOutput);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        registerTriAxisBlock(blockStateModelGenerator, ElementalBlocks.CLOGGED_CONDUIT);
        registerTriAxisBlock(blockStateModelGenerator, ElementalBlocks.OVERGROWN_CONDUIT);
        registerSnow(blockStateModelGenerator, ElementalBlocks.DUST);
    }

    private void registerTriAxisBlock(BlockStateModelGenerator bsmg, Block block) {
                TextureMap tm = new TextureMap().put(TextureKey.TEXTURE, ModelIds.getBlockSubModelId(block, "_core"))
                .put(TextureKey.SIDE, ModelIds.getBlockSubModelId(block, "_axis"))
                .put(TextureKey.END, ModelIds.getBlockSubModelId(block, "_face"));
        Identifier idCore = ElementalModels.TriAxisBlockCore.upload(ModelIds.getBlockSubModelId(block, "_core"), tm,
                bsmg.modelCollector);
        Identifier idAxis = ElementalModels.TriAxisBlockAxis.upload(ModelIds.getBlockSubModelId(block, "_axis"), tm,
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

    private void registerSnow(BlockStateModelGenerator bsmg, Block block) {
        TextureMap tm = TextureMap.all(block);
        Identifier idCubeAll = Models.CUBE_ALL.upload(block, tm, bsmg.modelCollector);
        bsmg.blockStateCollector.accept(VariantsBlockStateSupplier.create(block).coordinate(BlockStateVariantMap
                .create(Properties.LAYERS).register(height -> {
                    Model m = new Model(Optional.of(Identifier.ofVanilla( "block/snow_height" + height * 2)), Optional.empty(),                TextureKey.TEXTURE);
                    Identifier id = ModelIds.getBlockSubModelId(block, "_height" + height * 2);
                    id = m.upload(id, tm, bsmg.modelCollector);
                    return BlockStateVariant.create().put(VariantSettings.MODEL,
                            height < 8 ? id : idCubeAll);
                })));
        bsmg.registerParentedItemModel(block, ModelIds.getBlockSubModelId(block, "_height2"));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
    }
}
