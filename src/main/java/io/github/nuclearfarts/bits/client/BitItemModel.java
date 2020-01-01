package io.github.nuclearfarts.bits.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.Mesh;
import net.fabricmc.fabric.api.renderer.v1.mesh.MeshBuilder;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelItemPropertyOverrideList;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;

/**item model for single bits*/
@Environment(EnvType.CLIENT)
public class BitItemModel implements BakedModel, FabricBakedModel {
	
	private static final ModelTransformation TRANSFORM = new ModelTransformation(
			ModelHelper.TRANSFORM_BLOCK_3RD_PERSON_RIGHT, ModelHelper.TRANSFORM_BLOCK_3RD_PERSON_RIGHT,
			ModelHelper.TRANSFORM_BLOCK_1ST_PERSON_LEFT, ModelHelper.TRANSFORM_BLOCK_1ST_PERSON_RIGHT, Transformation.NONE, makeTransform(30, 225, 0, 0, 5, 0, 1, 1, 1),
			ModelHelper.TRANSFORM_BLOCK_GROUND, ModelHelper.TRANSFORM_BLOCK_FIXED);
	
	private final Map<BlockState, Mesh> bitMeshMap = new HashMap<>();
	
	private static Transformation makeTransform(
            float rotationX, float rotationY, float rotationZ,
            float translationX, float translationY, float translationZ,
            float scaleX, float scaleY, float scaleZ) {
        Vector3f translation = new Vector3f(translationX, translationY, translationZ);
        translation.scale(0.0625f);
        translation.clamp(-5.0F, 5.0F);
        return new Transformation(
                new Vector3f(rotationX, rotationY, rotationZ),
                translation, 
                new Vector3f(scaleX, scaleY, scaleZ));
    }
	
	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {}

	@Override
	public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
		BlockState state = NbtHelper.toBlockState(stack.getOrCreateTag().getCompound("Blockstate"));
		if(!state.isAir()) {
			context.meshConsumer().accept(bitMeshMap.computeIfAbsent(state, BitItemModel::bake));
		} else {
			//System.out.println("Attempted to render air bit!? This shouldn't happen. The bit's data is likely corrupt.");
		}
	}
	
	private static Mesh bake(BlockState state) {
		Sprite spr = MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(state);
		float minU = spr.getMinU();
		float maxU = spr.getFrameU(1);
		float minV = spr.getMinV();
		float maxV = spr.getFrameV(1);
		MeshBuilder mb = RendererAccess.INSTANCE.getRenderer().meshBuilder();
		mb.getEmitter()
		.spriteBake(0, spr, 0)
		.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
		.square(Direction.UP, 3/8f, 3/8f, 4/8f, 4/8f, 7/8f)
		.sprite(0, 0, minU, maxV)
		.sprite(1, 0, minU, minV)
		.sprite(2, 0, maxU, minV)
		.sprite(3, 0, maxU, maxV)
		.emit()
		.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
		.square(Direction.DOWN, 3/8f, 3/8f, 4/8f, 4/8f, 0f)
		.sprite(0, 0, minU, maxV)
		.sprite(1, 0, minU, minV)
		.sprite(2, 0, maxU, minV)
		.sprite(3, 0, maxU, maxV)
		.emit()
		.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
		.square(Direction.NORTH, 3/8f, 0, 4/8f, 1/8f, 4/8f)
		.sprite(0, 0, minU, maxV)
		.sprite(1, 0, minU, minV)
		.sprite(2, 0, maxU, minV)
		.sprite(3, 0, maxU, maxV)
		.emit()
		.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
		.square(Direction.SOUTH, 1/2f, 0, 5/8f, 1/8f, 3/8f)
		.sprite(0, 0, minU, maxV)
		.sprite(1, 0, minU, minV)
		.sprite(2, 0, maxU, minV)
		.sprite(3, 0, maxU, maxV)
		.emit()
		.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
		.square(Direction.EAST, 3/8f, 0f, 4/8f, 1/8f, 4/8f)
		.sprite(0, 0, minU, maxV)
		.sprite(1, 0, minU, minV)
		.sprite(2, 0, maxU, minV)
		.sprite(3, 0, maxU, maxV)
		.emit()
		.spriteColor(0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF)
		.square(Direction.WEST, 1/2f, 0f, 5/8f, 1/8f, 3/8f)
		.sprite(0, 0, minU, maxV)
		.sprite(1, 0, minU, minV)
		.sprite(2, 0, maxU, minV)
		.sprite(3, 0, maxU, maxV)
		.emit();
		return mb.build();
	}

	/**do not call*/
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
		return MinecraftClient.getInstance().getBlockRenderManager().getModels().getSprite(Blocks.STONE.getDefaultState());
		//don't think this does anything for non-block models, but return something anyway just in case.
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
