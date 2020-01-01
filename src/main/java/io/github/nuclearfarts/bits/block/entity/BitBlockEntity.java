package io.github.nuclearfarts.bits.block.entity;

import io.github.nuclearfarts.bits.BitMod;
import io.github.nuclearfarts.bits.block.BitBlock;
import io.github.nuclearfarts.bits.util.ShapeSerializer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.Tag;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;

public class BitBlockEntity extends BlockEntity implements BlockEntityClientSerializable {
	
	private static final Palette<BlockState> FALLBACK_PALETTE = new IdListPalette<>(Block.STATE_IDS, Blocks.AIR.getDefaultState());
	
	private final PalettedContainer<BlockState> container = new PalettedContainer<BlockState>(FALLBACK_PALETTE, Block.STATE_IDS, NbtHelper::toBlockState, NbtHelper::fromBlockState, Blocks.AIR.getDefaultState());
	
	@Environment(EnvType.CLIENT)
	public Mesh cachedModel;
	public boolean isModelValid = false;
	private int totalNonAir = 0;
	public final Object2IntMap<BlockState> states = new Object2IntOpenHashMap<>(); //count of how many of each state are in the block
	private BlockState most = Blocks.AIR.getDefaultState();
	private int totalLight = 0;
	private int emittedLight = 0;
	
	private VoxelShape shape = VoxelShapes.empty();
	
	public BitBlockEntity() {
		super(BitMod.BIT_BLOCK_ENTITY);
	}
	
	public void putBit(int x, int y, int z, BlockState from) {
		if(0 > x || x > 15 || 0 > y || y > 15 || 0 > z || z > 15) {
			throw new IllegalArgumentException("Attempted to place bit outside of containing block");
		}
		if(container.get(x, y, z) == from) {
			return;
		}
		container.set(x, y, z, from);
		if(!from.isAir()) {
			int newCount = states.getInt(from) + 1;
			states.put(from, newCount);
			shape = VoxelShapes.union(shape, VoxelShapes.cuboid(x/16d, y/16d, z/16d, (x+1)/16d, (y+1)/16d, (z+1)/16d));
			totalNonAir++;
			if(isAllSameType()) {
				world.setBlockState(pos, from);
			}
			totalLight += from.getLuminance();
			checkLight();
			updateMost();
			if(!world.isClient) {
				sync();
			}
			markDirty();
		} else {
			BlockState current = container.get(x, y, z);
			int count = states.getInt(current) - 1;
			if(count <= 0) {
				states.removeInt(current);
			} else {
				states.put(current, count);
			}
			shape = VoxelShapes.combineAndSimplify(shape, VoxelShapes.cuboid(x/16d, y/16d, z/16d, (x+1)/16d, (y+1)/16d, (z+1)/16d), (b1, b2) -> b1 ^ b2);
			totalNonAir--;
			if(totalNonAir == 0) {
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
			totalLight -= current.getLuminance();
			checkLight();
			updateMost();
			if(!world.isClient) {
				sync();
			}
			markDirty();
		}
		isModelValid = false;
		if(world.isClient) {
			((ClientWorld)world).updateListeners(pos, null, null, 8);
		}
	}
	
	public void fill(BlockState state) {
		for(int x = 0; x <= 15; x++) {
			for(int y = 0; y <= 15; y++) {
				for(int z = 0; z <= 15; z++) {
					container.set(x, y, z, state);
				}
			}
		}
		states.clear();
		if(!state.isAir()) {
			shape = VoxelShapes.fullCube();
			states.put(state, 16 * 16 * 16);
			totalNonAir = 16 * 16 * 16;
			totalLight = state.getLuminance() * 16 * 16 * 16;
		} else {
			shape = VoxelShapes.empty();
			totalNonAir = 0;
			totalLight = 0;
		}
		checkLight();
		markDirty();
	}
	
	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		container.write(tag, "Palette", "Data");
		IntArrayTag shapeTag = new IntArrayTag(new int[1]);
		shape.forEachBox(new ShapeSerializer(shapeTag));
		tag.put("Shape", shapeTag);
		tag.putInt("NonAir", totalNonAir);
		tag.put("CountMap", serializeCountMap());
		tag.put("Most", NbtHelper.fromBlockState(most));
		tag.putInt("Light", totalLight);
		return tag;
	}
	
