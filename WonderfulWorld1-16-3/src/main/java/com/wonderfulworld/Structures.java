package com.wonderfulworld;

import com.wonderfulworld.structures.KremowkaPieces;
import com.wonderfulworld.structures.KremowkaStructure;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage.Decoration;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@EventBusSubscriber( modid = WonderfulWorld.MOD_ID, bus = EventBusSubscriber.Bus.MOD )
public class Structures {
	@SubscribeEvent
	public static void register( RegistryEvent.Register< Feature< ? > > event ) {
		Registry.register( Registry.STRUCTURE_FEATURE, "kremowka_structure".toLowerCase( Locale.ROOT ), new KremowkaStructure() );
	}
	
	public static void setup( final FMLCommonSetupEvent event ) {
		//WorldGenRegistries.func_243663_a(WorldGenRegistries.field_243654_f, p_244162_0_, p_244162_1_);
	}
}
