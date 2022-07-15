package com.hollingsworth.arsnouveau.client.renderer.item;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.items.SpellBow;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class SpellBowModel extends AnimatedGeoModel<SpellBow> {

    @Override
    public ResourceLocation getModelResource(SpellBow wand) {
        return new ResourceLocation(ArsNouveau.MODID, "geo/spellbow.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SpellBow wand) {
        return new ResourceLocation(ArsNouveau.MODID, "textures/items/spellbow.png");
    }

    @Override
    public ResourceLocation getAnimationResource(SpellBow wand) {
        return new ResourceLocation(ArsNouveau.MODID, "animations/wand_animation.json");
    }


}
