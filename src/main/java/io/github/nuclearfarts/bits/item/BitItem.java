package io.github.nuclearfarts.bits.item;

import io.github.nuclearfarts.bits.BitMod;
import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import io.github.nuclearfarts.bits.util.BitModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class BitItem extends BlockItem {
	
	private BlockState extremelyBadPracticeSoundHack;
	private boolean newBlockCreated = false;
	
	public BitItem(Item.Settings settings) {
		super(BitMod.BIT_BLOCK, settings);
	}
	
	public ActionResult useOnBlock(ItemUsageContext context) {
		BlockState state = NbtHelper.toBlockState(context.getStack().getOrCreateSubTag("Blockstate"));
		World world = context.getWorld();
		BlockPos pos = context.getBlockPos();
		Direction side = context.getSide();
		if(!placeIntoExisting(world, state, side, pos, context.getHitPos())) {
			extremelyBadPracticeSoundHack = state;
			if(placeBitBlockIfNecessary(context)) {
				if(placeIntoExisting(world, state, side, pos.offset(side), context.getHitPos())) {
					if(!newBlockCreated) {
						BlockSoundGroup blockSoundGroup = state.getSoundGroup();
			        	world.playSound(context.getPlayer(), pos, blockSoundGroup.getPlaceSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
					}
					return ActionResult.SUCCESS;
				}
				return ActionResult.PASS;
			}
			return ActionResult.PASS;
		}
		BlockSoundGroup blockSoundGroup = state.getSoundGroup();
        world.playSound(context.getPlayer(), pos, blockSoundGroup.getPlaceSound(), SoundCategory.BLOCKS, (blockSoundGroup.getVolume() + 1.0F) / 2.0F, blockSoundGroup.getPitch() * 0.8F);
		return ActionResult.SUCCESS;
	}
	
	public Text getName(ItemStack stack) {
		BlockState state = NbtHelper.toBlockState(stack.getOrCreateSubTag("Blockstate"));
		return state.getBlock().getName().append(" ").append(new TranslatableText("block.blockbits.single_bit"));
	}
	
	private boolean placeBitBlockIfNecessary(ItemUsageContext context) {
		if(context.getWorld().getBlockState(context.getBlockPos().offset(context.getSide())).getBlock() != BitMod.BIT_BLOCK) {
			return newBlockCreated = super.useOnBlock(context) == ActionResult.SUCCESS;
		}
		newBlockCreated = false;
		return true;
	}
	
	private boolean placeIntoExisting(World world, BlockState type, Direction side, BlockPos pos, Vec3d hitPos) {
		if(world.getBlockState(pos).getBlock() == BitMod.BIT_BLOCK) {
			BlockEntity be = world.getBlockEntity(pos);
			if(be instanceof BitBlockEntity) {
				BitBlockEntity bitEntity = (BitBlockEntity) be;
				Vec3i subPos = BitModUtil.getSubPosForPlacement(pos, hitPos, side);
				if(subPos.getX() < 0 || subPos.getX() > 15 || subPos.getY() < 0 || subPos.getY() > 15 || subPos.getZ() < 0 || subPos.getZ() > 15) {
					return false;
				}
				bitEntity.putBit(subPos.getX(), subPos.getY(), subPos.getZ(), type);
				return true;
			}
		}
		return false;
	}
	
	public SoundEvent getPlaceSound(BlockState state) {
		return extremelyBadPracticeSoundHack.getSoundGroup().getPlaceSound();
	}
}
