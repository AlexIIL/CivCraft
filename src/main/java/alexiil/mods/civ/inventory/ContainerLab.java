package alexiil.mods.civ.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.item.ItemTechnology.EResearchState;
import alexiil.mods.civ.tile.TileLab;

public class ContainerLab extends Container {
    private class SlotLab extends Slot {
        public SlotLab(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }

        @Override
        public boolean isItemValid(ItemStack stack) {
            if (stack == null)
                return false;
            if (slotNumber == 0)
                return stack.getItem() == CivItems.technology && CivItems.technology.getState(stack) != EResearchState.RESEARCHED;
            return stack.getItem() == CivItems.sciencePacks[this.slotNumber - 1];
        }
    }

    public final TileLab lab;

    public ContainerLab(TileLab lab, EntityPlayer player) {
        this.lab = lab;

        int xPos = 0;
        for (int x = 0; x < InventoryLab.INVENTORY_SIZE; x++) {
            if (x == 1)
                xPos += 18;
            addSlotToContainer(new SlotLab(lab, x, xPos, 10));
            xPos += 18;
        }

        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 9; x++)
                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18, 153 + y * 18));

        for (int x = 0; x < 9; x++)
            addSlotToContainer(new Slot(player.inventory, x, 8 + x * 18, 211));
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return null;
    }
}
