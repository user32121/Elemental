package juniper.elemental.blockEntities;

import juniper.elemental.init.ElementalBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class LightBallBlockEntity extends BlockEntity {
    public LightBallBlockEntity(BlockPos pos, BlockState state) {
        super(ElementalBlockEntities.LIGHT_BALL, pos, state);
    }
}
