package com.hollingsworth.arsnouveau.client.gui.buttons;

import com.hollingsworth.arsnouveau.api.familiar.AbstractFamiliarHolder;
import com.hollingsworth.arsnouveau.client.gui.book.GuiFamiliarScreen;
import com.hollingsworth.arsnouveau.client.gui.utils.RenderUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class FamiliarButton extends Button {

    public GuiFamiliarScreen parent;
    public AbstractFamiliarHolder familiarHolder;

    public FamiliarButton(GuiFamiliarScreen parent, int x, int y, AbstractFamiliarHolder familiar) {
        super(x, y, 16, 16, Component.nullToEmpty(""), parent::onGlyphClick);
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = 16;
        this.height = 16;
        this.familiarHolder = familiar;
    }

    @Override
    public void render(PoseStack ms, int mouseX, int mouseY, float partialTicks) {
        if (visible) {
            RenderUtils.drawItemAsIcon(familiarHolder.getOutputItem().getItem(), ms, x, y, 16, false);
            if (parent.isMouseInRelativeRange(mouseX, mouseY, x, y, width, height)) {
                List<Component> tip = new ArrayList<>();
                if (Screen.hasShiftDown()) {
                    tip.add(familiarHolder.getLangDescription());
                } else {
                    tip.add(familiarHolder.getLangName());
                    tip.add(Component.translatable("tooltip.ars_nouveau.hold_shift"));
                }
                parent.tooltip = tip;
            }

        }
    }

}