package io.github.nuclearfarts.bits.util;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

public class MutableVec3i extends Vec3i {

	private int x;
	private int y;
	private int z;
	
	public MutableVec3i(int x, int y, int z) {
		super(x, y, z);
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public MutableVec3i(double x, double y, double z) {
		this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setZ(int z) {
		this.z = z;
	}
}
