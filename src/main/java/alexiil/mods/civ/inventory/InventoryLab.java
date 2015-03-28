package alexiil.mods.civ.inventory;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.item.ItemTechnology;
import alexiil.mods.civ.item.ItemTechnology.EResearchState;
import alexiil.mods.lib.nbt.NBTUtils;

public class InventoryLab extends InventoryPermanent implements ISidedInventory {
    public static final int INVENTORY_SIZE = CivItems.sciencePacks.length + 1;
    private static final int[] AVAILABLE_SLOTS = new int[INVENTORY_SIZE];

    static {
        for (int i = 0; i < INVENTORY_SIZE; i++)
            AVAILABLE_SLOTS[i] = i;
    }

    public InventoryLab() {
        super("Lab", false, INVENTORY_SIZE);
    }

    public boolean canResearch() {
        ItemStack research = super.getStackInSlot(0);
        if (research == null)
            return false;
        EResearchState state = CivItems.technology.getState(research);
        if (state == EResearchState.RESEARCHED)
            return false;

        for (int i = 1; i < INVENTORY_SIZE; i++) {
            ItemStack stack = super.getStackInSlot(i);
            if (stack == null)
                continue;
            int got = stack.stackSize;
            int needed = CivItems.technology.getSciencePacksRequired(research)[i - 1];
            if (needed > 0 && got > 0 && stack.getItem() == CivItems.sciencePacks[i - 1])
                return true;
        }
        return false;
    }

    /** @return <code>True</code> If any tech was researched */
    public boolean researchTech() {
        ItemStack research = super.getStackInSlot(0);
        for (int i = 1; i < INVENTORY_SIZE; i++) {
            ItemStack stack = super.getStackInSlot(i);
            if (stack != null) {
                int needed = CivItems.technology.getSciencePacksRequired(research)[i - 1];
                if (needed <= 0)
                    continue;
                super.decrStackSize(i, 1);
                int[] toAdd = new int[i];
                toAdd[i - 1] = 1;
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
        int[] requiredArr = CivItems.technology.getSciencePacksRequired(getStackInSlot(0));
        if (requiredArr.length <= index)
            return false;
        ItemStack alreadyHere = getStackInSlot(index + 1);
        int required = requiredArr[index];
        if (alreadyHere == null)
            return required >= stack.stackSize;
        required -= alreadyHere.stackSize;
        return required >= stack.stackSize;
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

    @Override
    public String toString() {
        return NBTUtils.toString(save()).replace("\t", "");
    }
}
