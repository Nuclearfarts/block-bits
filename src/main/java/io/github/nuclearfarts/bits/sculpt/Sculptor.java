package io.github.nuclearfarts.bits.sculpt;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.shape.VoxelShape;

public interface Sculptor {
	public void sculpt(Vec3i hitPos, Direction face, RelativeBitView its, BlockState with);
	
	public VoxelShape getEffectedShape(Vec3i hitPos, Direction face);
}
