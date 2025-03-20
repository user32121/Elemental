package juniper.elemental.items;

import juniper.elemental.init.ElementalFluids;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FluidFillable;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.FluidModificationItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

//because BucketItem does not like non FlowableFluids
public class AlkahestBucketItem extends BucketItem {
    public static ItemDispenserBehavior dispenserBehavior = new ItemDispenserBehavior() {
        private final ItemDispenserBehavior fallbackBehavior = new ItemDispenserBehavior();

        protected ItemStack dispenseSilently(BlockPointer dispenser, ItemStack stack) {
            if (!(stack.getItem() instanceof FluidModificationItem fmi)) {
                return fallbackBehavior.dispense(dispenser, stack);
            }
            BlockPos blockPos = dispenser.pos().offset(dispenser.state().get(DispenserBlock.FACING));
            ServerWorld world = dispenser.world();
            if (fmi.placeFluid(null, world, blockPos, null)) {
                fmi.onEmptied(null, world, stack, blockPos);
                return decrementStackWithRemainder(dispenser, stack, new ItemStack(Items.BUCKET));
            }
            return fallbackBehavior.dispense(dispenser, stack);
        };
    };

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
        return hitResult != null && placeFluid(player, world, hitResult.getBlockPos().offset(hitResult.getSide()), null);
    }
}
