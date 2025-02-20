package juniper.elemental.render.entity;

import juniper.elemental.entities.DarkGhostEntity;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.util.math.MathHelper;

public class DarkGhostEntityModel extends BipedEntityModel<DarkGhostEntity> {
    public DarkGhostEntityModel(ModelPart modelPart) {
        super(modelPart);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = BipedEntityModel.getModelData(Dilation.NONE, 0.0f);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void setAngles(DarkGhostEntity entity, float f, float g, float h, float i, float j) {
        super.setAngles(entity, f, g, h, i, j);
        float x = entity.getDataTracker().get(DarkGhostEntity.ARM_ANGLE);
        rightArm.pitch = MathHelper.cos(x + MathHelper.PI) * 0.5f;
        leftArm.pitch = MathHelper.cos(x) * 0.5f;
        rightLeg.pitch = MathHelper.cos(x) * 0.7f;
        leftLeg.pitch = MathHelper.cos(x + MathHelper.PI) * 0.7f;
    }
}
