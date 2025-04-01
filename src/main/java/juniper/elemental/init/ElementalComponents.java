package juniper.elemental.init;

import java.util.function.UnaryOperator;

import juniper.elemental.Elemental;
import juniper.elemental.spells.WandSpell;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ElementalComponents {
    public static final ComponentType<WandSpell> SPELL = register("Spell", builder -> builder.codec(WandSpell.CODEC));

    public static void init() {
    }

    public static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(Elemental.MOD_ID, name), builderOperator.apply(ComponentType.builder()).build());
    }
}
