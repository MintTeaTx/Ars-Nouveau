package com.hollingsworth.arsnouveau.common.command;

import com.google.common.collect.ImmutableList;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collection;

public class ResetCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ars-reset").
                requires(sender -> sender.hasPermission(2))
                .executes(context -> resetPlayers(context.getSource(), ImmutableList.of(context.getSource().getEntityOrException())))
                .then(Commands.argument("targets", EntityArgument.entities())
                        .executes(context -> resetPlayers(context.getSource(), EntityArgument.getEntities(context, "targets")))));
    }

    private static int resetPlayers(CommandSourceStack source, Collection<? extends Entity> entities) {
        for (Entity e : entities) {
            if (!(e instanceof LivingEntity))
                continue;
            CapabilityRegistry.getMana((LivingEntity) e).ifPresent(iMana -> {
                iMana.setBookTier(0);
                iMana.setGlyphBonus(0);
            });
            CapabilityRegistry.getPlayerDataCap((LivingEntity) e).ifPresent(iPlayerCap -> iPlayerCap.setKnownGlyphs(new ArrayList<>()));
            CapabilityRegistry.getPlayerDataCap((LivingEntity) e).ifPresent(ifam -> ifam.setUnlockedFamiliars(new ArrayList<>()));
        }
        source.sendSuccess(Component.translatable("ars_nouveau.reset.cleared"), true);
        return 1;
    }
}
