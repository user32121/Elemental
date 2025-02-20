package juniper.elemental.render.block;

import juniper.elemental.blockEntities.LightCrystalBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class LightCrystalBlockEntityModel extends Model {
    public static final String CORE_KEY = "core";
    public static final String CUBE_1_KEY = "cube_1";
    public static final String CUBE_2_KEY = "cube_2";
    public static final String CUBE_3_KEY = "cube_3";
    private static final float PI_4 = MathHelper.PI / 4;
    private static final float ATAN_R_1_2 = (float) Math.atan(Math.sqrt(0.5f));

    private final ModelPart root;

    public LightCrystalBlockEntityModel(ModelPart root) {
        super(RenderLayer::getEntitySolid);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        ModelPartData core = root.addChild(CORE_KEY, ModelPartBuilder.create(), ModelTransform.of(0, 0, 0, PI_4, 0, ATAN_R_1_2));
        core.addChild(CUBE_1_KEY, ModelPartBuilder.create().uv(0, 0).cuboid(-3, -3, -3, 6, 6, 6), ModelTransform.of(0, 0, 0, PI_4, 0, 0));
        core.addChild(CUBE_2_KEY, ModelPartBuilder.create().uv(0, 0).cuboid(-3, -3, -3, 6, 6, 6), ModelTransform.of(0, 0, 0, 0, PI_4, 0));
        core.addChild(CUBE_3_KEY, ModelPartBuilder.create().uv(0, 0).cuboid(-3, -3, -3, 6, 6, 6), ModelTransform.of(0, 0, 0, 0, 0, PI_4));
        return TexturedModelData.of(modelData, 32, 16);
    }

    public void setAngles(LightCrystalBlockEntity entity, float tickDelta) {
        root.pivotX = root.pivotY = root.pivotZ = 8;
        double time = entity.getWorld().getTime() + tickDelta;
        root.yaw = (float) ((time / 50) % (Math.PI * 2));
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        root.render(matrices, vertices, light, overlay, color);
    }
}
