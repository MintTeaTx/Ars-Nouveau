package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.recipe.GlyphPressRecipe;
import com.hollingsworth.arsnouveau.api.spell.ISpellTier;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketOneShotAnimation;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class GlyphPressTile extends AnimatedTile implements ITickableTileEntity, IAnimatable, IAnimationListener, ISidedInventory {
    public long frames;
    public boolean isCrafting;
    public ItemStack reagentItem = ItemStack.EMPTY;
    public ItemStack baseMaterial = ItemStack.EMPTY;
    public ItemStack oldBaseMat;
    public ItemEntity entity;
    public long timeStartedSpraying;

    public GlyphPressTile() {
        super(BlockRegistry.GLYPH_PRESS_TILE);
        frames = 0;
    }

    @Override
    public void read(BlockState state,CompoundNBT compound) {
        reagentItem = ItemStack.read((CompoundNBT)compound.get("itemStack"));
        baseMaterial = ItemStack.read((CompoundNBT)compound.get("baseMat"));
        oldBaseMat = ItemStack.read((CompoundNBT)compound.get("oldBase"));
        isCrafting = compound.getBoolean("crafting");
        timeStartedSpraying = compound.getLong("spraying");
        super.read(state, compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        if(reagentItem != null) {
            CompoundNBT reagentTag = new CompoundNBT();
            reagentItem.write(reagentTag);
            compound.put("itemStack", reagentTag);
        }
        if(baseMaterial != null){
            CompoundNBT baseMatTag = new CompoundNBT();
            baseMaterial.write(baseMatTag);
            compound.put("baseMat", baseMatTag);
        }

        if(oldBaseMat != null){
            CompoundNBT baseMatTag = new CompoundNBT();
            oldBaseMat.write(baseMatTag);
            compound.put("oldBase", baseMatTag);
        }
        compound.putBoolean("crafting", isCrafting);
        compound.putLong("spraying", timeStartedSpraying);
        return super.write(compound);
    }


    @Override
    public void tick() {
        if(!world.isRemote && world.getGameTime() % 20 == 0 && reagentItem != null && baseMaterial != null && canCraft(reagentItem.getItem(), baseMaterial.getItem()))
            craft(FakePlayerFactory.getMinecraft((ServerWorld) world));

        if(world.isRemote || !isCrafting){
            return;
        }
        counter += 1;
        if(counter == 110){
            GlyphPressRecipe recipe = ArsNouveauAPI.getInstance().getGlyphPressRecipe(world, reagentItem.getItem(), getTier(baseMaterial.getItem()));
            oldBaseMat = this.baseMaterial.copy();
            this.baseMaterial = recipe.output.copy();
            updateBlock();
        }
        if(counter ==150) {
            isCrafting = false;
            GlyphPressRecipe recipe = ArsNouveauAPI.getInstance().getGlyphPressRecipe(world, reagentItem.getItem(), getTier(oldBaseMat.getItem()));
            if(recipe == null)
                return;

            ItemStack stack = recipe.output.copy();
            stack = depositItem(stack);
            if(!stack.isEmpty())
                world.addEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY()+ 1.5, pos.getZ()+0.5, stack));
            reagentItem = new ItemStack(null);
            this.baseMaterial = new ItemStack(null);
            this.oldBaseMat = new ItemStack(null);
            counter = 1;

        }
        updateBlock();
    }

    public ItemStack depositItem(ItemStack stack){
        ArrayList<IInventory> iInventories = new ArrayList<>();
        for(Direction d : Direction.values()){
            IInventory iInventory =  HopperTileEntity.getInventoryAtPosition(world, pos.offset(d));
            if(iInventory != null && !(iInventory instanceof HopperTileEntity))
                iInventories.add(iInventory);
        }

        for(IInventory i : iInventories){
            if(stack == ItemStack.EMPTY || stack == null)
                break;
            stack = HopperTileEntity.putStackInInventoryAllSlots(null, i, stack, null);
        }
        return stack;
    }

    public void updateBlock(){
        BlockState state = world.getBlockState(pos);
        world.notifyBlockUpdate(pos, state, state, 2);
    }


    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return (reagentItem == null || reagentItem.isEmpty()) && (baseMaterial == null || baseMaterial.isEmpty());
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? reagentItem : index == 1 ? baseMaterial : ItemStack.EMPTY ;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if(index == 0 && reagentItem != null){
            reagentItem.shrink(count);
            return reagentItem;
        }else if(index == 1 && baseMaterial != null){
            baseMaterial.shrink(count);
            return  baseMaterial;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack = reagentItem;
        reagentItem.setCount(0);
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if(index == 0)
            reagentItem = stack;
        if(index == 1)
            baseMaterial = stack;

        updateBlock();
    }

    @Override
    public boolean isUsableByPlayer(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        reagentItem = ItemStack.EMPTY;
        baseMaterial = ItemStack.EMPTY;
    }

    public boolean craft(PlayerEntity playerEntity) {
        if(isCrafting || baseMaterial == null || baseMaterial == ItemStack.EMPTY)
            return false;
        GlyphPressRecipe recipe = ArsNouveauAPI.getInstance().getGlyphPressRecipe(world, reagentItem.getItem(), getTier(this.baseMaterial.getItem()));
        if(recipe == null)
            return false;

        int manaCost = recipe.tier == ISpellTier.Tier.ONE ? 1500 : (recipe.tier == ISpellTier.Tier.TWO ? 2500 : 5000);
        BlockPos jar = ManaUtil.takeManaNearbyWithParticles(pos, world, 5, manaCost);
        if(jar != null){
            isCrafting = true;
            Networking.sendToNearby(world, pos, new PacketOneShotAnimation(pos));
            return true;
        }

        playerEntity.sendMessage(new TranslationTextComponent("ars_nouveau.glyph_press.no_mana"), Util.DUMMY_UUID);
        return false;
    }

    public Item getMatchingClay(ISpellTier.Tier tier){
        if(tier == ISpellTier.Tier.ONE)
            return ItemsRegistry.magicClay;
        else if(tier == ISpellTier.Tier.TWO){
            return ItemsRegistry.marvelousClay;
        }
        return ItemsRegistry.mythicalClay;
    }


    public ISpellTier.Tier getTier(Item clay){
        if(clay == ItemsRegistry.magicClay)
            return ISpellTier.Tier.ONE;
        else if(clay == ItemsRegistry.marvelousClay){
            return ISpellTier.Tier.TWO;
        }else if(clay == ItemsRegistry.mythicalClay)
            return ISpellTier.Tier.THREE;
        return null;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "controller", 1, this::idlePredicate));
    }
    AnimationFactory manager = new AnimationFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    private <E extends TileEntity & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        return PlayState.CONTINUE;
    }

    @Override
    public void startAnimation(int arg) {
        AnimationController controller = this.manager.getOrCreateAnimationData(this.hashCode()).getAnimationControllers().get("controller");
        controller.markNeedsReload();
        controller.setAnimation(new AnimationBuilder().addAnimation("press", false));
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return side == Direction.UP ? new int[]{0} : side != Direction.DOWN ? new int[]{1} : new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, @Nullable Direction direction) {
        if(isCrafting)
            return false;
        if(index == 0 && (reagentItem == null || reagentItem.isEmpty()) && direction == Direction.UP){ // reagent
            return baseMaterial != null && canCraft(itemStackIn.getItem(), baseMaterial.getItem());
        }else if(index == 1 && direction != Direction.UP && direction != Direction.DOWN && (baseMaterial == null || baseMaterial.isEmpty())){
            return getTier(itemStackIn.getItem()) != null;
        }
        return false;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return false;
    }

    public boolean canCraft(Item reagent, Item base){
        if(reagent == null || reagent == Items.AIR || base == null || base == Items.AIR)
            return false;
        GlyphPressRecipe recipe = ArsNouveauAPI.getInstance().getGlyphPressRecipe(world, reagent, getTier(base));
        if(recipe == null)
            return false;

        int manaCost = recipe.tier == ISpellTier.Tier.ONE ? 2000 : (recipe.tier == ISpellTier.Tier.TWO ? 4000 : 6000);
        AtomicBoolean valid = new AtomicBoolean(false);
        BlockPos.getAllInBox(this.getPos().add(5, -3, 5), this.getPos().add(-5, 3, -5)).forEach(blockPos -> {
            if(!valid.get() && world.getTileEntity(blockPos) instanceof ManaJarTile && ((ManaJarTile) world.getTileEntity(blockPos)).getCurrentMana() >= manaCost) {
                valid.set(true);
            }
        });
        return valid.get();
    }
}
