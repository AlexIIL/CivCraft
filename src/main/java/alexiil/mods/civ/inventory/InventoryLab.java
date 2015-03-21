package alexiil.mods.civ.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.item.ItemTechnology;
import alexiil.mods.civ.item.ItemTechnology.EResearchState;

public class InventoryLab extends InventoryPermanent implements ISidedInventory {
    private static final int INVENTORY_SIZE = CivItems.sciencePacks.length + 1;
    private static final int[] AVAILABLE_SLOTS = new int[INVENTORY_SIZE];

    static {
        for (int i = 0; i < INVENTORY_SIZE; i++)
            AVAILABLE_SLOTS[i] = i;
    }

    public final int[] requiredCache = new int[INVENTORY_SIZE - 1];

    public InventoryLab() {
        super("Lab", false, INVENTORY_SIZE);
    }

    @Override
    public void onInventoryChanged(InventoryBasic invBasic) {
        refreshCache();
    }

    private void refreshCache() {
        ItemStack tech = super.getStackInSlot(0);
        int[] required = new int[CivItems.sciencePacks.length];
        if (tech != null)
            required = CivItems.technology.getSciencePacksRequired(tech);
        for (int i = 0; i < requiredCache.length; i++) {
            requiredCache[i] = i < required.length ? required[i] : 0;
            ItemStack stack = getStackInSlot(i + 1);
            requiredCache[i] -= stack == null ? 0 : stack.stackSize;
        }
    }

    public boolean canResearch() {
        for (int i = 0; i < requiredCache.length; i++) {
            ItemStack stack = super.getStackInSlot(i + 1);
            if (stack == null)
                continue;
            int got = stack.stackSize;
            if (got > 0)
                return true;
        }
        return false;
    }

    /** @return <code>True</code> If any tech was researched */
    public boolean researchTech() {
        for (int i = 0; i < requiredCache.length; i++) {
            ItemStack stack = super.getStackInSlot(i + 1);
            if (stack != null) {
                super.decrStackSize(i + 1, 1);
                int[] toAdd = new int[i + 1];
                toAdd[i] = 1;
                CivItems.technology.addPackCount(super.getStackInSlot(0), toAdd);
                return true;
            }
        }
        // if (hasChanged)
        // refreshCache();
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        if (index < 0)// Just in case...
            return false;
        if (index == 0)
            return (stack.getItem() instanceof ItemTechnology) && CivItems.technology.getState(stack) != EResearchState.RESEARCHED
                    && getStackInSlot(0) == null;
        index--;
        if (index >= CivItems.sciencePacks.length)
            return false;
        if (CivItems.sciencePacks[index] != stack.getItem())
            return false;
        return requiredCache[index] >= stack.stackSize;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return AVAILABLE_SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return isItemValidForSlot(index, stack);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (index != 0)
            return false;
        return (stack.getItem() instanceof ItemTechnology) && CivItems.technology.getState(stack) == EResearchState.RESEARCHED;
    }
}
