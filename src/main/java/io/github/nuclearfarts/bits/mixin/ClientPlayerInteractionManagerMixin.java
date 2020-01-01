package io.github.nuclearfarts.bits.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import io.github.nuclearfarts.bits.BitMod;
import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {
	
	private @Shadow @Final MinecraftClient client;
	
	@ModifyVariable(method = "updateBlockBreakingProgress(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;)Z",
			at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/block/BlockState.getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"),
			print = false, index = 4)
	private BlockSoundGroup fixSoundStuff(BlockSoundGroup group, BlockPos pos, Direction direction) {
		BlockState state = client.world.getBlockState(pos);
		if(state.getBlock() == BitMod.BIT_BLOCK) {
			return ((BitBlockEntity)client.world.getBlockEntity(pos)).getMost().getSoundGroup();
		}
		return group;
	}
}
