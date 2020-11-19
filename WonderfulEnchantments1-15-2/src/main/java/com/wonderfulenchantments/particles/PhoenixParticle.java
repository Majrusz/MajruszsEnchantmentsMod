package com.wonderfulenchantments.particles;

import com.wonderfulenchantments.WonderfulEnchantments;
import net.minecraft.client.particle.*;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn( Dist.CLIENT )
public class PhoenixParticle extends SpriteTexturedParticle {
	public PhoenixParticle( World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed ) {
		super( world, x, y, z, xSpeed, ySpeed, zSpeed );
		this.motionX = this.motionX * 0.01D + xSpeed;
		this.motionY = this.motionY * 0.01D + ySpeed * 0.0D;
		this.motionZ = this.motionZ * 0.01D + zSpeed;

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
			this.motionX *= 0.75D;
			this.motionY *= 0.75D;
			this.motionZ *= 0.75D;
			if( this.onGround ) {
				this.motionX *= 0.5D;
				this.motionZ *= 0.5D;
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
		public Particle makeParticle( BasicParticleType type, World world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed ) {
			PhoenixParticle particle = new PhoenixParticle( world, x, y, z, xSpeed, ySpeed, zSpeed );
			particle.selectSpriteRandomly( this.spriteSet );

			return particle;
		}
	}
}