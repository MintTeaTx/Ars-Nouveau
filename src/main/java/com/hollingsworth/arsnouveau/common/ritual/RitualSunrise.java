package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.MathUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;

public class RitualSunrise extends AbstractRitual {
    @Override
    protected void tick() {
        ParticleUtil.spawnRitualSkyEffect(this, tile, rand, new ParticleColor.IntWrapper(255 + rand.nextInt(1), 255 + rand.nextInt(1), 25 + rand.nextInt(1)));
        if (getWorld().getGameTime() % 20 == 0 && !getWorld().isClientSide) {
            incrementProgress();
            if (getProgress() >= 18) {
                ServerLevel world = (ServerLevel) getWorld();
                world.setDayTime(MathUtil.getNextDaysTime(world, MathUtil.DAY_TIME));
                setFinished();
            }
        }

    }


    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public String getLangName() {
        return "Sunrise";
    }

    @Override
    public String getLangDescription() {
        return "Sets the time to day.";
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(
                rand.nextInt(255),
                rand.nextInt(255),
                rand.nextInt(25));
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, RitualLib.SUNRISE);
    }
}

