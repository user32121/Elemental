package juniper.elemental.init;

import juniper.elemental.Elemental;
import juniper.elemental.blockEntities.CondenserBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ElementalBlockEntities {
    public static final BlockEntityType<CondenserBlockEntity> CONDENSER_BLOCK_ENTITY = register("condenser",
            CondenserBlockEntity::new, ElementalBlocks.CONDENSER);

    public static void init() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, Factory<T> factory,
            Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Elemental.MOD_ID, name),
                FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }
}
