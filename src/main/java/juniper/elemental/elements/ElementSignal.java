package juniper.elemental.elements;

import net.minecraft.util.StringIdentifiable;

public enum ElementSignal implements StringIdentifiable {
    OFF("off", false, false, -1), COOLDOWN("cooldown", false, true, -1), EARTH1("earth1", true, true, 0), EARTH2("earth2", true, true, 0), WATER1("water1", true, true, 1), WATER2("water2", true, true,
            1), AIR1("air1", true, true, 2), AIR2("air2", true, true, 2), FIRE1("fire1", true, true, 3), FIRE2("fire2", true, true, 3);

    public static final ElementSignal[] VALUES = values();
    public final String id;
    public final boolean is_active;
    public final boolean is_transient;
    public final int elementOrdinal; //ordinal for active elements

    private ElementSignal(String id, boolean active, boolean is_transient, int elementOrdinal) {
        this.id = id;
        this.is_active = active;
        this.is_transient = is_transient;
        this.elementOrdinal = elementOrdinal;
    }

    @Override
    public String asString() {
        return id;
    }
}
