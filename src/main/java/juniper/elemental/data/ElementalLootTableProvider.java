package juniper.elemental.data;

import java.util.concurrent.CompletableFuture;

import juniper.elemental.init.ElementalBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.SnowBlock;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryWrapper;

public class ElementalLootTableProvider extends FabricBlockLootTableProvider {
    public ElementalLootTableProvider(FabricDataOutput dataOutput,
            CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(ElementalBlocks.CONDUIT);
        addDrop(ElementalBlocks.OVERGROWN_CONDUIT);
        addDrop(ElementalBlocks.CLOGGED_CONDUIT);
        addDrop(ElementalBlocks.DUST, this::layeredBlock);
        addDrop(ElementalBlocks.MELTED_CONDUIT);
        addDrop(ElementalBlocks.BLOWN_OUT_CONDUIT);
        addDrop(ElementalBlocks.CONDENSER);
        addDrop(ElementalBlocks.CATALYST);
        addDrop(ElementalBlocks.RICH_SOIL);
        addDrop(ElementalBlocks.EXTRACTOR);
    }

    private Builder layeredBlock(Block block) {
        return LootTable.builder().pool(LootPool.builder()
                .with(AlternativeEntry.builder(SnowBlock.LAYERS.getValues(), layers -> ItemEntry.builder(block)
                        .conditionally(BlockStatePropertyLootCondition.builder(block).properties(
                                StatePredicate.Builder.create().exactMatch(SnowBlock.LAYERS, layers.intValue())))
                        .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(layers.intValue())))))
                .conditionally(createSilkTouchCondition()));
    }
}
