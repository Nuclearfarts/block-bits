package io.github.nuclearfarts.bits.sculpt;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

@FunctionalInterface
public interface AxisConstraint {
	public static final AxisConstraint NONE = i -> i;
	
	int constrain(int in);
	
	public static AxisConstraint clamp(int min, int max) {
		return i -> MathHelper.clamp(i, min, max);
	}
	
	public static AxisConstraint boxClampH(Box box) {
		return clamp(0, 16 - (int)box.getXLength());
	}
	
	public static AxisConstraint boxClampV(Box box) {
		return clamp(0, 16 - (int)box.getZLength());
	}
	
	public static AxisConstraint boxClampDepth(Box box) {
		return clamp(0, 16 - (int)box.getYLength());
	}
	
	public static AxisConstraint constant(int constant) {
		return i -> constant;
	}
}
