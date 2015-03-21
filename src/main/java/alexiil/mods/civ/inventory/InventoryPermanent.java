package alexiil.mods.civ.inventory;

import net.minecraft.inventory.IInvBasic;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class InventoryPermanent extends InventoryBasic implements IInvBasic {
    public InventoryPermanent(String title, boolean customName, int slotCount) {
        super(title, customName, slotCount);
        // Adds a listener to the inventory, allowing for us to get updates whenever something changes
        super.func_110134_a(this);
    }

    public NBTTagCompound save() {
        return save(new NBTTagCompound());
    }

    public NBTTagCompound save(NBTTagCompound nbt) {
        for (int idx = 0; idx < super.getSizeInventory(); idx++) {
            ItemStack stack = super.getStackInSlot(idx);
            if (stack != null)
                // ToHexString. Because, why not :P
                nbt.setTag(Integer.toHexString(idx), stack.writeToNBT(new NBTTagCompound()));
        }
        return nbt;
    }

    public void load(NBTTagCompound nbt) {
        for (int idx = 0; idx < super.getSizeInventory(); idx++) {
            String hex = Integer.toHexString(idx);
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag(hex));
            super.setInventorySlotContents(idx, stack);
        }
    }

    @Override
    public void onInventoryChanged(InventoryBasic invBasic) {}
}
