package juniper.elemental.init;

import java.util.function.Function;

import juniper.elemental.Elemental;
import juniper.elemental.elements.ElementSignal;
import juniper.elemental.items.AlkahestBucketItem;
import juniper.elemental.items.RadarItem;
import juniper.elemental.items.ShardItem;
import net.minecraft.block.DispenserBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
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
    public static final Item EARTH_SHARD = register("earth_shard", settings -> new ShardItem(settings, ElementSignal.EARTH1));
    public static final Item WATER_SHARD = register("water_shard", settings -> new ShardItem(settings, ElementSignal.WATER1));
    public static final Item AIR_SHARD = register("air_shard", settings -> new ShardItem(settings, ElementSignal.AIR1));
    public static final Item FIRE_SHARD = register("fire_shard", settings -> new ShardItem(settings, ElementSignal.FIRE1));
    public static final Item RADAR = register("radar", RadarItem::new, new Item.Settings().maxCount(1));
    public static final Item DARK_SHARD = register("dark_shard");
    public static final Item ALKAHEST_BUCKET = register("alkahest_bucket", AlkahestBucketItem::new, new Item.Settings().recipeRemainder(Items.BUCKET).maxCount(1));

    public static void init() {
        DispenserBlock.registerBehavior(ALKAHEST_BUCKET, AlkahestBucketItem.dispenserBehavior);
    }

    public static Item register(String name) {
        return register(name, Item::new);
    }

    public static Item register(String name, Function<Item.Settings, Item> factory) {
        return register(name, factory, new Item.Settings());
    }

    public static <T extends Item> T register(String name, Function<Item.Settings, T> factory, Item.Settings settings) {
        Identifier id = Identifier.of(Elemental.MOD_ID, name);
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, id);
        T item = factory.apply(settings.registryKey(key));
        if (item instanceof BlockItem) {
            BlockItem blockItem = (BlockItem) item;
            blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
        }
        ElementalItemGroups.ALL_ITEMS.add(item);
        return Registry.register(Registries.ITEM, key, item);
    }
}
