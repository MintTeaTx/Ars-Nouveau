package com.hollingsworth.arsnouveau.client.particle;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;

public class GlowParticleType extends ParticleType<ColorParticleTypeData> {
    public GlowParticleType() {
        super(false, ColorParticleTypeData.DESERIALIZER);
    }

    @Override
    public Codec<ColorParticleTypeData> codec() {
        return ColorParticleTypeData.CODEC;
    }
}
