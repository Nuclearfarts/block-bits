package io.github.nuclearfarts.bits.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.nuclearfarts.bits.BitMod;
import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import io.github.nuclearfarts.bits.util.BitModUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin {
	
	private @Shadow ClientWorld world;
	private @Shadow @Final MinecraftClient client;
	
	@ModifyVariable(method = "playLevelEvent(Lnet/minecraft/entity/player/PlayerEntity;ILnet/minecraft/util/math/BlockPos;I)V",
			at = @At(value = "INVOKE_ASSIGN", target = "net/minecraft/block/BlockState.getSoundGroup()Lnet/minecraft/sound/BlockSoundGroup;"),
			print = false, index = 7)
	private BlockSoundGroup dynamicSound(BlockSoundGroup group, PlayerEntity player, int type, BlockPos pos, int data) {
		BlockState state = Block.getStateFromRawId(data);
		if(state.getBlock() == BitMod.BIT_BLOCK) {
			return ((BitBlockEntity)world.getBlockEntity(pos)).getMost().getSoundGroup();
		}
		return group;
	}
	
	@Inject(method = "drawBlockOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;DDDLnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
			at = @At("HEAD"), cancellable = true)
	private void outlineCallback(MatrixStack matrixStack, VertexConsumer vertexConsumer, Entity entity, double d, double e, double f, BlockPos blockPos, BlockState blockState, CallbackInfo info) {
		if(client.player.getMainHandStack().getItem() == BitMod.CHISEL) {
			BlockHitResult hit = (BlockHitResult) client.crosshairTarget;
			System.out.println(BitMod.getSculptMode(client.player).getEffectedArea(BitModUtil.getSubPos(blockPos, hit.getPos(), hit.getSide()), hit.getSide()));
			drawShapeOutline(matrixStack, vertexConsumer, BitMod.getSculptMode(client.player).getEffectedArea(BitModUtil.getSubPos(blockPos, hit.getPos(), hit.getSide()), hit.getSide()), blockPos.getX() - d, blockPos.getY() - e, blockPos.getZ() - f, 0, 0, 0, 1);
			info.cancel();
		}
	}
	
	private static @Shadow void drawShapeOutline(MatrixStack matrixStack, VertexConsumer vertexConsumer, VoxelShape voxelShape, double d, double e, double f, float r, float g, float b, float a) {
		throw new RuntimeException("wtf");
	}
	
}
