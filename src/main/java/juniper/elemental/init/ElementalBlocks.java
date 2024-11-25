package juniper.elemental.init;

import java.util.function.Function;

import juniper.elemental.Elemental;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ElementalBlocks {
    public static Block CONDUIT = register("conduit", Block::new, AbstractBlock.Settings.create(), true);

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
        }

        return Registry.register(Registries.BLOCK, id, block);
    }
}
