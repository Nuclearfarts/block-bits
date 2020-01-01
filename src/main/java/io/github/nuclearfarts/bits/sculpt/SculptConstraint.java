package io.github.nuclearfarts.bits.sculpt;

import net.minecraft.util.math.Vec3i;

public final class SculptConstraint {
	
	public final AxisConstraint hConstraint;
	public final AxisConstraint vConstraint;
	public final AxisConstraint depthConstraint;
	
	public SculptConstraint(AxisConstraint hConstraint, AxisConstraint vConstraint, AxisConstraint depthConstraint) {
		this.hConstraint = hConstraint;
		this.vConstraint = vConstraint;
		this.depthConstraint = depthConstraint;
	}
	
	public Vec3i apply(Vec3i to) {
		return new Vec3i(hConstraint.constrain(to.getX()), depthConstraint.constrain(to.getY()), vConstraint.constrain(to.getZ()));
	}
}