	@Override
	public void fromTag(CompoundTag tag) {
		super.fromTag(tag);
		container.read((ListTag) tag.get("Palette"), tag.getLongArray("Data"));
		shape = ShapeSerializer.deserialize(tag.getIntArray("Shape"));
		totalNonAir = tag.getInt("NonAir");
		deserializeCountMap((ListTag) tag.get("CountMap"));
		most = NbtHelper.toBlockState(tag.getCompound("Most"));
		totalLight = tag.getInt("Light");
		checkLight();
	}
	
	public BlockState getBit(int x, int y, int z) {
		return container.get(x, y, z);
	}
	
	/**Used to render things. DO NOT USE.
	 * Like, seriously. If you use this directly it messes up the state of the bit block really badly.*/
	@Environment(EnvType.CLIENT)
	public PalettedContainer<BlockState> getContainer() {
		return container;
	}
	
	public VoxelShape getShape() {
		return shape;
	}

	@Override
	public void fromClientTag(CompoundTag tag) {
		super.fromTag(tag);
		container.read((ListTag) tag.get("Palette"), tag.getLongArray("Data"));
		shape = ShapeSerializer.deserialize(tag.getIntArray("Shape"));
		isModelValid = false;
		most = NbtHelper.toBlockState(tag.getCompound("Most"));
		((ClientWorld)world).updateListeners(pos, null, null, 8);
	}

	@Override
	public CompoundTag toClientTag(CompoundTag tag) {
		super.toTag(tag);
		container.write(tag, "Palette", "Data");
		IntArrayTag shapeTag = new IntArrayTag(new int[1]);
		shape.forEachBox(new ShapeSerializer(shapeTag));
		tag.put("Shape", shapeTag);
		tag.put("Most", NbtHelper.fromBlockState(most));
		return tag;
	}
	
	public boolean isAllSameType() {
		if(totalNonAir == 0) {
			return true;
		}
		if(totalNonAir != 16 * 16 * 16) {
			return false;
		}
		boolean hasFoundSingleState = false;
		for(int i : states.values()) {
			if(i != 0) {
				if(hasFoundSingleState) {
					return false;
				}
				hasFoundSingleState = true;
			}
		}
		return true;
	}
	
	public BlockState getMost() {
		return most;
	}
	
	private ListTag serializeCountMap() {
		ListTag tag = new ListTag();
		for(Object2IntMap.Entry<BlockState> e : states.object2IntEntrySet()) {
			CompoundTag state = NbtHelper.fromBlockState(e.getKey());
			state.putInt("Count", e.getIntValue());
			tag.add(state);
		}
		return tag;
	}
	
	private void deserializeCountMap(ListTag tag) {
		states.clear();
		for(Tag t : tag) {
			CompoundTag c = (CompoundTag) t;
			states.put(NbtHelper.toBlockState(c), c.getInt("Count"));
		}
	}
	
	private void updateMost() {
		int greatest = 0;
		for(Object2IntMap.Entry<BlockState> e : states.object2IntEntrySet()) {
			if(e.getIntValue() > greatest) {
				greatest = e.getIntValue();
				most = e.getKey();
			}
		}
	}
	
	private void checkLight() {
		if((int)(totalLight / (16d * 16d * 16d)) != emittedLight) {
			emittedLight = (int)(totalLight / (16d * 16d * 16d));
			world.setBlockState(pos, getCachedState().with(BitBlock.LIGHT, emittedLight));
		}
	}
}
