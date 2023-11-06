package com.majruszsenchantments.particles;

import com.mlib.annotation.Dist;
import com.mlib.annotation.OnlyIn;
import com.mlib.client.CustomParticle;
import com.mlib.math.Random;
import com.mlib.time.TimeHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.Mth;

@OnlyIn( Dist.CLIENT )
public class SmelterParticle extends CustomParticle {
	private final SpriteSet spriteSet;

	public SmelterParticle( ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet ) {
		super( level, x, y, z, xSpeed, ySpeed, zSpeed );

		this.spriteSet = spriteSet;
		this.xd = xSpeed;
		this.yd = ySpeed;
		this.zd = zSpeed;
		this.xdFormula = xd->xd * 0.85;
		this.ydFormula = yd->yd * 0.85;
		this.zdFormula = zd->zd * 0.85;
		this.lifetime = TimeHelper.toTicks( Random.nextFloat( 1.0f, 1.5f ) );
		this.alpha = 0.0f;
		this.alphaFormula = alpha->1.0f;
		this.scaleFormula = lifeRatio->Mth.clamp( 1.5f * ( 1.0f - ( float )this.age / this.lifetime ), 0.0f, 0.75f );

		float color = Random.nextFloat( 0.8f, 1.0f );
		this.pickSprite( this.spriteSet );
		this.setColor( color, color, color );
	}

	@Override
	public ParticleRenderType getRenderType() {
		return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
	}

	@OnlyIn( Dist.CLIENT )
	public static class Factory extends SimpleFactory {
		public Factory( SpriteSet sprite ) {
			super( sprite, SmelterParticle::new );
		}
	}
}
