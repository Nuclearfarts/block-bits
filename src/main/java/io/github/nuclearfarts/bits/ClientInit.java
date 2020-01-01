package io.github.nuclearfarts.bits;

import io.github.nuclearfarts.bits.client.BitModelVariantProvider;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.client.render.RenderLayer;

public class ClientInit implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModelLoadingRegistry.INSTANCE.registerVariantProvider(BitModelVariantProvider::new);
		BlockRenderLayerMap.INSTANCE.putBlock(BitMod.BIT_BLOCK, RenderLayer.getTranslucent());
	}

}
