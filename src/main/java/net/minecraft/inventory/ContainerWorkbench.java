package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import alexiil.forgechanges.MinecraftForgeNewHooks;

public class ContainerWorkbench extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    private World worldObj;
    private BlockPos field_178145_h;
    private static final String __OBFID = "CL_00001744";
    
    public ContainerWorkbench(InventoryPlayer playerInventory, World worldIn, BlockPos p_i45800_3_) {
        this.worldObj = worldIn;
        this.field_178145_h = p_i45800_3_;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 124, 35));
        int i;
        int j;
        
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 3; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }
        
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        
        for (i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
        
        this.onCraftMatrixChanged(this.craftMatrix);
    }
    
    // ///////////////////////////////////////////////
    //
    // CHANGE HERE!
    //
    // ///////////////////////////////////////////////
    
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        // REMOVED:
        // this.craftResult.setInventorySlotContents(0,
        // CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.worldObj));
        // ADDED:
        // this.craftResult.setInventorySlotContents(0, CraftUtils.findMatchingRecipe(craftMatrix, worldObj,
        // field_178145_h));
        
        // ADDED TYPE 2:
        ItemStack stack = CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, worldObj);
        if (MinecraftForgeNewHooks.canCraftBlockEvent(craftMatrix, stack, worldObj, field_178145_h))
            craftResult.setInventorySlotContents(0, stack);
        else
            craftResult.setInventorySlotContents(0, null);
        // TODO: find a better way of doing this, OR add an event to forge
        // TODO 2: either write this as a core-mod, or remove this IF forge accepts my pull request
    }
    
    // ///////////////////////////////////////////////
    //
    // END OF CHANGE
    //
    // ///////////////////////////////////////////////
    
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        
        if (!this.worldObj.isRemote) {
            for (int i = 0; i < 9; ++i) {
                ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);
                
                if (itemstack != null) {
                    playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }
    
    public boolean canInteractWith(EntityPlayer playerIn) {
        return this.worldObj.getBlockState(this.field_178145_h).getBlock() != Blocks.crafting_table ? false
                : playerIn.getDistanceSq((double) this.field_178145_h.getX() + 0.5D, (double) this.field_178145_h.getY() + 0.5D,
                        (double) this.field_178145_h.getZ() + 0.5D) <= 64.0D;
    }
    
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);
        
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 10, 46, true)) {
                    return null;
                }
                
                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 10 && index < 37) {
                if (!this.mergeItemStack(itemstack1, 37, 46, false)) {
                    return null;
                }
            }
            else if (index >= 37 && index < 46) {
                if (!this.mergeItemStack(itemstack1, 10, 37, false)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 10, 46, false)) {
                return null;
            }
            
            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
            }
            else {
                slot.onSlotChanged();
            }
            
            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }
            
            // STACK OVERFLOW EXCEPTION HERE WHEN SHIFT-CLICKING!
            slot.onPickupFromSlot(playerIn, itemstack1);
        }
        
        return itemstack;
    }
    
    public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
        return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
    }
}