package io.github.nuclearfarts.bits.client;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;
import java.util.function.Supplier;

import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.chunk.IdListPalette;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;

public class BitBlockModel implements FabricBakedModel, BakedModel {

	private static final ModelTransformation TRANSFORM = ModelHelper.MODEL_TRANSFORM_BLOCK;
	private static final Palette<BlockState> FALLBACK_PALETTE = new IdListPalette<>(Block.STATE_IDS, Blocks.AIR.getDefaultState());
	private static final PalettedContainer<BlockState> TEMP_CONTAINER = new PalettedContainer<BlockState>(FALLBACK_PALETTE, Block.STATE_IDS, NbtHelper::toBlockState, NbtHelper::fromBlockState, Blocks.AIR.getDefaultState());
	
	private final Map<ItemStack, Mesh> itemModelMap = new WeakHashMap<>();
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView world, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
		BlockEntity be = world.getBlockEntity(pos);
		if (be instanceof BitBlockEntity) {
			BitBlockEntity bitEntity = (BitBlockEntity) be;
			if (!bitEntity.isModelValid) {
				MeshBuilder builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
				QuadEmitter emitter = builder.getEmitter();
				emitQuads(bitEntity.getContainer(), emitter);
				bitEntity.cachedModel = builder.build();
				bitEntity.isModelValid = true;
			}
			context.meshConsumer().accept(bitEntity.cachedModel);
		}
	}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		if(!itemModelMap.containsKey(stack)) {
			MeshBuilder builder = RendererAccess.INSTANCE.getRenderer().meshBuilder();
			QuadEmitter emitter = builder.getEmitter();
			CompoundTag tag = stack.getOrCreateSubTag("BlockEntityTag");
			TEMP_CONTAINER.read((ListTag) tag.get("Palette"), tag.getLongArray("Data"));
			emitQuads(TEMP_CONTAINER, emitter);
			itemModelMap.put(stack, builder.build());
		}
		context.meshConsumer().accept(itemModelMap.get(stack));
	}

	private void emitQuads(PalettedContainer<BlockState> container, QuadEmitter emitter) {
		BlockModels models = MinecraftClient.getInstance().getBakedModelManager().getBlockModels();
		BlockState lastState = null;
		Sprite lastSprite = null;
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				for (int z = 0; z < 16; z++) {
					BlockState state = container.get(x, y, z);
					if (!state.isAir()) {
						Sprite sp;
						if(state == lastState) {
							sp = lastSprite;
						} else {
							lastState = state;
							sp = lastSprite = models.getSprite(state);
						}
						if (z == 0 || !container.get(x, y, z - 1).isOpaque()) {
							float minU = sp.getFrameU(15 - x);
							float maxU = sp.getFrameU(16 - x);
							float minV = sp.getFrameV(15 - y);
							float maxV = sp.getFrameV(16 - y);
							emitter.square(Direction.NORTH, x / 16f, y / 16f, (x + 1) / 16f, (y + 1) / 16f, z / 16f)
							.spriteBake(0, sp, 0)
							.sprite(0, 0, maxU, maxV)
							.sprite(1, 0, maxU, minV)
							.sprite(2, 0, minU, minV)
							.sprite(3, 0, minU, maxV)
							.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
							.emit();
						}
						if (z == 15 || !container.get(x, y, z + 1).isOpaque()) {
							float minU = sp.getFrameU(x);
							float maxU = sp.getFrameU(x + 1);
							float minV = sp.getFrameV(15 - y);
							float maxV = sp.getFrameV(16 - y);
							emitter.square(Direction.SOUTH, (15 - x) / 16f, y / 16f, (16 - x) / 16f, (y + 1) / 16f, (15 - z) / 16f)
							.spriteBake(0, sp, 0)
							.sprite(0, 0, maxU, maxV)
							.sprite(1, 0, maxU, minV)
							.sprite(2, 0, minU, minV)
							.sprite(3, 0, minU, maxV)
							.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
							.emit();
						}
						if (x == 0 || !container.get(x - 1, y, z).isOpaque()) {
							float minU = sp.getFrameU(z);
							float maxU = sp.getFrameU(z + 1);
							float minV = sp.getFrameV(15 - y);
							float maxV = sp.getFrameV(16 - y);
							emitter.square(Direction.WEST, z / 16f, y / 16f, (z + 1) / 16f, (y + 1) / 16f, x / 16f)
							.spriteBake(0, sp, 0)
							.sprite(0, 0, minU, maxV)
							.sprite(1, 0, minU, minV)
							.sprite(2, 0, maxU, minV)
							.sprite(3, 0, maxU, maxV)
							.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
							.emit();
						}
						if (x == 15 || !container.get(x + 1, y, z).isOpaque()) {
							float minU = sp.getFrameU(15 - z);
							float maxU = sp.getFrameU(16 - z);
							float minV = sp.getFrameV(15 - y);
							float maxV = sp.getFrameV(16 - y);
							emitter.square(Direction.EAST, (15 - z) / 16f, y / 16f, (16 - z) / 16f, (y + 1) / 16f, (15 - x) / 16f)
							.spriteBake(0, sp, 0)
							.sprite(0, 0, minU, maxV)
							.sprite(1, 0, minU, minV)
							.sprite(2, 0, maxU, minV)
							.sprite(3, 0, maxU, maxV)
							.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
							.emit();
						}
						if (y == 0 || !container.get(x, y - 1, z).isOpaque()) {
							float minU = sp.getFrameU(x);
							float maxU = sp.getFrameU(x + 1);
							float minV = sp.getFrameV(15 - z);
							float maxV = sp.getFrameV(16 - z);//
							emitter.square(Direction.DOWN, x / 16f, z / 16f, (x + 1) / 16f, (z + 1) / 16f, y / 16f)
							.spriteBake(0, sp, 0)
							.sprite(0, 0, minU, maxV)
							.sprite(1, 0, minU, minV)
							.sprite(2, 0, maxU, minV)
							.sprite(3, 0, maxU, maxV)
							.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
							.emit();
						}
						if (y == 15 || !container.get(x, y + 1, z).isOpaque()) {
							float minU = sp.getFrameU(x);
							float maxU = sp.getFrameU(x + 1);
							float minV = sp.getFrameV(z);
							float maxV = sp.getFrameV(z + 1);
							emitter.square(Direction.UP, x / 16f, (15 - z) / 16f, (x + 1) / 16f, (16 - z) / 16f,(15 - y) / 16f)
							.spriteBake(0, sp, 0)
							.sprite(0, 0, minU, maxV)
							.sprite(1, 0, minU, minV)
							.sprite(2, 0, maxU, minV)
							.sprite(3, 0, maxU, maxV)
							.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
							.emit();
						}
					}
				}
			}
		}
	}

	/** should never be called */
	@Override
	public List<BakedQuad> getQuads(BlockState state, Direction face, Random random) {
		return null;
	}

	@Override
	public boolean useAmbientOcclusion() {
		return true;
	}

	@Override
	public boolean hasDepthInGui() {
		return true;
	}

	@Override
	public boolean isBuiltin() {
		return false;
	}

	@Override
	public Sprite getSprite() {
		return MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(Blocks.AIR.getDefaultState());
		//just in case. this should just be missing texture. most of the time it will get overwritten by my BlockCrackParticle  or Entity mixins anyway.
	}

	@Override
	public ModelTransformation getTransformation() {
		return TRANSFORM;
	}

	@Override
	public ModelItemPropertyOverrideList getItemPropertyOverrides() {
		return ModelItemPropertyOverrideList.EMPTY;
	}
}
