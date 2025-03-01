package juniper.elemental.init;

import java.util.ArrayList;
import java.util.List;

import juniper.elemental.Elemental;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ElementalItemGroups {
    public static final RegistryKey<ItemGroup> ELEMENTAL_ITEM_GROUP_KEY = register("elemental");
    public static final ItemGroup ELEMENTAL_ITEM_GROUP = FabricItemGroup.builder()
            .displayName(Text.translatable("itemGroup." + Elemental.MOD_ID))
            .icon(() -> new ItemStack(ElementalBlocks.CONDUIT))
            .build();

    public static final List<ItemConvertible> ALL_ITEMS = new ArrayList<>();

    public static void init() {
        Registry.register(Registries.ITEM_GROUP, ELEMENTAL_ITEM_GROUP_KEY, ELEMENTAL_ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(ELEMENTAL_ITEM_GROUP_KEY).register((itemGroup) -> {
            for (ItemConvertible item : ALL_ITEMS) {
                itemGroup.add(item);
            }
        });
    }

    private static RegistryKey<ItemGroup> register(String id) {
        return RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(Elemental.MOD_ID, id));
    }

}
