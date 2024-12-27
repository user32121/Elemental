package juniper.elemental.init;

import juniper.elemental.Elemental;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ElementalDimensions {
    public static final Identifier DARK_ID = Identifier.of(Elemental.MOD_ID, "dark");
    public static final RegistryKey<World> DARK = RegistryKey.of(RegistryKeys.WORLD, DARK_ID);

    public static void init() {
    }
}
