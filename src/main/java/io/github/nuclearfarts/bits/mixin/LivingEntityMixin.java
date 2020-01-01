package io.github.nuclearfarts.bits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.nuclearfarts.bits.BitMod;
import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

	private LivingEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}
	
	@ModifyVariable(method = "method_23328()V",
			at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/world/World.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
			print = false, index = 4)
	private BlockState dynamicSound(BlockState state) {
		if(state.getBlock() == BitMod.BIT_BLOCK) {
			System.out.println(state);
			int i = MathHelper.floor(getX());
			int j = MathHelper.floor(getY() - 0.2);
			int k = MathHelper.floor(getZ());
			BlockPos pos = new BlockPos(i, j, k);
			return ((BitBlockEntity)((Entity)(Object)this).world.getBlockEntity(pos)).getMost();
		}
		return state;
	}
	
	@ModifyVariable(method = "fall(DZLnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V", at = @At("HEAD"), index = 4)
	private BlockState fallParticles(BlockState state) {
		if(state.getBlock() == BitMod.BIT_BLOCK) {
			return ((BitBlockEntity)((Entity)(Object)this).world.getBlockEntity(getLandingPos())).getMost();
		}
		return state;
	}
}
