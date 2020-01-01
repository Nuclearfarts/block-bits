package io.github.nuclearfarts.bits.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.BlockCrackParticle;
import net.minecraft.client.particle.SpriteBillboardParticle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import io.github.nuclearfarts.bits.BitMod;
import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;

@Mixin(BlockCrackParticle.class)
public abstract class BlockCrackParticleMixin extends SpriteBillboardParticle {
	protected BlockCrackParticleMixin(World world, double d, double e, double f) {
		super(world, d, e, f);
	}
	
	private @Shadow @Final BlockState blockState;

	@Inject(at = @At("HEAD"), method = "setBlockPos(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/client/particle/BlockCrackParticle;")
	private void dynamicParticles1(BlockPos pos, CallbackInfoReturnable<BlockCrackParticle> info) {
		if(blockState.getBlock() == BitMod.BIT_BLOCK) {
			BitBlockEntity be = (BitBlockEntity) world.getBlockEntity(pos);
			setSprite(MinecraftClient.getInstance().getBlockRenderManager().getModel(be.getMost()).getSprite());
		}
	}
}
