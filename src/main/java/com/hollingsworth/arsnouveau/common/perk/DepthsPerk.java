package com.hollingsworth.arsnouveau.common.perk;

import com.google.common.collect.Multimap;
import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.perk.Perk;
import com.hollingsworth.arsnouveau.api.perk.PerkAttributes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class DepthsPerk extends Perk {

    public static DepthsPerk INSTANCE = new DepthsPerk(new ResourceLocation(ArsNouveau.MODID, "thread_depths"));
    public static final UUID PERK_UUID = UUID.fromString("ce320c42-9d63-4b83-9e69-ef144790d667");

    public DepthsPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.DEPTHS.get(), new AttributeModifier(PERK_UUID, "Depths", 0.2 * slotValue, AttributeModifier.Operation.ADDITION)).build();
    }

    @Override
    public int getCountCap() {
        return 3;
    }

    @Override
    public String getLangDescription() {
        return "Greatly increases the amount of time you may breathe underwater by reducing the chance your air will decrease. Stacks with Respiration Enchantments.";
    }

    @Override
    public String getLangName() {
        return "Depths";
    }
}