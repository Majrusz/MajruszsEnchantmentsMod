package com.majruszsenchantments.particles;

import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.particles.ConfigurableParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class DodgeParticle extends ConfigurableParticle {
	DodgeParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );

		this.xd = this.xd * 0.0025 + xSpeed;
		this.yd = this.yd * 0.0200 + ySpeed;
		this.zd = this.zd * 0.0025 + zSpeed;
		this.xdFormula = xd->xd + Random.nextFloat( -0.0002f, 0.0002f );
		this.ydFormula = yd->yd - 0.00002f;
		this.zdFormula = zd->zd + Random.nextFloat( -0.0002f, 0.0002f );
		this.alphaFormula = alpha->Math.max( 0.0f, alpha - 0.04f );
		this.scaleFormula = lifeRatio->1.0f - 0.1f * lifeRatio;
		this.lifetime = this.random.nextInt( Utility.secondsToTicks( 0.5 ) ) + Utility.secondsToTicks( 1.5 );

		this.scale( 2.0f );
		this.setSize( 0.125f, 0.125f );
		this.pickSprite( spriteSet );
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory extends SimpleFactory {
		public Factory( SpriteSet sprite ) {
			super( sprite, DodgeParticle::new );
		}
	}
}