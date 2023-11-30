package com.majruszsenchantments.particles;

import com.majruszlibrary.data.Reader;
import com.majruszsenchantments.MajruszsEnchantments;
import com.majruszlibrary.data.Serializables;
import com.majruszlibrary.particles.CustomParticleOptions;
import com.majruszlibrary.particles.CustomParticleType;

public class TelekinesisParticleType extends CustomParticleType< TelekinesisParticleType.Options > {
	static {
		Serializables.get( Options.class )
			.define( "age", Reader.integer(), s->s.age, ( s, v )->s.age = v )
			.define( "lifetime", Reader.integer(), s->s.lifetime, ( s, v )->s.lifetime = v )
			.define( "pulse_speed", Reader.number(), s->s.pulseSpeed, ( s, v )->s.pulseSpeed = v );
	}

	public TelekinesisParticleType() {
		super( Options::new );
	}

	public static class Options extends CustomParticleOptions< Options > {
		public int age;
		public int lifetime;
		public float pulseSpeed;

		public Options() {
			super( MajruszsEnchantments.TELEKINESIS_PARTICLE );
		}

		public Options( int age, int lifetime, float pulseSpeed ) {
			this();

			this.age = age;
			this.lifetime = lifetime;
			this.pulseSpeed = pulseSpeed;
		}
	}
}
