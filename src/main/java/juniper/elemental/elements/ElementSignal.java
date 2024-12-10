package juniper.elemental.elements;

import net.minecraft.util.StringIdentifiable;

public enum ElementSignal implements StringIdentifiable {
    OFF("off", false, false), COOLDOWN1("cooldown1", false, true), COOLDOWN2("cooldown2", false, true), EARTH1("earth1",
            true, true), EARTH2("earth2", true, true), WATER1("water1", true, true), WATER2("water2", true, true), AIR1(
                    "air1", true,
                    true), AIR2("air2", true, true), FIRE1("fire1", true, true), FIRE2("fire2", true, true),;

    public static final ElementSignal[] VALUES = values();
    public final String id;
    public final boolean is_active;
    public final boolean is_transient;

    private ElementSignal(String id, boolean active, boolean is_transient) {
        this.id = id;
        this.is_active = active;
        this.is_transient = is_transient;
    }

    @Override
    public String asString() {
        return id;
    }
}
