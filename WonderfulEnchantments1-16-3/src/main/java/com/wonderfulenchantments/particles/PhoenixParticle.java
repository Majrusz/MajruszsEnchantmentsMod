package com.wonderfulenchantments.particles;

import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class PhoenixParticle extends SpriteTexturedParticle {
	public PhoenixParticle( ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed ) {
		super( world, x, y, z, xSpeed, ySpeed, zSpeed );
		this.motionX = this.motionX * ( double )0.01F + xSpeed;
		this.motionY = this.motionY * ( double )0.01F + ySpeed * 0.0F;
		this.motionZ = this.motionZ * ( double )0.01F + zSpeed;

		this.maxAge = ( 20 + WonderfulEnchantments.RANDOM.nextInt( 10 ) );
	}

	@Override
	public float getScale( float scaleFactor ) {
		float factor = ( ( float )this.age + scaleFactor ) / ( float )this.maxAge;
		return this.particleScale * ( 1.0F - factor * 0.5F );
	}

	@Override
	public void tick() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if( this.age++ >= this.maxAge )
			this.setExpired();

		else {
			this.move( this.motionX, this.motionY, this.motionZ );
			this.motionX *= ( double )0.75F;
			this.motionY *= ( double )0.75F;
			this.motionZ *= ( double )0.75F;
			if( this.onGround ) {
				this.motionX *= ( double )0.5F;
				this.motionZ *= ( double )0.5F;
			}
		}
	}

	@Override
	public IParticleRenderType getRenderType() {
		return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory implements IParticleFactory< BasicParticleType > {
		private final IAnimatedSprite spriteSet;

		public Factory( IAnimatedSprite sprite ) {
			this.spriteSet = sprite;
		}

		@Override
		public Particle makeParticle( BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed ) {
			PhoenixParticle particle = new PhoenixParticle( world, x, y, z, xSpeed, ySpeed, zSpeed );
			particle.selectSpriteRandomly( this.spriteSet );

			return particle;
		}
	}
}