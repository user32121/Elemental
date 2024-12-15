package juniper.elemental.render.entity;

import org.joml.Vector3f;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;

public class CraftingEntityModel extends EntityModel<CraftingEntityRenderState> {
    public static final String CIRCLE_EARTH_KEY = "circle_earth";
    public static final String CIRCLE_WATER_KEY = "circle_Water";
    public static final String CIRCLE_AIR_KEY = "circle_air";
    public static final String CIRCLE_FIRE_KEY = "circle_fire";
    private final ModelPart circleEarth;
    private final ModelPart circleWater;
    private final ModelPart circleAir;
    private final ModelPart circleFire;

    public CraftingEntityModel(ModelPart root) {
        super(root);
        circleEarth = root.getChild(CIRCLE_EARTH_KEY);
        circleWater = root.getChild(CIRCLE_WATER_KEY);
        circleAir = root.getChild(CIRCLE_AIR_KEY);
        circleFire = root.getChild(CIRCLE_FIRE_KEY);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        root.addChild(CIRCLE_EARTH_KEY, ModelPartBuilder.create().uv(0, 16 * 0).cuboid(-8, 0.13f, -8, 16, 0, 16), ModelTransform.NONE.withScale(2));
        root.addChild(CIRCLE_WATER_KEY, ModelPartBuilder.create().uv(0, 16 * 1).cuboid(-8, 0.12f, -8, 16, 0, 16), ModelTransform.NONE.withScale(2));
        root.addChild(CIRCLE_AIR_KEY, ModelPartBuilder.create().uv(0, 16 * 2).cuboid(-8, 0.11f, -8, 16, 0, 16), ModelTransform.NONE.withScale(2));
        root.addChild(CIRCLE_FIRE_KEY, ModelPartBuilder.create().uv(0, 16 * 3).cuboid(-8, 0.10f, -8, 16, 0, 16), ModelTransform.NONE.withScale(2));
        return TexturedModelData.of(modelData, 16, 64);
    }

    @Override
    public void setAngles(CraftingEntityRenderState state) {
        // TODO Auto-generated method stub
        super.setAngles(state);
        circleEarth.rotate(new Vector3f(0, state.age / 10, 0));
        circleWater.rotate(new Vector3f(0, state.age / 10, 0));
        circleAir.rotate(new Vector3f(0, -state.age / 10, 0));
        circleFire.rotate(new Vector3f(0, -state.age / 10, 0));
        circleWater.visible = false;
        circleFire.visible = false;
    }
}
