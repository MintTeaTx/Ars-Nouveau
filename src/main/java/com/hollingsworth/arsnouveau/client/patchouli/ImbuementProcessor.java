package com.hollingsworth.arsnouveau.client.patchouli;

import com.hollingsworth.arsnouveau.common.crafting.recipes.ImbuementRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import vazkii.patchouli.api.IComponentProcessor;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.api.IVariableProvider;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ImbuementProcessor implements IComponentProcessor {
    ImbuementRecipe recipe;

    @Override
    public void setup(IVariableProvider variables) {
        RecipeManager manager = Minecraft.getInstance().level.getRecipeManager();
        String recipeID = variables.get("recipe").asString();
        recipe = (ImbuementRecipe) manager.byKey(new ResourceLocation(recipeID)).orElse(null);
    }

    @Override
    public IVariable process(String key) {
        if (recipe == null)
            return null;
        if (key.equals("reagent"))
            return IVariable.wrapList(Arrays.stream(recipe.input.getItems()).map(IVariable::from).collect(Collectors.toList()));

        if (key.equals("recipe")) {
            return IVariable.wrap(recipe.getId().toString());
        }
        if (key.equals("output")) {
            return IVariable.from(recipe.output);
        }
        if (key.equals("footer")) {
            return IVariable.wrap(recipe.output.getItem().getDescriptionId());
        }

        return null;
    }
}
