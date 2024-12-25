package juniper.elemental.items;

import juniper.elemental.blocks.ElementHolder;
import juniper.elemental.elements.ElementSignal;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ShardItem extends Item {
    private final ElementSignal signal;

    public ShardItem(Settings settings, ElementSignal signal) {
        super(settings);
        this.signal = signal;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        ItemStack stack = context.getStack();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() instanceof ElementHolder && !state.get(ElementHolder.SIGNAL).is_transient) {
            if (world.isClient()) {
                return ActionResult.SUCCESS;
            }
            world.setBlockState(pos, state.with(ElementHolder.SIGNAL, signal));
            stack.decrement(1);
            return ActionResult.SUCCESS_SERVER;
        }
        return super.useOnBlock(context);
    }
}
