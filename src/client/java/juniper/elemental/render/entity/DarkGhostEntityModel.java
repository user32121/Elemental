package juniper.elemental.render.entity;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.math.MathHelper;

public class DarkGhostEntityModel extends BipedEntityModel<DarkGhostEntityRenderState> {
    public DarkGhostEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(DarkGhostEntityRenderState dgers) {
        super.setAngles(dgers);
        float f = dgers.armAngle;
        rightArm.pitch = MathHelper.cos(f + MathHelper.PI) * 0.5f;
        leftArm.pitch = MathHelper.cos(f) * 0.5f;
        rightLeg.pitch = MathHelper.cos(f) * 0.7f;
        leftLeg.pitch = MathHelper.cos(f + MathHelper.PI) * 0.7f;
    }
}
