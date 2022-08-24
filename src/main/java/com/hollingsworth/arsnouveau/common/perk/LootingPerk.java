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

public class LootingPerk extends Perk {

    public static final LootingPerk INSTANCE = new LootingPerk(new ResourceLocation(ArsNouveau.MODID, "thread_drygmy"));
    public static final UUID PERK_UUID = UUID.fromString("ff9459e5-ec2c-44c8-ac3b-19c78c76b4bb");


    public LootingPerk(ResourceLocation key) {
        super(key);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot pEquipmentSlot, ItemStack stack, int slotValue) {
        return attributeBuilder().put(PerkAttributes.DRYGMY.get(), new AttributeModifier(PERK_UUID, "LootingPerk", slotValue, AttributeModifier.Operation.ADDITION)).build();
    }

    @Override
    public int getCountCap() {
        return 3;
    }

    @Override
    public String getLangName() {
        return "Drygmy";
    }

    @Override
    public String getLangDescription() {
        return "Grants an additional stack of looting.";
    }
}