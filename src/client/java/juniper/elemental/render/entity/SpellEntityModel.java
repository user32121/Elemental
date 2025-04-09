package juniper.elemental.render.entity;

import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.util.math.MathHelper;

public class SpellEntityModel extends EntityModel<SpellEntityRenderState> {
    private final ModelPart root;

    public SpellEntityModel(ModelPart root) {
        super(root);
        this.root = root;
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();
        root.addChild("cube1", ModelPartBuilder.create().uv(0, -5).cuboid(0, -2.5f, -2.5f, 0, 5, 5), ModelTransform.NONE.scaled(0.5f));
        root.addChild("cube2", ModelPartBuilder.create().uv(-5, 0).cuboid(-2.5f, 0, -2.5f, 5, 0, 5), ModelTransform.NONE.scaled(0.5f));
        root.addChild("cube3", ModelPartBuilder.create().uv(0, 0).cuboid(-2.5f, -2.5f, 0, 5, 5, 0), ModelTransform.NONE.scaled(0.5f));
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public void setAngles(SpellEntityRenderState state) {
        super.setAngles(state);
        root.setAngles(state.pitch * MathHelper.RADIANS_PER_DEGREE, -state.yaw * MathHelper.RADIANS_PER_DEGREE, 0);
    }
}
