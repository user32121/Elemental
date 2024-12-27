package juniper.elemental.blockEntities;

import juniper.elemental.init.ElementalBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class DarkPortalBlockEntity extends BlockEntity {
    public DarkPortalBlockEntity(BlockPos pos, BlockState state) {
        super(ElementalBlockEntities.DARK_PORTAL, pos, state);
    }
}
