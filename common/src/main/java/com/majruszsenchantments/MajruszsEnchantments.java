package com.majruszsenchantments;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.item.CustomEnchantment;
import com.majruszlibrary.item.ItemHelper;
import com.majruszlibrary.modhelper.ModHelper;
import com.majruszlibrary.registry.Custom;
import com.majruszlibrary.registry.RegistryGroup;
import com.majruszlibrary.registry.RegistryObject;
import com.majruszsenchantments.curses.*;
import com.majruszsenchantments.data.Config;
import com.majruszsenchantments.enchantments.*;
import com.majruszsenchantments.particles.DodgeParticle;
import com.majruszsenchantments.particles.SmelterParticle;
import com.majruszsenchantments.particles.TelekinesisParticle;
import com.majruszsenchantments.particles.TelekinesisParticleType;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Predicate;

public class MajruszsEnchantments {
	public static final String MOD_ID = "majruszsenchantments";
	public static final ModHelper HELPER = ModHelper.create( MOD_ID );

	// Configs
	static {
		HELPER.config( Config.class ).autoSync().create();
	}

	// Registry Groups
	public static final RegistryGroup< Enchantment > ENCHANTMENTS = HELPER.create( Registry.ENCHANTMENT );
	public static final RegistryGroup< ParticleType< ? > > PARTICLES = HELPER.create( Registry.PARTICLE_TYPE );

	// Enchantments
	public static final RegistryObject< CustomEnchantment > DEATH_WISH = ENCHANTMENTS.create( "death_wish", DeathWishEnchantment::create );
	public static final RegistryObject< CustomEnchantment > DODGE = ENCHANTMENTS.create( "dodge", DodgeEnchantment::create );
	public static final RegistryObject< CustomEnchantment > ENLIGHTENMENT = ENCHANTMENTS.create( "enlightenment", EnlightenmentEnchantment::create );
	public static final RegistryObject< CustomEnchantment > FISHING_FANATIC = ENCHANTMENTS.create( "fishing_fanatic", FishingFanaticEnchantment::create );
	public static final RegistryObject< CustomEnchantment > FUSE_CUTTER = ENCHANTMENTS.create( "fuse_cutter", FuseCutterEnchantment::create );
	public static final RegistryObject< CustomEnchantment > GOLD_FUELLED = ENCHANTMENTS.create( "gold_fuelled", GoldFuelledEnchantment::create );
	public static final RegistryObject< CustomEnchantment > HARVESTER = ENCHANTMENTS.create( "harvester", HarvesterEnchantment::create );
	public static final RegistryObject< CustomEnchantment > HORSE_FROST_WALKER = ENCHANTMENTS.create( "horse_frost_walker", HorseFrostWalkerEnchantment::create );
	public static final RegistryObject< CustomEnchantment > HORSE_PROTECTION = ENCHANTMENTS.create( "horse_protection", HorseProtectionEnchantment::create );
	public static final RegistryObject< CustomEnchantment > HORSE_SWIFTNESS = ENCHANTMENTS.create( "horse_swiftness", HorseSwiftnessEnchantment::create );
	public static final RegistryObject< CustomEnchantment > HUNTER = ENCHANTMENTS.create( "hunter", HunterEnchantment::create );
	public static final RegistryObject< CustomEnchantment > IMMORTALITY = ENCHANTMENTS.create( "immortality", ImmortalityEnchantment::create );
	public static final RegistryObject< CustomEnchantment > LEECH = ENCHANTMENTS.create( "leech", LeechEnchantment::create );
	public static final RegistryObject< CustomEnchantment > MAGIC_PROTECTION = ENCHANTMENTS.create( "magic_protection", MagicProtectionEnchantment::create );
	public static final RegistryObject< CustomEnchantment > MISANTHROPY = ENCHANTMENTS.create( "misanthropy", MisanthropyEnchantment::create );
	public static final RegistryObject< CustomEnchantment > REPULSION = ENCHANTMENTS.create( "repulsion", RepulsionEnchantment::create );
	public static final RegistryObject< CustomEnchantment > SIXTH_SENSE = ENCHANTMENTS.create( "sixth_sense", SixthSenseEnchantment::create );
	public static final RegistryObject< CustomEnchantment > SMELTER = ENCHANTMENTS.create( "smelter", SmelterEnchantment::create );
	public static final RegistryObject< CustomEnchantment > TELEKINESIS = ENCHANTMENTS.create( "telekinesis", TelekinesisEnchantment::create );

	// Curses
	public static final RegistryObject< CustomEnchantment > BREAKING = ENCHANTMENTS.create( "breaking_curse", BreakingCurse::create );
	public static final RegistryObject< CustomEnchantment > CORROSION = ENCHANTMENTS.create( "corrosion_curse", CorrosionCurse::create );
	public static final RegistryObject< CustomEnchantment > FATIGUE = ENCHANTMENTS.create( "fatigue_curse", FatigueCurse::create );
	public static final RegistryObject< CustomEnchantment > INCOMPATIBILITY = ENCHANTMENTS.create( "incompatibility_curse", IncompatibilityCurse::create );
	public static final RegistryObject< CustomEnchantment > SLIPPERY = ENCHANTMENTS.create( "slippery_curse", SlipperyCurse::create );

	// Enchantment Categories
	public static final Predicate< ItemStack > IS_BOW_OR_CROSSBOW = itemStack->ItemHelper.isRangedWeapon( itemStack.getItem() );
	public static final Predicate< ItemStack > IS_GOLDEN = itemStack->ItemHelper.isGoldenToolOrArmor( itemStack.getItem() );
	public static final Predicate< ItemStack > IS_HORSE_ARMOR = itemStack->itemStack.getItem() instanceof HorseArmorItem;
	public static final Predicate< ItemStack > IS_HOE = itemStack->itemStack.getItem() instanceof HoeItem;
	public static final Predicate< ItemStack > IS_MELEE_MINECRAFT = itemStack->itemStack.getItem() instanceof SwordItem || itemStack.getItem() instanceof AxeItem; // for some reason all minecraft sword enchantments are applicable to axes
	public static final Predicate< ItemStack > IS_MELEE = itemStack->ItemHelper.isMeleeWeapon( itemStack.getItem() );
	public static final Predicate< ItemStack > IS_SHIELD = itemStack->ItemHelper.isShield( itemStack.getItem() );
	public static final Predicate< ItemStack > IS_TOOL = itemStack->ItemHelper.isAnyTool( itemStack.getItem() );

	// Particles
	public static final RegistryObject< SimpleParticleType > DODGE_PARTICLE = PARTICLES.create( "dodge", ()->new SimpleParticleType( true ) {} );
	public static final RegistryObject< SimpleParticleType > SMELTER_PARTICLE = PARTICLES.create( "smelter", ()->new SimpleParticleType( true ) {} );
	public static final RegistryObject< TelekinesisParticleType > TELEKINESIS_PARTICLE = PARTICLES.create( "telekinesis", TelekinesisParticleType::new );

	private MajruszsEnchantments() {}

	@OnlyIn( Dist.CLIENT )
	public static class Client {
		static {
			HELPER.create( Custom.Particles.class, particles->{
				particles.register( DODGE_PARTICLE.get(), DodgeParticle.Factory::new );
				particles.register( SMELTER_PARTICLE.get(), SmelterParticle.Factory::new );
				particles.register( TELEKINESIS_PARTICLE.get(), TelekinesisParticle.Factory::new );
			} );
		}
	}
}
