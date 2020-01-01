package io.github.nuclearfarts.bits.util;

import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.util.shape.VoxelShapes.BoxConsumer;

public class ShapeSerializer implements BoxConsumer {

	private final IntArrayTag tag;
	
	public ShapeSerializer(IntArrayTag tag) {
		this.tag = tag;
	}
	
	@Override
	public void consume(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		tag.add(IntTag.of(pack(minX * 16, minY * 16, minZ * 16, maxX * 16, maxY * 16, maxZ * 16)));
	}
	
	public static VoxelShape deserialize(int[] ints) {
		VoxelShape shape = fromInt(ints[0]);
		for(int i = 1; i < ints.length; i++) {
			shape = VoxelShapes.combine(shape, fromInt(ints[i]), BooleanBiFunction.OR);
		}
		return shape.simplify();
	}

	
	public static VoxelShape fromInt(int val) {
		return VoxelShapes.cuboid(new Box((val & 0b11111) / 16d, ((val >> 5) & 0b11111) / 16d, ((val >> 10) & 0b11111) / 16d, ((val >> 15) & 0b11111) / 16d, ((val >> 20) & 0b11111) / 16d, ((val >> 25) & 0b11111) / 16d));
	}
	
	public static int pack(Box box) {
		return pack(box.x1, box.y1, box.z1, box.x2, box.y2, box.z2);
	}
	
	public static int pack(double x1, double y1, double z1, double x2, double y2, double z2) {
		return (int)x1 | ((int)y1 << 5) | ((int)z1 << 10) | ((int)x2 << 15) | ((int)y2 << 20) | ((int)z2 << 25);
	}
}
