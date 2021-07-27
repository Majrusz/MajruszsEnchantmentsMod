package com.wonderfulenchantments.particles;

import com.mlib.MajruszLibrary;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/** Particles created when entity falls with Phoenix Dive enchanment. */
@OnlyIn( Dist.CLIENT )
public class PhoenixParticle extends TextureSheetParticle {
	public PhoenixParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );
		this.xd = this.xd * 0.01D + xSpeed;
		this.yd = this.yd * 0.01D + ySpeed * 0.0D;
		this.zd = this.zd * 0.01D + zSpeed;

		this.lifetime = ( 20 + MajruszLibrary.RANDOM.nextInt( 10 ) );
	}

	@Override
	public float getQuadSize( float sizeFactor ) {
		float factor = ( ( float )this.age + sizeFactor ) / ( float )this.lifetime;
		return this.quadSize * ( 1.0F - factor * 0.5F );
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;

		if( this.age++ >= this.lifetime )
			this.remove();

		else {
			this.move( this.xd, this.yd, this.zd );
			this.xd *= 0.75D;
			this.yd *= 0.75D;
			this.zd *= 0.75D;
			if( this.onGround ) {
				this.xd *= 0.5D;
				this.zd *= 0.5D;
			}
		}
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory implements ParticleProvider< SimpleParticleType > {
		private final SpriteSet spriteSet;

		public Factory( SpriteSet sprite ) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle createParticle( SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed,
			double zSpeed
		) {
			PhoenixParticle particle = new PhoenixParticle( level, x, y, z, xSpeed, ySpeed, zSpeed );
			particle.pickSprite( this.spriteSet );

			return particle;
		}
	}
}