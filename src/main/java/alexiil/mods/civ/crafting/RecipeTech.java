package alexiil.mods.civ.crafting;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import scala.actors.threadpool.Arrays;
import alexiil.mods.civ.event.TechResearchedEvent.ItemTechResearchedEvent;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.item.ItemTechnology;
import alexiil.mods.civ.item.ItemTechnology.EResearchState;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.utils.TechUtils;

public class RecipeTech implements IRecipe {
    public static RecipeTech instance = new RecipeTech();

    private RecipeTech() {}

    @Override
    public boolean matches(InventoryCrafting craft, World world) {
        return getOutput(null, craft, false) != null;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craft) {
        return getOutput(null, craft, false);
    }

    public ItemStack getOutput(EntityPlayer player, InventoryCrafting craft, boolean edit) {
        if (craft == null)
            return null;

        int[] packs = new int[CivItems.sciencePacks.length];
        ItemStack t = null;
        ArrayList<ItemStack> techs = new ArrayList<ItemStack>();
        ArrayList<Integer> techPositions = new ArrayList<Integer>();
        for (int p = 0; p < craft.getSizeInventory(); p++) {
            ItemStack stack = craft.getStackInSlot(p);
            if (stack == null)
                continue;
            if ((stack.getItem() == CivItems.technology && (stack.getItemDamage() == 0 || stack.getItemDamage() == 1)))
                if (t == null)
                    t = stack;
                else
                    return null;
            else if ((stack.getItem() == CivItems.technology && stack.getItemDamage() == 2)) {
                techs.add(stack.copy());
                techPositions.add(p);
            }
            else {
                boolean added = false;
                for (int idx = 0; idx < packs.length; idx++)
                    if (stack.getItem() == CivItems.sciencePacks[idx]) {
                        packs[idx]++;
                        added = true;
                        break;
                    }
                if (!added)
                    return null;

            }
        }
        if (t == null)
            if (techs.size() == 0)
                return null;
            else {
                for (int idx = 0; idx < packs.length; idx++)
                    if (packs[idx] > 0)
                        return null;
                ArrayList<Tech> ts = new ArrayList<Tech>();
                for (ItemStack i : techs)
                    ts.add(((ItemTechnology) i.getItem()).getTech(i));
                Tech tech = TechTree.currentTree.getResult(ts, techPositions.get(0));
                if (tech == null)
                    return null;
                int[] newPacks = null;
                if (player != null)
                    if (TechUtils.hasTech(tech, player))
                        newPacks = tech.getSciencePacksNeeded();
                if (newPacks == null)
                    newPacks = new int[0];
                ItemStack output = CivItems.technology.getItemForTech(tech, newPacks);
                // if (edit) {
                // for (int i : techPositions)
                // craft.getStackInSlot(i).stackSize++;
                // }
                return output;
            }
        int[] required = CivItems.technology.getSciencePacksRequired(t);
        if (required.length < packs.length)
            required = Arrays.copyOf(required, packs.length);
        for (int idx = 0; idx < packs.length; idx++)
            if (required[idx] < packs[idx])
                return null;
        ItemStack nT = t.copy();
        CivItems.technology.addPackCount(nT, packs);
        if (edit && CivItems.technology.getState(nT) == EResearchState.RESEARCHED)
            MinecraftForge.EVENT_BUS.post(new ItemTechResearchedEvent(nT, player));
        // if (edit)
        // craft.decrStackSize(pos, 1);
        return nT;
    }

    @Override
    public int getRecipeSize() {
        return 2;
    }

    @Override
    public ItemStack getRecipeOutput() {
        return null;
    }

    /** This just returns whatever should be in the inventory afterwards */
    @Override
    public ItemStack[] getRemainingItems(InventoryCrafting craft) {
        ItemStack stack = getOutput(null, craft, false);
        if (stack == null || stack.getItem() == null || !(stack.getItem() instanceof ItemTechnology)) {
            // If it was a normal (or other) crafting recipe
            ItemStack[] stacks = new ItemStack[craft.getSizeInventory()];
            for (int index = 0; index < stacks.length; index++) {
                stacks[index] = ForgeHooks.getContainerItem(craft.getStackInSlot(index));
            }
            return stacks;
        }
        ItemTechnology item = (ItemTechnology) stack.getItem();
        int[] packs = item.getScienceCount(stack);
        boolean hasAny = false;
        for (int idx = 0; idx < packs.length; idx++)
            hasAny |= packs[idx] > 0;
        if (hasAny) {
            // If it was (tech + beaker(s) -> tech)
            return new ItemStack[craft.getSizeInventory()];
        }
        // If it was (child tech -> parent tech)
        ItemStack[] aitemstack = new ItemStack[craft.getSizeInventory()];
        for (int i = 0; i < aitemstack.length; ++i) {
            ItemStack itemstack = craft.getStackInSlot(i);
            aitemstack[i] = ForgeHooks.getContainerItem(itemstack);
        }
        return aitemstack;
    }
}
