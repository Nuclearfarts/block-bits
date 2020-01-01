package io.github.nuclearfarts.bits.sculpt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import io.github.nuclearfarts.bits.util.BitModUtil;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;

public final class SculptModes {

	public static final List<SculptMode> MODES = new ArrayList<>();
	
	private SculptModes() {}
	
	public static final SculptMode SINGLE_BIT = register(box(BitModUtil.boxAtOrigin(1, 1, 1), "Single Bit"));
	public static final SculptMode TWO_CUBE = register(box(BitModUtil.boxAtOrigin(2, 2, 2), "Two Cube"));
	public static final SculptMode FOUR_CUBE = register(box(BitModUtil.boxAtOrigin(4, 4, 4), "Four Cube"));
	public static final SculptMode EIGHT_CUBE = register(box(BitModUtil.boxAtOrigin(8, 8, 8), "Eight Cube"));
	public static final SculptMode TWO_SQUARE = register(box(BitModUtil.boxAtOrigin(2, 1, 2), "Two Square"));
	public static final SculptMode FOUR_SQUARE = register(box(BitModUtil.boxAtOrigin(4, 1, 4), "Four Square"));
	public static final SculptMode EIGHT_SQUARE = register(box(BitModUtil.boxAtOrigin(8, 1, 8), "Eight Square"));
	public static final SculptMode FULL_SQUARE = register(box(BitModUtil.boxAtOrigin(16, 1, 16), "Sixteen Square"));
	public static final SculptMode STRIP = register(box(BitModUtil.boxAtOrigin(1, 16, 1), "Strip"));
	public static final SculptMode SLAB = register(box(BitModUtil.boxAtOrigin(16, 8, 16), "Slab"));
	
	public static SculptMode register(SculptMode mode) {
		mode.index = MODES.size();
		MODES.add(mode);
		return mode;
	}
	
	public static SculptMode box(Box box, String name) {
		return new SculptMode(SculptConstraints.forBox(box), new BoxSculptor(box), name);
	}
	
	public static BiFunction<Vec3i, Direction, VoxelShape> getShapeProvider(Box forBox) {
		return (v, d) -> VoxelShapes.cuboid(BitModUtil.translateBy16ths(BitModUtil.fromRelativeBox(forBox, d), v));
	}
}
