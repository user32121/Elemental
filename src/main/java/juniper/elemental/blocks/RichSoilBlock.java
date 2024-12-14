package juniper.elemental.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Fertilizable;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;

public class RichSoilBlock extends Block {
    public RichSoilBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.randomTick(state, world, pos, random);
        for (int i = 1; i <= 2; ++i) {
            BlockPos targetPos = pos.up(i);
            BlockState targetState = world.getBlockState(targetPos);
            if (targetState.getBlock() instanceof Fertilizable fer && fer.isFertilizable(world, targetPos, targetState) && fer.canGrow(world, random, targetPos, targetState)) {
                fer.grow(world, random, targetPos, targetState);
            }
        }
        if (random.nextFloat() < 0.1) {
            world.setBlockState(pos, Blocks.DIRT.getDefaultState());
        }
    }
}
