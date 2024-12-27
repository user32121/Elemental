package juniper.elemental.init;

import juniper.elemental.Elemental;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ElementalSounds {
    public static SoundEvent RADAR_PING = register("radar_ping");

    public static void init() {
    }

    public static SoundEvent register(String name) {
        Identifier id = Identifier.of(Elemental.MOD_ID, name);
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
    }
}
