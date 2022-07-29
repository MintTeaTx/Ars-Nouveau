package com.hollingsworth.arsnouveau.client.renderer.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.PotionMelderTile;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class PotionMelderModel extends AnimatedGeoModel<PotionMelderTile> {

    public static final ResourceLocation model = new ResourceLocation(ArsNouveau.MODID, "geo/potion_melder.geo.json");
    public static final ResourceLocation texture = new ResourceLocation(ArsNouveau.MODID, "textures/blocks/potion_stirrer.png");
    public static final ResourceLocation anim = new ResourceLocation(ArsNouveau.MODID, "animations/potion_melder_animation.json");

    @Override
    public ResourceLocation getModelResource(PotionMelderTile volcanicTile) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(PotionMelderTile volcanicTile) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(PotionMelderTile volcanicTile) {
        return anim;
    }
}
