package juniper.elemental.init;

import java.util.function.Function;

import juniper.elemental.Elemental;
import juniper.elemental.blocks.CatalystBlock;
import juniper.elemental.blocks.CondenserBlock;
import juniper.elemental.blocks.ConduitBlock;
import juniper.elemental.blocks.DarkPortalBlock;
import juniper.elemental.blocks.ExtractorBlock;
import juniper.elemental.blocks.LightCrystalBlock;
import juniper.elemental.blocks.RichSoilBlock;
import juniper.elemental.blocks.TriAxisBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SnowBlock;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ElementalBlocks {
    public static Block CONDUIT = register("conduit", ConduitBlock::new,
            AbstractBlock.Settings.create().strength(1.5f).requiresTool(), true);
    public static Block OVERGROWN_CONDUIT = register("overgrown_conduit", TriAxisBlock::new,
            AbstractBlock.Settings.create().strength(1.5f).requiresTool(), true);
    public static Block CLOGGED_CONDUIT = register("clogged_conduit", TriAxisBlock::new,
            AbstractBlock.Settings.create().strength(1.5f).requiresTool(), true);
    public static Block DUST = register("dust", SnowBlock::new,
                    AbstractBlock.Settings.create().strength(0.5f), true);
    public static Block MELTED_CONDUIT = register("melted_conduit", TriAxisBlock::new,
            AbstractBlock.Settings.create().strength(1.5f).requiresTool(), true);
    public static Block BLOWN_OUT_CONDUIT = register("blown_out_conduit", TriAxisBlock::new,
                    AbstractBlock.Settings.create().strength(1.5f).requiresTool(), true);
    public static Block CONDENSER = register("condenser", CondenserBlock::new,
                    AbstractBlock.Settings.create().strength(1.5f).requiresTool(), true);
    public static Block CATALYST = register("catalyst", CatalystBlock::new,
            AbstractBlock.Settings.create().strength(1.5f).requiresTool(), true);
    public static Block RICH_SOIL = register("rich_soil", RichSoilBlock::new, AbstractBlock.Settings.create().strength(0.5f).ticksRandomly(), true);
    public static Block EXTRACTOR = register("extractor", ExtractorBlock::new, AbstractBlock.Settings.create().strength(1.5f).requiresTool(), true);
    public static Block DARK_BLOCK = register("dark_block", Block::new, AbstractBlock.Settings.create().strength(1.5f).requiresTool().dropsNothing(), true);
    public static Block DARK_PORTAL = register("dark_portal", DarkPortalBlock::new,
            AbstractBlock.Settings.create().noCollision().strength(-1.0f, 3600000.0f).dropsNothing().pistonBehavior(PistonBehavior.BLOCK), false);
    public static Block LIGHT_CRYSTAL = register("light_crystal", LightCrystalBlock::new, AbstractBlock.Settings.create().strength(1.5f).requiresTool(), true);

    public static void init() {
    }

    public static Block register(String name, Function<AbstractBlock.Settings, Block> factory,
            AbstractBlock.Settings settings, boolean shouldRegisterItem) {
        // Register the block and its item.
        Identifier id = Identifier.of(Elemental.MOD_ID, name);
        Block block = factory.apply(settings.registryKey(RegistryKey.of(RegistryKeys.BLOCK, id)));
        // Sometimes, you may not want to register an item for the block.
        // Eg: if it's a technical block like `minecraft:air` or `minecraft:end_gateway`
        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block,
                    new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, id)));
            Registry.register(Registries.ITEM, id, blockItem);
            ElementalItemGroups.ALL_ITEMS.add(blockItem);
        }
        return Registry.register(Registries.BLOCK, id, block);
    }
}
