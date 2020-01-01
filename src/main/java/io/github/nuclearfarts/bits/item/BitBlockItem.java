package io.github.nuclearfarts.bits.item;

import io.github.nuclearfarts.bits.BitMod;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.ActionResult;

public class BitBlockItem extends BlockItem {

	private BlockState extremelyBadPracticeSoundHack;
	
	public BitBlockItem(Settings settings) {
		super(BitMod.BIT_BLOCK, settings);
	}
	
	public ActionResult useOnBlock(ItemUsageContext context) {
		extremelyBadPracticeSoundHack = NbtHelper.toBlockState(context.getStack().getOrCreateSubTag("BlockEntityTag").getCompound("Most"));
		return super.useOnBlock(context);
	}
	
	public SoundEvent getPlaceSound(BlockState state) {
		return extremelyBadPracticeSoundHack.getSoundGroup().getPlaceSound();
	}
}
