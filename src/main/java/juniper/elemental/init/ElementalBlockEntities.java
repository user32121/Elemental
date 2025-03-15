package juniper.elemental.init;

import juniper.elemental.Elemental;
import juniper.elemental.blockEntities.CondenserBlockEntity;
import juniper.elemental.blockEntities.ExtractorBlockEntity;
import juniper.elemental.blockEntities.LightBallBlockEntity;
import juniper.elemental.blockEntities.LightCrystalBlockEntity;
import juniper.elemental.blockEntities.PackedLightCrystalBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder.Factory;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ElementalBlockEntities {
    public static final BlockEntityType<CondenserBlockEntity> CONDENSER = register("condenser", CondenserBlockEntity::new, ElementalBlocks.CONDENSER);
    public static final BlockEntityType<ExtractorBlockEntity> EXTRACTOR = register("extractor", ExtractorBlockEntity::new, ElementalBlocks.EXTRACTOR);
    public static final BlockEntityType<LightCrystalBlockEntity> LIGHT_CRYSTAL = register("light_crystal", LightCrystalBlockEntity::new, ElementalBlocks.LIGHT_CRYSTAL);
    public static final BlockEntityType<PackedLightCrystalBlockEntity> PACKED_LIGHT_CRYSTAL = register("packed_light_crystal", PackedLightCrystalBlockEntity::new,
            ElementalBlocks.PACKED_LIGHT_CRYSTAL);
    public static final BlockEntityType<LightBallBlockEntity> LIGHT_BALL = register("light_ball", LightBallBlockEntity::new, ElementalBlocks.LIGHT_BALL);

    public static void init() {
    }

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, Factory<T> factory, Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Elemental.MOD_ID, name), FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }
}
