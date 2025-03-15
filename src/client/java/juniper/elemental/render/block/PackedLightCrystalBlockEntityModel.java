package juniper.elemental.render.block;

import juniper.elemental.blockEntities.PackedLightCrystalBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.MathHelper;

public class PackedLightCrystalBlockEntityModel extends Model {
    private static final float PI_8 = MathHelper.PI / 6;
    private static final float ATAN_R_1_2 = (float) Math.atan(Math.sqrt(0.5f));

    public PackedLightCrystalBlockEntityModel(ModelPart root) {
        super(root, RenderLayer::getEntitySolid);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData core = root.addChild("core", ModelPartBuilder.create(), ModelTransform.of(0, 0, 0, PI_8, 0, ATAN_R_1_2));
        core.addChild("cube_1", ModelPartBuilder.create().uv(0, 0).cuboid(-3, -3, -3, 6, 6, 6), ModelTransform.of(0, 0, 0, PI_8, 0, 0));
        core.addChild("cube_2", ModelPartBuilder.create().uv(0, 0).cuboid(-3, -3, -3, 6, 6, 6), ModelTransform.of(0, 0, 0, -PI_8, 0, 0));
        core.addChild("cube_3", ModelPartBuilder.create().uv(0, 0).cuboid(-3, -3, -3, 6, 6, 6), ModelTransform.of(0, 0, 0, 0, PI_8, 0));
        core.addChild("cube_4", ModelPartBuilder.create().uv(0, 0).cuboid(-3, -3, -3, 6, 6, 6), ModelTransform.of(0, 0, 0, 0, -PI_8, 0));
        core.addChild("cube_5", ModelPartBuilder.create().uv(0, 0).cuboid(-3, -3, -3, 6, 6, 6), ModelTransform.of(0, 0, 0, 0, 0, PI_8));
        core.addChild("cube_6", ModelPartBuilder.create().uv(0, 0).cuboid(-3, -3, -3, 6, 6, 6), ModelTransform.of(0, 0, 0, 0, 0, -PI_8));
        return TexturedModelData.of(modelData, 32, 16);
    }

    public void setAngles(PackedLightCrystalBlockEntity entity, float tickDelta) {
        root.pivotX = root.pivotY = root.pivotZ = 8;
        double time = entity.getWorld().getTime() + tickDelta;
        root.yaw = (float) ((time / 50) % (Math.PI * 2));
    }
}
