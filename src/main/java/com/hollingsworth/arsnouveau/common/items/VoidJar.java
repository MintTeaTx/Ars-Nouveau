package com.hollingsworth.arsnouveau.common.items;

import com.hollingsworth.arsnouveau.api.item.IScribeable;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class VoidJar extends ModItem implements IScribeable {

    public VoidJar() {
        super(ItemsRegistry.defaultItemProperties().stacksTo(1));
    }

    public void toggleStatus(Player playerEntity, ItemStack stack) {
        VoidJarData jarData = new VoidJarData(stack);
        jarData.setActive(!jarData.isActive());
        if (jarData.isActive()) {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.on"));
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.off"));
        }
    }

    public static boolean tryVoiding(Player player, ItemStack pickingUp) {
        NonNullList<ItemStack> list = player.inventory.items;
        for (int i = 0; i < 9; i++) {
            ItemStack jar = list.get(i);
            if (jar.getItem() == ItemsRegistry.VOID_JAR.get()) {
                VoidJarData jarData = new VoidJarData(jar);
                if (jarData.isActive() && jarData.containsStack(pickingUp)) {
                    CapabilityRegistry.getMana(player).ifPresent(iMana -> iMana.addMana(5.0 * pickingUp.getCount()));
                    pickingUp.setCount(0);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player player, InteractionHand handIn) {
        if (worldIn.isClientSide)
            return super.use(worldIn, player, handIn);
        ItemStack stack = player.getItemInHand(handIn);
        VoidJarData data = new VoidJarData(stack);

        if (handIn == InteractionHand.MAIN_HAND) {
            ItemStack stackToWrite = player.getOffhandItem();
            if (player.isShiftKeyDown()) {
                toggleStatus(player, stack);
                return InteractionResultHolder.consume(stack);
            }
            data.writeWithFeedback(player, stackToWrite);
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip2, TooltipFlag flagIn) {
        if(!stack.hasTag())
            return;
        VoidJarData data = new VoidJarData(stack);
        if (data.isActive()) {
            tooltip2.add(Component.translatable("ars_nouveau.on"));
        } else {
            tooltip2.add(Component.translatable("ars_nouveau.off"));
        }
        for (ItemStack s : data.getItems()) {
            tooltip2.add(s.getHoverName());
        }

        super.appendHoverText(stack, worldIn, tooltip2, flagIn);
    }

    @Override
    public boolean onScribe(Level world, BlockPos pos, Player player, InteractionHand handIn, ItemStack thisStack) {
        VoidJarData data = new VoidJarData(thisStack);
        return data.writeWithFeedback(player, player.getItemInHand(handIn));
    }

    public static class VoidJarData extends ItemScroll.ItemScrollData{
        private boolean active;
        public VoidJarData(ItemStack stack) {
            super(stack);
            CompoundTag tag = getItemTag(stack);
            if(tag == null)
                return;
            active = tag.getBoolean("on");
        }

        public boolean isActive(){
            return active;
        }

        public void setActive(boolean active){
            this.active = active;
            writeItem();
        }

        @Override
        public void writeToNBT(CompoundTag tag) {
            super.writeToNBT(tag);
            tag.putBoolean("on", active);
        }

        @Override
        public String getTagString() {
            return "an_voidJar";
        }
    }
}
