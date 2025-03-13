package juniper.elemental.data;

import java.util.concurrent.CompletableFuture;

import juniper.elemental.init.ElementalBlocks;
import juniper.elemental.init.ElementalItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.block.Block;
import net.minecraft.block.SnowBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.condition.BlockStatePropertyLootCondition;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import net.minecraft.loot.function.LimitCountLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.predicate.StatePredicate;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;

public class ElementalLootTableProvider extends FabricBlockLootTableProvider {
    public ElementalLootTableProvider(FabricDataOutput dataOutput,
            CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        RegistryEntryLookup<Enchantment> enchantments = this.registries.getOrThrow(RegistryKeys.ENCHANTMENT);
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
        addDrop(ElementalBlocks.LIGHT_CRYSTAL);
        addDrop(ElementalBlocks.DARK_BLOCK,
                (Block block) -> this.dropsWithSilkTouch(block,
                        this.applyExplosionDecay(block,
                                ((ItemEntry.builder(ElementalItems.DARK_SHARD).apply(SetCountLootFunction.builder(UniformLootNumberProvider.create(2, 4))))
                                        .apply(ApplyBonusLootFunction.uniformBonusCount(enchantments.getOrThrow(Enchantments.FORTUNE))))
                                                .apply(LimitCountLootFunction.builder(BoundedIntUnaryOperator.create(1, 4))))));
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
