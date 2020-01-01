package io.github.nuclearfarts.bits.sculpt;

import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import io.github.nuclearfarts.bits.util.BitModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;

public final class SculptMode {
	
	int index;
	
	private final SculptConstraint constraint;
	private final Sculptor sculptor;
	public final String name;
	
	public SculptMode(SculptConstraint constraint, Sculptor sculptor, String name) {
		this.constraint = constraint;
		this.sculptor = sculptor;
		this.name = name;
	}
	
	public void sculpt(Vec3i hitPos, Direction hitDirection, BitBlockEntity bits, BlockState state) {
		RelativeBitView view = new RelativeBitView(bits, hitDirection);
		//System.out.println("Absolute: " + hitPos);
		hitPos = BitModUtil.toRelative(hitPos, hitDirection);
		//System.out.println("Relative: " + hitPos);
		hitPos = constraint.apply(hitPos);
		//System.out.println("Constrained: " + hitPos);
		sculptor.sculpt(hitPos, hitDirection, view, state);
	}
	
	public VoxelShape getEffectedArea(Vec3i hitPos, Direction direction) {
		return sculptor.getEffectedShape(hitPos, direction);
	}
	
	public SculptMode next() {
		return SculptModes.MODES.get((index + 1) % SculptModes.MODES.size());
	}
}
