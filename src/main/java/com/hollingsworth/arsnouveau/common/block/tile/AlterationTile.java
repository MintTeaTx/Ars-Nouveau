package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.perk.IPerkHolder;
import com.hollingsworth.arsnouveau.api.util.PerkUtil;
import com.hollingsworth.arsnouveau.common.armor.MagicArmor;
import com.hollingsworth.arsnouveau.common.block.AlterationTable;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.phys.AABB;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class AlterationTile extends ModdedTile implements IAnimatable, ITickable {

    public ItemStack armorStack = ItemStack.EMPTY;
    public ItemEntity renderEntity;
    public List<ItemStack> perkList = new ArrayList<>();

    public AlterationTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public AlterationTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.ARMOR_TILE, pos, state);
    }

    @Override
    public void registerControllers(AnimationData animationData) {}

    public AnimationFactory factory = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return factory;
    }

    public @Nullable AlterationTile getLogicTile() {
        AlterationTile tile = this;
        if (!isMasterTile()) {
            BlockEntity tileEntity = level.getBlockEntity(getBlockPos().relative(AlterationTable.getConnectedDirection(getBlockState())));
            tile = tileEntity instanceof AlterationTile alterationTile ? alterationTile : null;
        }
        return tile;
    }

    public boolean isMasterTile() {
        return getBlockState().getValue(AlterationTable.PART) == BedPart.HEAD;
    }

    public void setArmorStack(ItemStack stack, Player player){
        IPerkHolder<ItemStack> holder = PerkUtil.getPerkHolder(stack);
        if(holder instanceof MagicArmor.ArmorPerkHolder armorPerkHolder){
            this.perkList = new ArrayList<>(armorPerkHolder.getPerkStacks());
            armorPerkHolder.setPerkStacks(new ArrayList<>());
            this.armorStack = stack.copy();
            stack.shrink(1);
            updateBlock();
        }
    }

    public void removePerk(Player player) {
        if(!perkList.isEmpty()){
            ItemStack stack = perkList.get(0);
            if(!player.addItem(stack.copy())){
                level.addFreshEntity(new ItemEntity(level, player.position().x(), player.position().y(), player.position().z(), stack.copy()));
            }
            perkList.remove(0);
        }
    }

    public void removeArmorStack(Player player){
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(armorStack);
        if(perkHolder instanceof MagicArmor.ArmorPerkHolder armorPerkHolder){
            armorPerkHolder.setPerkStacks(new ArrayList<>(this.perkList));
        }
        if(!player.addItem(armorStack.copy())){
            level.addFreshEntity(new ItemEntity(level, player.position().x(), player.position().y(), player.position().z(), armorStack.copy()));
        }
        this.armorStack = ItemStack.EMPTY;
        updateBlock();
    }

    public void addPerkStack(ItemStack stack, Player player){
        IPerkHolder<ItemStack> perkHolder = PerkUtil.getPerkHolder(armorStack);
        if(!(perkHolder instanceof MagicArmor.ArmorPerkHolder armorPerkHolder)){
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.set_armor"));
            return;
        }
        if(this.perkList.size() > armorPerkHolder.getSlotsForTier().size()){
            PortUtil.sendMessage(player, Component.translatable("ars_nouveau.perk.max_perks"));
            return;
        }
        this.perkList.add(stack.copy());
        player.getMainHandItem().shrink(1);
        updateBlock();
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag armorTag = new CompoundTag();
        armorStack.save(armorTag);
        tag.put("armorStack", armorTag);
        tag.putInt("numPerks", perkList.size());
        int count = 0;
        for(ItemStack i : perkList){
            CompoundTag perkTag = new CompoundTag();
            i.save(perkTag);
            tag.put("perk" + count, perkTag);
            count++;
        }
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.armorStack = ItemStack.of(compound.getCompound("armorStack"));
        int count = compound.getInt("numPerks");
        for(int i = 0; i < count; i++){
            CompoundTag perkTag = compound.getCompound("perk" + i);
            ItemStack perk = ItemStack.of(perkTag);
            perkList.add(perk);
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return super.getRenderBoundingBox().inflate(2);
    }

}
