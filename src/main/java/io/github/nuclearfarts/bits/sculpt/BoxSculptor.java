package io.github.nuclearfarts.bits.sculpt;

import io.github.nuclearfarts.bits.util.BitModUtil;
import io.github.nuclearfarts.bits.util.MutableVec3i;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public class BoxSculptor implements Sculptor {

	private final Box box;
	
	/**H, depth, V*/
	public BoxSculptor(Box box) {
		this.box = box;
	}

	@Override
	public void sculpt(Vec3i hitPos, Direction face, RelativeBitView bits, BlockState with) {
		Box tl = BitModUtil.translate(box, hitPos);
		MutableVec3i placePos = new MutableVec3i(0, 0, 0);
		for(int x = (int)tl.x1; x < tl.x2; x++) {
			for(int y = (int)tl.y1; y < tl.y2; y++) {
				for(int z = (int)tl.z1; z < tl.z2; z++) {
					placePos.setX(x);
					placePos.setY(y);
					placePos.setZ(z);
					bits.putBit(placePos, with);
				}
			}
		}
	}

	@Override
	public VoxelShape getEffectedShape(Vec3i hitPos, Direction face) {
		Box tl = BitModUtil.translate(box, BitModUtil.toRelativeMut(hitPos, face));
		Box rotatedAndDivided = BitModUtil.boxFromVec3i(BitModUtil.fromRelative(new Vec3i(tl.x1, tl.y1, tl.z1), face), BitModUtil.fromRelative(new Vec3i(tl.x2, tl.y2, tl.z2), face));
		System.out.println(rotatedAndDivided);
		return VoxelShapes.cuboid(rotatedAndDivided);
	}
}
