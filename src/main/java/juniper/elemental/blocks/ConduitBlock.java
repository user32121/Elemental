package juniper.elemental.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ConduitBlock extends Block {
    public static final VoxelShape SHAPE = VoxelShapes.union(
            VoxelShapes.cuboid(0, 3 / 8.0, 3 / 8.0, 1, 5 / 8.0, 5 / 8.0),
            VoxelShapes.cuboid(3 / 8.0, 0, 3 / 8.0, 5 / 8.0, 1, 5 / 8.0),
            VoxelShapes.cuboid(3 / 8.0, 3 / 8.0, 0, 5 / 8.0, 5 / 8.0, 1));

    public ConduitBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
