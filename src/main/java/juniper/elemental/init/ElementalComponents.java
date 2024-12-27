package juniper.elemental.init;

import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;

import juniper.elemental.Elemental;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ElementalComponents {
    public static final ComponentType<Long> LAST_PING_TIME = register("last_ping_time", builder -> builder.codec(Codec.LONG).packetCodec(PacketCodecs.LONG));

    public static void init() {
    }

    public static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Elemental.MOD_ID, name), builderOperator.apply(ComponentType.builder()).build());
    }
}
