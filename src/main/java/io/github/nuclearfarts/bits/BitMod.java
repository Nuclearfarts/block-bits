package io.github.nuclearfarts.bits;

import java.util.Map;
import java.util.WeakHashMap;

import com.google.common.collect.Sets;

import io.github.nuclearfarts.bits.block.BitBlock;
import io.github.nuclearfarts.bits.block.entity.BitBlockEntity;
import io.github.nuclearfarts.bits.item.BitBlockItem;
import io.github.nuclearfarts.bits.item.BitItem;
import io.github.nuclearfarts.bits.item.ChiselItem;
import io.github.nuclearfarts.bits.sculpt.SculptMode;
import io.github.nuclearfarts.bits.sculpt.SculptModes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class BitMod implements ModInitializer {
	
	public static final String MODID = "blockbits";
	
	private static final Map<PlayerEntity, SculptMode> SCULPT_MODES = new WeakHashMap<>();
	
	public static final Block BIT_BLOCK = new BitBlock(FabricBlockSettings.of(Material.STONE).hardness(0.5f).dynamicBounds().sounds(BlockSoundGroup.GRASS).nonOpaque().breakByHand(true).build());
	public static final BlockEntityType<BitBlockEntity> BIT_BLOCK_ENTITY = new BlockEntityType<BitBlockEntity>(BitBlockEntity::new, Sets.newHashSet(BIT_BLOCK), null);
	
	public static final Tag<Block> CHISELABLE_BLOCKS = TagRegistry.block(id("chiselable")); 
	
	public static final Item BIT_ITEM = new BitItem(new Item.Settings());
	public static final ChiselItem CHISEL = new ChiselItem(new Item.Settings());
	
	@Override
	public void onInitialize() {
		
		
		
		Registry.register(Registry.BLOCK, id("bits"), BIT_BLOCK);
		Registry.register(Registry.BLOCK_ENTITY, id("bit_be"), BIT_BLOCK_ENTITY);
		Registry.register(Registry.ITEM, id("single_bit"), BIT_ITEM);
		Registry.register(Registry.ITEM, id("chisel"), CHISEL);
		Registry.register(Registry.ITEM, id("bits"), new BitBlockItem(new Item.Settings()));
	}
	
	public static Identifier id(String s) {
		return new Identifier(MODID, s);
	}
	
	public static SculptMode getSculptMode(PlayerEntity player) {
		return SCULPT_MODES.getOrDefault(player, SculptModes.SINGLE_BIT);
	}
	
	public static void cycleMode(PlayerEntity player) {
		SCULPT_MODES.putIfAbsent(player, SculptModes.SINGLE_BIT);
		//System.out.println();
		SCULPT_MODES.put(player, BitMod.SCULPT_MODES.get(player).next());
	}
}
