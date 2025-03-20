package juniper.elemental.fluids;

import java.util.Map;

import com.google.common.collect.Maps;

import juniper.elemental.blocks.AlkahestBlock;
import juniper.elemental.init.ElementalBlocks;
import juniper.elemental.init.ElementalItems;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.state.StateManager.Builder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.WorldView;

public class AlkahestFluid extends Fluid {
    private final Map<FluidState, VoxelShape> shapeCache = Maps.newIdentityHashMap();

    public AlkahestFluid() {
        super();
        setDefaultState(getDefaultState().with(AlkahestBlock.LAYERS, AlkahestBlock.MAX_HEIGHT));
    }

    @Override
    protected void appendProperties(Builder<Fluid, FluidState> builder) {
        super.appendProperties(builder);
        builder.add(AlkahestBlock.LAYERS);
    }

    @Override
    public Item getBucketItem() {
        return ElementalItems.ALKAHEST_BUCKET;
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockView world, BlockPos pos, Fluid fluid, Direction dir) {
        return true;
    }

    @Override
    public int getTickRate(WorldView var1) {
        return AlkahestBlock.TICK_RATE;
    }

    @Override
    protected float getBlastResistance() {
        return -1;
    }

    @Override
    public float getHeight(FluidState state, BlockView world, BlockPos pos) {
        return state.getHeight();
    }

    @Override
    public float getHeight(FluidState state) {
        return state.getLevel() / (float) AlkahestBlock.MAX_HEIGHT;
    }

    @Override
    protected BlockState toBlockState(FluidState state) {
        return ElementalBlocks.ALKAHEST.getDefaultState().with(AlkahestBlock.LAYERS, getLevel(state));
    }

    @Override
    public boolean isStill(FluidState state) {
        return true;
    }

    @Override
    public int getLevel(FluidState state) {
        return state.get(AlkahestBlock.LAYERS);
    }

    @Override
    public VoxelShape getShape(FluidState state, BlockView world, BlockPos pos) {
        return this.shapeCache.computeIfAbsent(state, state2 -> VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, state2.getHeight(world, pos), 1.0));
    }

    @Override
    protected Vec3d getVelocity(BlockView var1, BlockPos var2, FluidState var3) {
        return Vec3d.ZERO;
    }
}
