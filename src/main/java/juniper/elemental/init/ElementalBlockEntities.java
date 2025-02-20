package juniper.elemental.init;

import juniper.elemental.Elemental;
import juniper.elemental.blockEntities.CondenserBlockEntity;
import juniper.elemental.blockEntities.ExtractorBlockEntity;
import juniper.elemental.blockEntities.LightCrystalBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BlockEntityType.BlockEntityFactory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ElementalBlockEntities {
    public static final BlockEntityType<CondenserBlockEntity> CONDENSER = register("condenser", CondenserBlockEntity::new, ElementalBlocks.CONDENSER);
    public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR = register("extractor", ExtractorBlockEntity::new, ElementalBlocks.EXTRACTOR);
    public static final BlockEntityType<LightCrystalBlockEntity> LIGHT_CRYSTAL = register("light_crystal", LightCrystalBlockEntity::new, ElementalBlocks.LIGHT_CRYSTAL);

    public static void init() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityFactory<T> factory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Elemental.MOD_ID, name), BlockEntityType.Builder.create(factory, blocks).build());
    }
}
