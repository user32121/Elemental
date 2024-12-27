package juniper.elemental.blocks;

import com.mojang.serialization.MapCodec;

import juniper.elemental.blockEntities.DarkPortalBlockEntity;
import juniper.elemental.init.ElementalDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.Portal;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.TeleportTarget.PostDimensionTransition;
import net.minecraft.world.World;

public class DarkPortalBlock extends BlockWithEntity implements Portal {
    public DarkPortalBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DarkPortalBlockEntity(pos, state);
    }

    @Override
    public TeleportTarget createTeleportTarget(ServerWorld world, Entity entity, BlockPos pos) {
        ServerWorld targetWorld = world.getServer().getWorld(ElementalDimensions.DARK);
        pos = pos.add(targetWorld.getRandom().nextBetween(-64, 64), 0, targetWorld.getRandom().nextBetween(-64, 64)).withY(targetWorld.getBottomY() + targetWorld.getHeight());
        BlockState state;
        while (true) {
            BlockPos newPos = pos.add(0, -8, 0);
            state = targetWorld.getBlockState(newPos);
            if (state.isOf(Blocks.VOID_AIR) || !state.isAir()) {
                break;
            }
            pos = newPos;
        }
        PostDimensionTransition pdt = TeleportTarget.SEND_TRAVEL_THROUGH_PORTAL_PACKET.then(TeleportTarget.ADD_PORTAL_CHUNK_TICKET);
        return new TeleportTarget(targetWorld, pos.toBottomCenterPos(), Vec3d.ZERO, 0, 0, pdt);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(DarkPortalBlock::new);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!entity.canUsePortals(false)) {
            return;
        }
        entity.tryUsePortal(this, pos);
        world.breakBlock(pos, false);
    }
}
