package com.majruszsenchantments.particles;

import com.mlib.Random;
import com.mlib.Utility;
import com.mlib.particles.SimpleParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class TelekinesisParticle extends SimpleParticle {
	TelekinesisParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed, spriteSet, 0.0f );

		this.xd = this.xd * Random.nextFloat( -0.1f, 0.1f ) + xSpeed;
		this.yd = this.yd * Random.nextFloat( -0.1f, 0.1f ) + ySpeed;
		this.zd = this.zd * Random.nextFloat( -0.1f, 0.1f ) + zSpeed;
		this.xdOnGroundFormula = this.xdFormula = xd->xd;
		this.ydOnGroundFormula = this.ydFormula = yd->yd;
		this.zdOnGroundFormula = this.zdFormula = zd->zd;
		this.alphaFormula = alpha->Math.max( 1.0f - 1.0f * this.age / this.lifetime, 0.0f );
		this.scaleFormula = lifeRatio->1.0f - 0.2f * lifeRatio;
		this.lifetime = this.random.nextInt( Utility.secondsToTicks( 0.3 ) ) + Utility.secondsToTicks( 0.4 );
		float colorFactor = Random.nextFloat( 0.5f, 0.85f );

		this.scale( 0.5f );
		this.setSize( 0.125f, 0.125f );
		this.setColor( colorFactor, 0.0f, colorFactor );
		this.pickSprite( spriteSet );
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory extends SimpleFactory {
		public Factory( SpriteSet sprite ) {
			super( sprite, TelekinesisParticle::new );
		}
	}
}