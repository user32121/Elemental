package juniper.elemental.blocks;

import com.mojang.serialization.MapCodec;

import juniper.elemental.blockEntities.LightCrystalBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

//TODO radar render
public class LightCrystalBlock extends BlockWithEntity {
    public static final VoxelShape SHAPE = VoxelShapes.cuboid(1 / 5.0, 1 / 5.0, 1 / 5.0, 4 / 5.0, 4 / 5.0, 4 / 5.0);

    public LightCrystalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LightCrystalBlockEntity(pos, state);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(LightCrystalBlock::new);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
