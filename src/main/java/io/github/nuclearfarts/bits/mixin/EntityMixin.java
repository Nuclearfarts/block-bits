package io.github.nuclearfarts.bits.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.nuclearfarts.bits.BitMod;
import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Mixin(Entity.class)
public abstract class EntityMixin {
	
	private @Shadow double x;
	private @Shadow double y;
	private @Shadow double z;
	
	@ModifyVariable(method = "playStepSound(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
			at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/world/World.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
			print = false, index = 2)
	private BlockState dynamicSound(BlockState state, BlockPos pos) {
		if(state.getBlock() == BitMod.BIT_BLOCK) {
			return ((BitBlockEntity)((Entity)(Object)this).world.getBlockEntity(pos)).getMost();
		}
		return state;
	}
	
	@ModifyVariable(method = "spawnSprintingParticles()V",
			at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/world/World.getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;"),
			print = false, index = 5)
	private BlockState dynamicSprintParticle(BlockState state) {
		if(state.getBlock() == BitMod.BIT_BLOCK) {
			int i = MathHelper.floor(x);
			int j = MathHelper.floor(y - 0.2);
			int k = MathHelper.floor(z);
			BlockPos pos = new BlockPos(i, j, k);
			return ((BitBlockEntity)((Entity)(Object)this).world.getBlockEntity(pos)).getMost();
		}
		return state;
	}
}
