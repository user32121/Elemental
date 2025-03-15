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
    private static final float ATAN_R_1_2 = (float) Math.atan(Math.sqrt(0.5f));

    public LightBallBlockEntityModel(ModelPart root) {
        super(root, RenderLayer::getEntitySolid);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData core = root.addChild("core", ModelPartBuilder.create(), ModelTransform.of(0, 0, 0, PI_4, 0, ATAN_R_1_2));
        // TODO
        core.addChild("cube_1", ModelPartBuilder.create().uv(0, 0).cuboid(-3, -3, -3, 6, 6, 6), ModelTransform.of(0, 0, 0, 0, 0, 0));
        return TexturedModelData.of(modelData, 32, 16);
    }

    public void setAngles(LightBallBlockEntity entity, float tickDelta) {
        root.pivotX = root.pivotY = root.pivotZ = 8;
        double time = entity.getWorld().getTime() + tickDelta;
        root.yaw = (float) ((time / 50) % (Math.PI * 2));
    }
}
