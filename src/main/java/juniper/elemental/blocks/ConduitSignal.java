package juniper.elemental.blocks;

import net.minecraft.util.StringIdentifiable;

public enum ConduitSignal implements StringIdentifiable {
    OFF("off"), COOLDOWN("cooldown"), ON("on");

    public final String id;

    private ConduitSignal(String id) {
        this.id = id;
    }

    @Override
    public String asString() {
        return id;
    }

}
