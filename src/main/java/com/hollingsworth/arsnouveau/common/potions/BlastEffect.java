package com.hollingsworth.arsnouveau.common.potions;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Explosion;

public class BlastEffect extends MobEffect {
    public BlastEffect() {
        super(MobEffectCategory.HARMFUL, new ParticleColor(250, 0, 0).getColor());
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        pLivingEntity.level.explode(null, pLivingEntity.getX(), pLivingEntity.getY() + 1, pLivingEntity.getZ(), 2.0f + pAmplifier, false, Explosion.BlockInteraction.NONE);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return pDuration == 1;
    }
}
