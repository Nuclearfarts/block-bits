package io.github.nuclearfarts.bits.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public final class BitModUtil {
	private BitModUtil() {}
	
	public static Vec3i getSubPosForPlacement(BlockPos blockPos, Vec3d hitPos, Direction side) {
		Vec3d mod = hitPos.subtract(blockPos.getX(), blockPos.getY(), blockPos.getZ());
		int x = (int)Math.ceil(mod.x * 16) - 1 + MathHelper.clamp(side.getOffsetX(), 0, 1);
		int y = (int)Math.ceil(mod.y * 16) - 1 + MathHelper.clamp(side.getOffsetY(), 0, 1);
		int z = (int)Math.ceil(mod.z * 16) - 1 + MathHelper.clamp(side.getOffsetZ(), 0, 1);
		return new Vec3i(x, y, z);
	}
	
	public static Vec3i getSubPos(BlockPos pos, Vec3d hitPos, Direction side) {
		return getSubPosForPlacement(pos, hitPos, side).offset(side, -1);
	}
	
	private static final MutableVec3i MUT_INSTANCE = new MutableVec3i(0, 0, 0);
	
	/**H, DEPTH, V. 0-16 only. THIS RE-USES A MUTABLE EACH TIME IS IS CALLED. CONSTRUCT A NEW ONE MANUALLY IF NEEDED.*/
	public static Vec3i toRelativeMut(Vec3i in, Direction to) {
		switch(to) {
		case NORTH:
			return reuse(15 - in.getX(), in.getZ(), in.getY());
		case SOUTH:
			return reuse(in.getX(), 15 - in.getZ(), in.getY());
		case EAST:
			return reuse(15 - in.getZ(), 15 - in.getX(), in.getY());
		case WEST:
			return reuse(in.getZ(), in.getX(), in.getY());
		case UP:
			return reuse(in.getX(), 15 - in.getY(), in.getZ());
		case DOWN:
			return reuse(in.getX(), in.getY(), in.getZ());
		default:
			throw new RuntimeException("Congrats, you broke physics. Or enums. Probably enums.");
		}
	}
	
	public static Vec3i toRelative(Vec3i in, Direction to) {
		switch(to) {
		case NORTH:
			return new Vec3i(15 - in.getX(), in.getZ(), in.getY());
		case SOUTH:
			return new Vec3i(in.getX(), 15 - in.getZ(), in.getY());
		case EAST:
			return new Vec3i(15 - in.getZ(), 15 - in.getX(), in.getY());
		case WEST:
			return new Vec3i(in.getZ(), in.getX(), in.getY());
		case UP:
			return new Vec3i(in.getX(), 15 - in.getY(), in.getZ());
		case DOWN:
			return new Vec3i(in.getX(), in.getY(), in.getZ());
		default:
			throw new RuntimeException("Congrats, you broke physics. Or enums. Probably enums.");
		}
	}
	
	/**H, DEPTH, V. 0-15. THIS RE-USES A MUTABLE EACH TIME IS IS CALLED. CONSTRUCT A NEW ONE MANUALLY IF NEEDED.*/
	public static Vec3i fromRelativeMut(Vec3i in, Direction to) {
		switch(to) {
		case NORTH:
			return reuse(15 - in.getX(), in.getZ(), in.getY());
		case SOUTH:
			return reuse(in.getX(), in.getZ(), 15 - in.getY());
		case EAST:
			return reuse(15 - in.getY(), in.getZ(), 15 - in.getX());
		case WEST:
			return reuse(in.getY(), in.getZ(), in.getX());
		case UP:
			return reuse(in.getX(), 15 - in.getY(), in.getZ());
		case DOWN:
			return reuse(in.getX(), in.getY(), in.getZ());
		default:
			throw new RuntimeException("Congrats, you broke physics. Or enums. Probably enums.");
		}
	}
	
	public static Vec3i fromRelative(Vec3i in, Direction to) {
		switch(to) {
		case NORTH:
			return new Vec3i(15 - in.getX(), in.getZ(), in.getY());
		case SOUTH:
			return new Vec3i(in.getX(), in.getZ(), 15 - in.getY());
		case EAST:
			return new Vec3i(15 - in.getY(), in.getZ(), 15 - in.getX());
		case WEST:
			return new Vec3i(in.getY(), in.getZ(), in.getX());
		case UP:
			return new Vec3i(in.getX(), 15 - in.getY(), in.getZ());
		case DOWN:
			return new Vec3i(in.getX(), in.getY(), in.getZ());
		default:
			throw new RuntimeException("Congrats, you broke physics. Or enums. Probably enums.");
		}
	}
	
	public static Vec3i clone(Vec3i vec) {
		return new Vec3i(vec.getX(), vec.getY(), vec.getZ());
	}
	
	public static Box translate(Box box, Vec3i vec) {
		return new Box(box.x1 + vec.getX(), box.y1 + vec.getY(), box.z1 + vec.getZ(), box.x2 + vec.getX(), box.y2 + vec.getY(), box.z2 + vec.getZ());
	}
	
	private static Vec3i reuse(int x, int y, int z) {
		MUT_INSTANCE.setX(x);
		MUT_INSTANCE.setY(y);
		MUT_INSTANCE.setZ(z);
		return MUT_INSTANCE;
		//return new Vec3i(x, y, z);
	}
	
	/**Note: This expects 16ths of a block as unit.*/
	public static Box boxFromVec3i(Vec3i vec1, Vec3i vec2) {
		return new Box(vec1.getX() / 16d, vec1.getY() / 16d, vec1.getZ() / 16d, vec2.getX() / 16d, vec2.getY() / 16d, vec2.getZ() / 16d);
	}
	
	public static Box fromRelativeBox(Box box, Direction to) {
		Vec3i corner1 = new Vec3i(box.x1, box.y1, box.z1);
		Vec3i corner2 = new Vec3i(box.x2, box.y2, box.z2);
		return boxFromVec3i(fromRelative(corner1, to), toRelative(corner2, to));
	}
	
	public static Box translateBy16ths(Box box, Vec3i vec) {
		return new Box(box.x1 + vec.getX() / 16d, box.y1 + vec.getY() / 16d, box.z1 + vec.getZ() / 16d, box.x2 + vec.getX() / 16d, box.y2 + vec.getY() / 16d, box.z2 + vec.getZ() / 16d);
	}
	
	public static Box boxAtOrigin(int x, int y, int z) {
		return new Box(0, 0, 0, x, y, z);
	}
}
