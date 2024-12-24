package juniper.elemental.blockEntities;

import juniper.elemental.init.ElementalBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ExtractorBlockEntity extends BlockEntity {
    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(ElementalBlockEntities.EXTRACTOR, pos, state);
    }
}
