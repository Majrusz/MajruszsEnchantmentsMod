package com.majruszsenchantments.particles;

import com.majruszlibrary.annotation.Dist;
import com.majruszlibrary.annotation.OnlyIn;
import com.majruszlibrary.client.CustomParticle;
import com.majruszlibrary.math.Random;
import com.majruszlibrary.time.TimeHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;

@OnlyIn( Dist.CLIENT )
public class DodgeParticle extends CustomParticle {
	private final SpriteSet spriteSet;

	public DodgeParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );

		this.spriteSet = spriteSet;
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		this.xdFormula = xd->xd * 0.9;
		this.ydFormula = yd->yd * 0.0;
		this.zdFormula = zd->zd * 0.9;
		this.lifetime = TimeHelper.toTicks( Random.nextFloat( 1.0f, 1.5f ) );
		this.alpha = 0.0f;
		this.alphaFormula = alpha->Mth.clamp( 3.0f * ( 1.0f - Math.abs( 2.0f * this.age / this.lifetime - 1.0f ) ), 0.0f, 1.0f );
		this.scaleFormula = lifeRatio->1.0f;

		float color = Random.nextFloat( 0.7f, 1.0f );
		this.setSpriteFromAge( this.spriteSet );
		this.setColor( color, color, color );
	}

	@Override
	public void tick() {
		super.tick();

		this.setSpriteFromAge( this.spriteSet );
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
