package juniper.elemental.blockEntities;

import juniper.elemental.init.ElementalBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class PackedLightCrystalBlockEntity extends BlockEntity {
    public PackedLightCrystalBlockEntity(BlockPos pos, BlockState state) {
        super(ElementalBlockEntities.PACKED_LIGHT_CRYSTAL, pos, state);
    }
}
