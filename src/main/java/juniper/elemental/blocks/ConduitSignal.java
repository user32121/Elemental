package juniper.elemental.blocks;

import net.minecraft.util.StringIdentifiable;

public enum ConduitSignal implements StringIdentifiable {
    OFF("off", false, false),
    COOLDOWN("cooldown", false, true),
    EARTH1("earth1", true, true),
    EARTH2("earth2", true, true),
    WATER1("water1", true, true),
    WATER2("water2", true, true),
    AIR1("air1", true, true),
    AIR2("air2", true, true),
    FIRE1("fire1", true, true),
    FIRE2("fire2", true, true),
    ;

    public final String id;
    public final boolean is_active;
    public final boolean is_transient;

    private ConduitSignal(String id, boolean active, boolean is_transient) {
        this.id = id;
        this.is_active = active;
        this.is_transient = is_transient;
    }

    @Override
    public String asString() {
        return id;
    }

}
