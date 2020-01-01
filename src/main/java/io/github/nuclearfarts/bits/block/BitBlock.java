package io.github.nuclearfarts.bits.block;

import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class BitBlock extends Block implements BlockEntityProvider {

	public static final IntProperty LIGHT = IntProperty.of("light", 0, 15);
	
	public BitBlock(Settings settings) {
		super(settings);
		setDefaultState(stateManager.getDefaultState().with(LIGHT, 0));
	}
	
	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(LIGHT);
	}

	@Override
	public BlockEntity createBlockEntity(BlockView view) {
		return new BitBlockEntity();
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		BlockEntity be = view.getBlockEntity(pos);
		if(be instanceof BitBlockEntity) {
			VoxelShape shape = ((BitBlockEntity) be).getShape();
			return shape.isEmpty() ? VoxelShapes.fullCube() : shape;
		}
		return VoxelShapes.fullCube();
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockView view, BlockPos pos, EntityContext ePos) {
		BlockEntity be = view.getBlockEntity(pos);
		if(be instanceof BitBlockEntity) {
			return ((BitBlockEntity) be).getShape();
		}
		return VoxelShapes.empty();
	}
	
	@Override
	public int getLuminance(BlockState state) {
		return state.get(LIGHT);
	}
}
