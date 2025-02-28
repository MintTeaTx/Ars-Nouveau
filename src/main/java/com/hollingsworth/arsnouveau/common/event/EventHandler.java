package com.hollingsworth.arsnouveau.common.event;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.DispelEvent;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.LavaLily;
import com.hollingsworth.arsnouveau.common.command.ResetCommand;
import com.hollingsworth.arsnouveau.common.items.VoidJar;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class EventHandler {


    @SubscribeEvent(priority= EventPriority.LOWEST)
    public static void itemPickupEvent( EntityItemPickupEvent event) {
        PlayerEntity player = event.getPlayer();
        ItemStack pickingUp = event.getItem().getItem();
        boolean voided = VoidJar.tryVoiding(player, pickingUp);
        if (voided) event.setResult(Event.Result.ALLOW);
    }


    @SubscribeEvent
    public static void livingAttackEvent(LivingAttackEvent e){
        if(e.getSource() == DamageSource.HOT_FLOOR && e.getEntityLiving() != null && !e.getEntity().getEntityWorld().isRemote){
            World world = e.getEntity().world;
            if(world.getBlockState(e.getEntityLiving().getPosition()).getBlock() instanceof LavaLily){
                e.setCanceled(true);
            }
        }
    }



    @SubscribeEvent
    public static void jumpEvent(LivingEvent.LivingJumpEvent e) {
        if(e.getEntityLiving() == null  || e.getEntityLiving().getActivePotionEffect(Effects.SLOWNESS) == null)
            return;
        EffectInstance effectInstance = e.getEntityLiving().getActivePotionEffect(Effects.SLOWNESS);
        if(effectInstance.getAmplifier() >= 20){
            e.getEntityLiving().setMotion(0,0,0);
        }
    }



    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent e) {
        if(e.getEntityLiving().getEntityWorld().isRemote || !Config.SPAWN_BOOK.get())
            return;
        CompoundNBT tag = e.getPlayer().getPersistentData().getCompound(PlayerEntity.PERSISTED_NBT_TAG);
        String book_tag = "an_book_";
        if(tag.getBoolean(book_tag))
            return;

        LivingEntity entity = e.getEntityLiving();
        e.getEntityLiving().getEntityWorld().addEntity(new ItemEntity(entity.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), new ItemStack(ItemsRegistry.wornNotebook)));
        tag.putBoolean(book_tag, true);
        e.getPlayer().getPersistentData().put(PlayerEntity.PERSISTED_NBT_TAG, tag);
    }


    @SubscribeEvent
    public static void clientTickEnd(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            ClientInfo.ticksInGame++;
        }
    }

    @SubscribeEvent
    public static void playerDamaged(LivingHurtEvent e){
        if(e.getEntityLiving() != null && e.getEntityLiving().getActivePotionMap().containsKey(ModPotions.SHIELD_POTION)
                && (e.getSource() == DamageSource.MAGIC || e.getSource() == DamageSource.GENERIC || e.getSource() instanceof EntityDamageSource)){
            float damage = e.getAmount() - (1.0f + 0.5f * e.getEntityLiving().getActivePotionMap().get(ModPotions.SHIELD_POTION).getAmplifier());
            e.setAmount(Math.max(0, damage));
        }
    }

    @SubscribeEvent
    public static void entityHurt(LivingHurtEvent e){
        if(e.getEntityLiving() != null && e.getSource() == DamageSource.LIGHTNING_BOLT && e.getEntityLiving().getActivePotionEffect(ModPotions.SHOCKED_EFFECT) != null){
            float damage = e.getAmount() + 3.0f + 3.0f * e.getEntityLiving().getActivePotionEffect(ModPotions.SHOCKED_EFFECT).getAmplifier();
            e.setAmount(Math.max(0, damage));
        }
    }

    @SubscribeEvent
    public static void dispelEvent(DispelEvent event){
        if(event.rayTraceResult instanceof EntityRayTraceResult && ((EntityRayTraceResult) event.rayTraceResult).getEntity() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) ((EntityRayTraceResult) event.rayTraceResult).getEntity();
            if(entity instanceof WitchEntity){
                if(entity.getHealth() <= entity.getMaxHealth()/2){
                    entity.remove();
                    ParticleUtil.spawnPoof((ServerWorld) event.world, entity.getPosition());
                    event.world.addEntity(new ItemEntity(event.world, entity.getPosX(), entity.getPosY(), entity.getPosZ(), new ItemStack(ItemsRegistry.WIXIE_SHARD)));
                }
            }

        }

    }


    @SubscribeEvent
    public static void commandRegister(RegisterCommandsEvent event){
        ResetCommand.register(event.getDispatcher());
    }

    private EventHandler(){}

}
