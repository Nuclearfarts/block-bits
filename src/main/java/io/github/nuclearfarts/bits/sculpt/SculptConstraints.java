package io.github.nuclearfarts.bits.sculpt;

import net.minecraft.util.math.Box;

public final class SculptConstraints {

	private SculptConstraints() {}
	
	public static final SculptConstraint ORIGIN = new SculptConstraint(i -> 0, i -> 0, i -> 0);
	public static final SculptConstraint NONE = new SculptConstraint(i -> i, i -> i, i -> i);
	
	public static SculptConstraint forBox(Box box) {
		System.out.println(box);
		return new SculptConstraint(AxisConstraint.boxClampH(box), AxisConstraint.boxClampV(box), AxisConstraint.boxClampDepth(box));
	}
}
