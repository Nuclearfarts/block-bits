package io.github.nuclearfarts.bits.sculpt;

import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import io.github.nuclearfarts.bits.util.BitModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

public class RelativeBitView {
	private final BitBlockEntity blockEntity;
	private final Direction direction;
	
	public RelativeBitView(BitBlockEntity be, Direction relativeTo) {
		blockEntity = be;
		direction = relativeTo;
	}
	
	public void putBit(Vec3i pos, BlockState bit) {
		Vec3i rotated = BitModUtil.fromRelativeMut(pos, direction);
		blockEntity.putBit(rotated.getX(), rotated.getY(), rotated.getZ(), bit);
	}
	
	public BlockState getBit(Vec3i pos) {
		Vec3i rotated = BitModUtil.fromRelativeMut(pos, direction);
		return blockEntity.getBit(rotated.getX(), rotated.getY(), rotated.getZ());
	}
}
