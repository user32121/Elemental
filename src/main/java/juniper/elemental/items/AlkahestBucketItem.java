package juniper.elemental.items;

import juniper.elemental.init.ElementalFluids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidFillable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//TODO unable to pick up alkahest
//because BucketItem does not like non FlowableFluids
public class AlkahestBucketItem extends BucketItem {
    public AlkahestBucketItem(Settings settings) {
        super(ElementalFluids.ALKAHEST, settings);
    }

    @Override
    public boolean placeFluid(PlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof FluidFillable fluidFillable && fluidFillable.tryFillWithFluid(world, pos, state, ElementalFluids.ALKAHEST.getDefaultState())) {
            playEmptyingSound(player, world, pos);
            return true;
        }
        if (state.canBucketPlace(ElementalFluids.ALKAHEST)) {
            world.breakBlock(pos, true);
            world.setBlockState(pos, ElementalFluids.ALKAHEST.getDefaultState().getBlockState());
            return true;
        }
        return hitResult != null && this.placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null);
    }
}
