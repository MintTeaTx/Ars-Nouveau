package com.hollingsworth.arsnouveau.common.spell.augment;

import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;

public class AugmentSensitive extends AbstractAugment {
    public static AugmentSensitive INSTANCE = new AugmentSensitive();

    private AugmentSensitive() {
        super(GlyphLib.AugmentSensitiveID, "Sensitive");
    }

    @Override
    public String getBookDescription() {
        return "Causes forms to target blocks they normally cannot target. Projectile and Orbit will target grass, and Touch will target fluids and air.";
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }
}
