package io.github.nuclearfarts.bits.client;

import io.github.nuclearfarts.bits.BitMod;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelVariantProvider;
import net.minecraft.client.render.model.UnbakedModel;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class BitModelVariantProvider implements ModelVariantProvider {

	private static final Identifier BIT_BLOCK_MODEL_ID = BitMod.id("bits");
	private static final Identifier BIT_ITEM_MODEL_ID = BitMod.id("single_bit");
	
	public BitModelVariantProvider(ResourceManager rm) {}
	
	@Override
	public UnbakedModel loadModelVariant(ModelIdentifier modelId, ModelProviderContext context) throws ModelProviderException {
		//System.out.println(modelId);
		if(BIT_BLOCK_MODEL_ID.equals(modelId)) {
			return new SupplierUnbakedModel(BitBlockModel::new);
		} else if(BIT_ITEM_MODEL_ID.equals(modelId)) {
			return new SupplierUnbakedModel(BitItemModel::new);
		}
		
		return null;
	}

}
