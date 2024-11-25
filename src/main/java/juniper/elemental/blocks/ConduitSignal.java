package juniper.elemental.blocks;

import net.minecraft.util.StringIdentifiable;

public enum ConduitSignal implements StringIdentifiable {
    OFF("off", false, false), COOLDOWN("cooldown", false, true), ON1("on1", true, true), ON2("on2", true, true);

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
