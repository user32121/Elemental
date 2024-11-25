package juniper.elemental.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ConduitBlock extends Block {
    public static final BooleanProperty AXIS_X = BooleanProperty.of("axis_x");
    public static final BooleanProperty AXIS_Y = BooleanProperty.of("axis_y");
    public static final BooleanProperty AXIS_Z = BooleanProperty.of("axis_z");

    public static final VoxelShape SHAPE_CORE = VoxelShapes.cuboid(3 / 8.0, 3 / 8.0, 3 / 8.0, 5 / 8.0, 5 / 8.0,
            5 / 8.0);
    public static final VoxelShape SHAPE_AXIS_X = VoxelShapes.cuboid(0, 3 / 8.0, 3 / 8.0, 1, 5 / 8.0, 5 / 8.0);
    public static final VoxelShape SHAPE_AXIS_Y = VoxelShapes.cuboid(3 / 8.0, 0, 3 / 8.0, 5 / 8.0, 1, 5 / 8.0);
    public static final VoxelShape SHAPE_AXIS_Z = VoxelShapes.cuboid(3 / 8.0, 3 / 8.0, 0, 5 / 8.0, 5 / 8.0, 1);

    public ConduitBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(getDefaultState().with(AXIS_X, false).with(AXIS_Y, false).with(AXIS_Z, false));
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = SHAPE_CORE;
        if (state.get(AXIS_X)) {
            shape = VoxelShapes.union(shape, SHAPE_AXIS_X);
        }
        if (state.get(AXIS_Y)) {
            shape = VoxelShapes.union(shape, SHAPE_AXIS_Y);
        }
        if (state.get(AXIS_Z)) {
            shape = VoxelShapes.union(shape, SHAPE_AXIS_Z);
        }
        return shape;
    }

    @Override
    protected void appendProperties(Builder<Block, BlockState> builder) {
        builder.add(AXIS_X, AXIS_Y, AXIS_Z);
    }
}
