package juniper.elemental.render.entity;

import net.minecraft.client.render.entity.state.BipedEntityRenderState;

public class DarkGhostEntityRenderState extends BipedEntityRenderState {
    public float armAngle; // angular time value in radians (i.e. 0 would be the beginning of a walk
                           // animation, pi would be halfway through)
}
