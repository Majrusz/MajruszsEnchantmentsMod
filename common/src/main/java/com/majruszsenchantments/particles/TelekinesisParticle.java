package com.majruszsenchantments.particles;

import com.mlib.annotation.Dist;
import com.mlib.annotation.OnlyIn;
import com.mlib.client.CustomParticle;
import com.mlib.math.Random;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;

@OnlyIn( Dist.CLIENT )
public class TelekinesisParticle extends CustomParticle {
	private final SpriteSet spriteSet;

	public TelekinesisParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );

		this.spriteSet = spriteSet;
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		this.xdFormula = xd->0.0;
		this.ydFormula = yd->0.0;
		this.zdFormula = zd->0.0;
		this.alpha = 0.0f;
		this.alphaFormula = alpha->Math.max( 5.0f * ( 1.0f - ( float )this.age / this.lifetime ), 0.0f ) % 1.0f;
		this.scaleFormula = lifeRatio->1.0f;

		float color = Random.nextFloat( 0.8f, 1.0f );
		this.setSpriteFromAge( this.spriteSet );
		this.setColor( color, color, color );
	}

	@Override
	public void tick() {
		super.tick();

		if( !this.removed ) {
			this.setSprite( this.spriteSet.get( ( this.lifetime * this.age / 4 ) % this.lifetime, this.lifetime ) );
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	public void update( TelekinesisParticleType.Options options ) {
		this.age = options.age;
		this.lifetime = options.lifetime;
		this.alphaFormula = alpha->Math.max( options.pulseSpeed * ( 1.0f - ( float )this.age / this.lifetime ), 0.0f ) % 1.0f;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory extends CustomParticle.Factory< TelekinesisParticle, TelekinesisParticleType.Options > {
		public Factory( SpriteSet sprite ) {
			super( sprite, TelekinesisParticle::new, TelekinesisParticle::update );
		}
	}
}
