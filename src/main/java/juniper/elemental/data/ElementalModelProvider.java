package juniper.elemental.data;

import java.util.Optional;

import juniper.elemental.blocks.ConduitBlock;
import juniper.elemental.blocks.ElementHolder;
import juniper.elemental.blocks.TriAxisBlock;
import juniper.elemental.elements.ElementSignal;
import juniper.elemental.init.ElementalBlocks;
import juniper.elemental.init.ElementalItems;
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
        registerConduit(blockStateModelGenerator, ElementalBlocks.CONDUIT);
        registerTriAxisBlock(blockStateModelGenerator, ElementalBlocks.CLOGGED_CONDUIT);
        registerTriAxisBlock(blockStateModelGenerator, ElementalBlocks.OVERGROWN_CONDUIT);
        registerSnow(blockStateModelGenerator, ElementalBlocks.DUST);
        registerTriAxisBlock(blockStateModelGenerator, ElementalBlocks.MELTED_CONDUIT);
        registerTriAxisBlock(blockStateModelGenerator, ElementalBlocks.BLOWN_OUT_CONDUIT);
        registerSignalHolder(blockStateModelGenerator, ElementalBlocks.CONDENSER);
        registerSignalHolder(blockStateModelGenerator, ElementalBlocks.CATALYST);
        blockStateModelGenerator.registerSimpleCubeAll(ElementalBlocks.RICH_SOIL);
        blockStateModelGenerator.registerSimpleCubeAll(ElementalBlocks.EXTRACTOR);
        blockStateModelGenerator.registerSimpleCubeAll(ElementalBlocks.DARK_BLOCK);
        blockStateModelGenerator.registerSimpleCubeAll(ElementalBlocks.DARK_PORTAL);
        blockStateModelGenerator.registerBuiltin(ElementalBlocks.LIGHT_CRYSTAL, ElementalBlocks.LIGHT_CRYSTAL).includeWithItem(ElementalBlocks.LIGHT_CRYSTAL);
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

    private void registerConduit(BlockStateModelGenerator bsmg, Block block) {
        Identifier idItem = null;
        MultipartBlockStateSupplier mbss = MultipartBlockStateSupplier.create(block);
        for (ElementSignal signal : ConduitBlock.SIGNAL.getValues()) {
            String signalName = signal.asString();
            ElementSignal complementarySignal = signal;
            if (signalName.endsWith("2")) {
                continue;
            } else if (signalName.endsWith("1")) {
                signalName = signalName.substring(0, signalName.length() - 1);
                String signalStr = signal.toString();
                complementarySignal = ElementSignal.valueOf(signalStr.substring(0, signalStr.length() - 1) + "2");
            }
            TextureMap tm = new TextureMap()
                    .put(TextureKey.TEXTURE, ModelIds.getBlockSubModelId(block, "_core_" + signalName))
                    .put(TextureKey.SIDE, ModelIds.getBlockSubModelId(block, "_axis_" + signalName))
                    .put(TextureKey.END, ModelIds.getBlockSubModelId(block, "_face_" + signalName));
            Identifier idCore = ElementalModels.TriAxisBlockCore
                    .upload(ModelIds.getBlockSubModelId(block, "_core_" + signalName), tm, bsmg.modelCollector);
            Identifier idAxis = ElementalModels.TriAxisBlockAxis
                    .upload(ModelIds.getBlockSubModelId(block, "_axis_" + signalName), tm, bsmg.modelCollector);
            if (signal.equals(ElementSignal.OFF)) {
                idItem = idAxis;
            }
            mbss = mbss
                    .with(When.create().set(ConduitBlock.SIGNAL, signal, complementarySignal),
                            BlockStateVariant.create().put(VariantSettings.MODEL, idCore))
                    .with(When.create().set(TriAxisBlock.AXIS_X, true).set(ConduitBlock.SIGNAL, signal,
                            complementarySignal),
                            BlockStateVariant.create().put(VariantSettings.MODEL, idAxis).put(VariantSettings.Y,
                                    VariantSettings.Rotation.R90))
                    .with(When.create().set(TriAxisBlock.AXIS_Y, true).set(ConduitBlock.SIGNAL, signal,
                            complementarySignal),
                            BlockStateVariant.create().put(VariantSettings.MODEL, idAxis).put(VariantSettings.X,
                                    VariantSettings.Rotation.R90))
                    .with(When.create().set(TriAxisBlock.AXIS_Z, true).set(ConduitBlock.SIGNAL, signal,
                            complementarySignal), BlockStateVariant.create().put(VariantSettings.MODEL, idAxis));
        }
        bsmg.blockStateCollector.accept(mbss);
        bsmg.registerParentedItemModel(block, idItem);
    }

    private void registerSnow(BlockStateModelGenerator bsmg, Block block) {
        TextureMap tm = TextureMap.all(block);
        Identifier idCubeAll = Models.CUBE_ALL.upload(block, tm, bsmg.modelCollector);
        bsmg.blockStateCollector.accept(VariantsBlockStateSupplier.create(block)
                .coordinate(BlockStateVariantMap.create(Properties.LAYERS).register(height -> {
                    Model m = new Model(Optional.of(Identifier.ofVanilla("block/snow_height" + height * 2)),
                            Optional.empty(), TextureKey.TEXTURE);
                    Identifier id = ModelIds.getBlockSubModelId(block, "_height" + height * 2);
                    id = m.upload(id, tm, bsmg.modelCollector);
                    return BlockStateVariant.create().put(VariantSettings.MODEL, height < 8 ? id : idCubeAll);
                })));
        bsmg.registerParentedItemModel(block, ModelIds.getBlockSubModelId(block, "_height2"));
    }

    private void registerSignalHolder(BlockStateModelGenerator bsmg, Block block) {
        bsmg.blockStateCollector.accept(VariantsBlockStateSupplier.create(block)
                        .coordinate(BlockStateVariantMap.create(ElementHolder.SIGNAL).register(signal -> {
                    String signalName = signal.asString();
                    if (signalName.endsWith("1") || signalName.endsWith("2")) {
                        signalName = signalName.substring(0, signalName.length() - 1);
                    }
                    Identifier id = ModelIds.getBlockSubModelId(block, "_" + signalName);
                    if (!signal.asString().endsWith("2")) {
                        TextureMap tm = TextureMap.all(id);
                        id = Models.CUBE_ALL.upload(id, tm, bsmg.modelCollector);
                    }
                    return BlockStateVariant.create().put(VariantSettings.MODEL, id);
                })));
        bsmg.registerParentedItemModel(block, ModelIds.getBlockSubModelId(block, "_off"));
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(ElementalItems.EARTH_FRAGMENT, Models.GENERATED);
        itemModelGenerator.register(ElementalItems.WATER_FRAGMENT, Models.GENERATED);
        itemModelGenerator.register(ElementalItems.AIR_FRAGMENT, Models.GENERATED);
        itemModelGenerator.register(ElementalItems.FIRE_FRAGMENT, Models.GENERATED);
        itemModelGenerator.register(ElementalItems.EARTH_SHARD, Models.GENERATED);
        itemModelGenerator.register(ElementalItems.WATER_SHARD, Models.GENERATED);
        itemModelGenerator.register(ElementalItems.AIR_SHARD, Models.GENERATED);
        itemModelGenerator.register(ElementalItems.FIRE_SHARD, Models.GENERATED);
        itemModelGenerator.register(ElementalItems.RADAR, Models.GENERATED);
    }
}
