package alexiil.mods.civ.compat.nei;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import scala.actors.threadpool.Arrays;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.item.ItemTechnology.EResearchState;
import alexiil.mods.civ.tech.TechTree.Tech;
import codechicken.nei.recipe.ShapelessRecipeHandler;

public class TechCraftingHandler extends ShapelessRecipeHandler {
    @Override
    public String getGuiTexture() {
        return "textures/gui/container/crafting_table.png";
    }
    
    @Override
    public String getOverlayIdentifier() {
        return "crafting";
    }
    
    /* @Override public void loadCraftingRecipes(String outputId, Object... results) { if ("crafting".equals(outputId))
     * { // Technology Creation + Researching for (Tech t0 : TechTree.currentTree.getTechs().values()) { Tech[] parents
     * = t0.getParentTechs(); ItemStack[] parentItems = new ItemStack[parents.length]; for (int i = 0; i <
     * parents.length; i++) parentItems[i] = CivCraft.technology.getItemForTech(parents[i],
     * parents[i].getBeakersNeeded()); arecipes.add(new CachedShapelessRecipe(parentItems,
     * CivCraft.technology.getItemForTech(t0, 0))); } // Technology Duplication } else {
     * super.loadCraftingRecipes(outputId, results); } } */
    
    @Override
    public void loadCraftingRecipes(ItemStack result) {
        if (result == null || result.getItem() != CivItems.technology)
            return;
        // Technology Creation + Researching
        Tech t0 = CivItems.technology.getTech(result);
        EResearchState state = CivItems.technology.getState(result); // Made from other techs
        Tech[] parents = t0.getParentTechs();
        ItemStack[] parentItems = new ItemStack[parents.length];
        for (int i = 0; i < parents.length; i++)
            parentItems[i] = CivItems.technology.getItemForTech(parents[i], parents[i].getSciencePacksNeeded());
        if (parentItems.length != 0)
            arecipes.add(new CachedShapelessRecipe(parentItems, CivItems.technology.getItemForTech(t0, new int[0])));
        
        if (state == EResearchState.RESEARCHING || state == EResearchState.RESEARCHED) {
            int[] packsGot = CivItems.technology.getScienceCount(result);
            for (int idx = 0; idx < packsGot.length; idx++) {
                int beakersGot = packsGot[idx];
                int max = Math.min(beakersGot, 8);
                for (int i = max; i > 0; i--) {
                    int[] arr = Arrays.copyOf(packsGot, packsGot.length);
                    arr[idx] -= i;
                    List<ItemStack> stacks = new ArrayList<ItemStack>();
                    stacks.add(CivItems.technology.getItemForTech(t0, arr));
                    for (int b = 0; b < i; b++)
                        stacks.add(new ItemStack(CivItems.sciencePacks[idx]));
                    arecipes.add(new CachedShapelessRecipe(stacks, result));
                }
            }
        }
    }
    
    @Override
    public void loadUsageRecipes(ItemStack in) {
        if (in == null)
            return;
        if (in.getItem() == CivItems.technology) {
            Tech t = CivItems.technology.getTech(in); // Used to make other technology's
            for (Tech t1 : t.getChildTechs()) {
                ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
                for (Tech t2 : t1.getParentTechs())
                    stacks.add(CivItems.technology.getItemForTech(t2, t2.getSciencePacksNeeded()));
                arecipes.add(new CachedShapelessRecipe(stacks, CivItems.technology.getItemForTech(t1, new int[0])));
            } // Used to research the technology with beakers
            EResearchState state = CivItems.technology.getState(in);
            if (state != EResearchState.RESEARCHED) {
                // TODO: and this one too (but do the above first!)
                int[] packsNeeded = CivItems.technology.getSciencePacksRequired(in);
                int[] packsGot = CivItems.technology.getScienceCount(in);
                if (packsNeeded.length < packsGot.length)
                    packsNeeded = Arrays.copyOf(packsNeeded, packsGot.length);
                if (packsGot.length < packsNeeded.length)
                    packsGot = Arrays.copyOf(packsGot, packsNeeded.length);
                for (int idx = 0; idx < packsNeeded.length; idx++) {
                    int max = Math.min(packsNeeded[idx], 8);
                    for (int i = max; i > 0; i--) {
                        int[] arr = Arrays.copyOf(packsGot, packsGot.length);
                        arr[idx] += i;
                        List<ItemStack> stacks = new ArrayList<ItemStack>();
                        stacks.add(in);
                        for (int b = 0; b < i; b++)
                            stacks.add(new ItemStack(CivItems.sciencePacks[idx]));
                        arecipes.add(new CachedShapelessRecipe(stacks, CivItems.technology.getItemForTech(t, arr)));
                    }
                }
            }
        }
        else if (in.getItem() == CivItems.sciencePacks[0]) {
            // Wow, thats a lot of recipes, and thats not even with a complete vanilla tech tree
        }
    }
}
