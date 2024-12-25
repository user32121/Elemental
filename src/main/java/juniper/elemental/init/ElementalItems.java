package juniper.elemental.init;

import java.util.function.Function;

import juniper.elemental.Elemental;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ElementalItems {
    public static final Item EARTH_FRAGMENT = register("earth_fragment");
    public static final Item WATER_FRAGMENT = register("water_fragment");
    public static final Item AIR_FRAGMENT = register("air_fragment");
    public static final Item FIRE_FRAGMENT = register("fire_fragment");
    //TODO allow apply elements to conduits
    public static final Item EARTH_SHARD = register("earth_shard");
    public static final Item WATER_SHARD = register("water_shard");
    public static final Item AIR_SHARD = register("air_shard");
    public static final Item FIRE_SHARD = register("fire_shard");

    public static void init() {
    }

    public static Item register(String name) {
        return register(name, Item::new, new Item.Settings());
    }

    public static Item register(String name, Function<Item.Settings, Item> factory, Item.Settings settings) {
        Identifier id = Identifier.of(Elemental.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        Item item = factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem) item;
            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        }
        ElementalItemGroups.ALL_ITEMS.add(item);
        return Registry.register(Registries.ITEM, key, item);
    }
}
