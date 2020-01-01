package io.github.nuclearfarts.bits.item;

import io.github.nuclearfarts.bits.BitMod;
import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import io.github.nuclearfarts.bits.sculpt.SculptMode;
import io.github.nuclearfarts.bits.util.BitModUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class ChiselItem extends Item {
	
	public SculptMode mode;
	
	public ChiselItem(Item.Settings settings) {
		super(settings);
	}
	
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		if(user.isSneaking() && !world.isClient) {
			BitMod.cycleMode(user);
			user.sendMessage(new LiteralText("Switched mode to: " + BitMod.getSculptMode(user).name));
			return TypedActionResult.consume(user.getStackInHand(hand));
		}
		else {
			return super.use(world, user, hand);
		}
	}
	
	public ActionResult useOnBlock(ItemUsageContext ctx) {
		World world = ctx.getWorld();
		BlockPos pos = ctx.getBlockPos();
		BlockState clickedState = world.getBlockState(pos);
		if(clickedState.getBlock() != BitMod.BIT_BLOCK && clickedState.getBlock().matches(BitMod.CHISELABLE_BLOCKS)) {
			world.setBlockState(pos, BitMod.BIT_BLOCK.getDefaultState());
			BitBlockEntity be = (BitBlockEntity) world.getBlockEntity(pos);
			be.fill(clickedState);
			clickedState = BitMod.BIT_BLOCK.getDefaultState();
		}
		if(clickedState.getBlock() == BitMod.BIT_BLOCK) {
			BitBlockEntity be = (BitBlockEntity) world.getBlockEntity(pos);
			Vec3i innerPos = BitModUtil.getSubPosForPlacement(pos, ctx.getHitPos(), ctx.getSide()).offset(ctx.getSide(), -1);
			/*ItemStack bitItem = new ItemStack(BitMod.BIT_ITEM);
			CompoundTag tag = new CompoundTag();
			tag.put("Blockstate", NbtHelper.fromBlockState(be.getBit(innerPos.getX(), innerPos.getY(), innerPos.getZ())));
			bitItem.setTag(tag);
			be.putBit(innerPos.getX(), innerPos.getY(), innerPos.getZ(), Blocks.AIR.getDefaultState());
			ctx.getPlayer().giveItemStack(bitItem);*/
			if(!world.isClient)
			BitMod.getSculptMode(ctx.getPlayer()).sculpt(innerPos, ctx.getSide(), be, Blocks.AIR.getDefaultState());
			return ActionResult.SUCCESS;
		}
		return ActionResult.FAIL;
	}
	
	public Box getEffective(Vec3i playerHitPos, Direction hitDirection) {
		return BitModUtil.boxFromVec3i(BitModUtil.fromRelative(new Vec3i(0, 0, 0), hitDirection), BitModUtil.fromRelative(new Vec3i(10, 2, 10), hitDirection));
	}
}
