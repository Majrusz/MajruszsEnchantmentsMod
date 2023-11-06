package com.majruszsenchantments.particles;

import com.majruszsenchantments.MajruszsEnchantments;
import com.mlib.data.Serializables;
import com.mlib.particles.CustomParticleOptions;
import com.mlib.particles.CustomParticleType;

public class TelekinesisParticleType extends CustomParticleType< TelekinesisParticleType.Options > {
	static {
		Serializables.get( Options.class )
			.defineInteger( "age", s->s.age, ( s, v )->s.age = v )
			.defineInteger( "lifetime", s->s.lifetime, ( s, v )->s.lifetime = v )
			.defineFloat( "pulse_speed", s->s.pulseSpeed, ( s, v )->s.pulseSpeed = v );
	}

	public TelekinesisParticleType() {
		super( Options.class, Options::new );
	}

	public static class Options extends CustomParticleOptions< Options > {
		public int age;
		public int lifetime;
		public float pulseSpeed;

		public Options() {
			super( Options.class, MajruszsEnchantments.TELEKINESIS_PARTICLE );
		}

		public Options( int age, int lifetime, float pulseSpeed ) {
			this();

			this.age = age;
			this.lifetime = lifetime;
			this.pulseSpeed = pulseSpeed;
		}
	}
}
