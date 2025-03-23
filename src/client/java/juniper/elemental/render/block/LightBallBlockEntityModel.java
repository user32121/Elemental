package juniper.elemental.render.block;

import juniper.elemental.blockEntities.LightBallBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.MathHelper;

public class LightBallBlockEntityModel extends Model {
    private static final float PI_4 = MathHelper.PI / 4;
    private static final float PI_6 = MathHelper.PI / 6;
    private static final float TAN_PI_12 = (float) Math.tan(Math.PI / 12);
    private static final float ATAN_R_1_2 = (float) Math.atan(Math.sqrt(0.5f));
    private static final float ATAN_R2_TP12 = (float) Math.atan(Math.sqrt(2) * TAN_PI_12);

    public LightBallBlockEntityModel(ModelPart root) {
        super(root, RenderLayer::getEntitySolid);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData core = root.addChild("core", ModelPartBuilder.create(), ModelTransform.of(0, 0, 0, PI_4, 0, ATAN_R_1_2));

        int quads = 0;
        int groups = 0;
        //xy ring
        for (int i = 0; i < 12; ++i) {
            core.addChild("quad_" + ++quads, ModelPartBuilder.create().uv(4, 0).cuboid(-1, 1 / TAN_PI_12, -1, 2, 0, 2), ModelTransform.of(0, 0, 0, 0, 0, i * PI_6));
        }
        //yz ring
        for (int i = 0; i < 12; ++i) {
            if (i % 6 != 0) {
                core.addChild("quad_" + ++quads, ModelPartBuilder.create().uv(4, 0).cuboid(-1, 1 / TAN_PI_12, -1, 2, 0, 2), ModelTransform.of(0, 0, 0, i * PI_6, 0, 0));
            }
        }
        //xz ring
        for (int i = 0; i < 12; ++i) {
            if (i % 3 != 0) {
                core.addChild("quad_" + ++quads, ModelPartBuilder.create().uv(4, 0).cuboid(-1, 1 / TAN_PI_12, -1, 2, 0, 2), ModelTransform.of(0, 0, 0, MathHelper.PI / 2, i * PI_6, 0));
            }
        }
        //octants
        for (int i = 0; i < 8; ++i) {
            int i1 = (i & 1) == 0 ? 0 : 1;
            int i2 = (i & 2) == 0 ? -1 : 1;
            int i3 = (i & 4) == 0 ? -1 : 1;
            core.addChild("group_" + ++groups, ModelPartBuilder.create(), ModelTransform.of(0, 0, 0, i3 * 2 * ATAN_R2_TP12, i2 * PI_4, i1 * MathHelper.PI)).addChild("quad_" + ++quads,
                    ModelPartBuilder.create().uv(4, 0).cuboid(-1, 1 / TAN_PI_12, -1, 2, 0, 2), ModelTransform.of(0, 0, 0, 0, i2 * -PI_4, 0));
            core.addChild("group_" + ++groups, ModelPartBuilder.create(), ModelTransform.of(0, 0, 0, i2 * PI_4, 0, i1 * MathHelper.PI)).addChild("quad_" + ++quads,
                    ModelPartBuilder.create().uv(4, 0).cuboid(1 / TAN_PI_12, -1, -1, 0, 2, 2), ModelTransform.of(0, 0, 0, i2 * -PI_4, 0, i3 * 2 * ATAN_R2_TP12));
            core.addChild("group_" + ++groups, ModelPartBuilder.create(), ModelTransform.of(0, 0, 0, i3 * -2 * ATAN_R2_TP12, i1 * MathHelper.PI, i2 * -PI_4)).addChild("quad_" + ++quads,
                    ModelPartBuilder.create().uv(4, 0).cuboid(-1, -1, 1 / TAN_PI_12, 2, 2, 0), ModelTransform.of(0, 0, 0, 0, 0, i2 * PI_4));
        }

        return TexturedModelData.of(modelData, 32, 16);
    }

    public void setAngles(LightBallBlockEntity entity, float tickDelta) {
        root.pivotX = root.pivotY = root.pivotZ = 8;
        double time = entity.getWorld().getTime() + tickDelta;
        root.yaw = (float) ((time / 50) % (Math.PI * 2));
    }
}
