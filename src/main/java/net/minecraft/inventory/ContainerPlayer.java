package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alexiil.forgechanges.MinecraftForgeNewHooks;

public class ContainerPlayer extends Container {
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();
    public boolean isLocalWorld;
    private final EntityPlayer thePlayer;
    private static final String __OBFID = "CL_00001754";
    
    public ContainerPlayer(final InventoryPlayer playerInventory, boolean localWorld, EntityPlayer player) {
        this.isLocalWorld = localWorld;
        this.thePlayer = player;
        this.addSlotToContainer(new SlotCrafting(playerInventory.player, this.craftMatrix, this.craftResult, 0, 144, 36));
        int i;
        int j;
        
        for (i = 0; i < 2; ++i) {
            for (j = 0; j < 2; ++j) {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 88 + j * 18, 26 + i * 18));
            }
        }
        
        for (i = 0; i < 4; ++i) {
            final int k = i;
            this.addSlotToContainer(new Slot(playerInventory, playerInventory.getSizeInventory() - 1 - i, 8, 8 + i * 18) {
                private static final String __OBFID = "CL_00001755";
                
                public int getSlotStackLimit() {
                    return 1;
                }
                
                public boolean isItemValid(ItemStack stack) {
                    if (stack == null) return false;
                    return stack.getItem().isValidArmor(stack, k, thePlayer);
                }
                
                @SideOnly(Side.CLIENT) public String getSlotTexture() {
                    return ItemArmor.EMPTY_SLOT_NAMES[k];
                }
            });
        }
        
        for (i = 0; i < 3; ++i) {
            for (j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(playerInventory, j + (i + 1) * 9, 8 + j * 18, 84 + i * 18));
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
        // CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
        
        // ADDED:
        // this.craftResult.setInventorySlotContents(0, CraftUtils.findMatchingRecipe(craftMatrix, thePlayer));
        
        // ADDED TYPE 2:
        ItemStack stack = CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj);
        if (MinecraftForgeNewHooks.canCraftPlayerEvent(craftMatrix, stack, thePlayer.worldObj, thePlayer))
            craftResult.setInventorySlotContents(0, stack);
        else craftResult.setInventorySlotContents(0, null);
        // TODO: find a better way of doing this, OR add an event to forge
    }
    
    // ///////////////////////////////////////////////
    //
    // END OF CHANGE
    //
    // ///////////////////////////////////////////////
    
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        
        for (int i = 0; i < 4; ++i) {
            ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);
            
            if (itemstack != null) {
                playerIn.dropPlayerItemWithRandomChoice(itemstack, false);
            }
        }
        
        this.craftResult.setInventorySlotContents(0, (ItemStack) null);
    }
    
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
    
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot) this.inventorySlots.get(index);
        
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            
            if (index == 0) {
                if (!this.mergeItemStack(itemstack1, 9, 45, true)) { return null; }
                
                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index >= 1 && index < 5) {
                if (!this.mergeItemStack(itemstack1, 9, 45, false)) { return null; }
            }
            else if (index >= 5 && index < 9) {
                if (!this.mergeItemStack(itemstack1, 9, 45, false)) { return null; }
            }
            else if (itemstack.getItem() instanceof ItemArmor
                    && !((Slot) this.inventorySlots.get(5 + ((ItemArmor) itemstack.getItem()).armorType)).getHasStack()) {
                int j = 5 + ((ItemArmor) itemstack.getItem()).armorType;
                
                if (!this.mergeItemStack(itemstack1, j, j + 1, false)) { return null; }
            }
            else if (index >= 9 && index < 36) {
                if (!this.mergeItemStack(itemstack1, 36, 45, false)) { return null; }
            }
            else if (index >= 36 && index < 45) {
                if (!this.mergeItemStack(itemstack1, 9, 36, false)) { return null; }
            }
            else if (!this.mergeItemStack(itemstack1, 9, 45, false)) { return null; }
            
            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack) null);
            }
            else {
                slot.onSlotChanged();
            }
            
            if (itemstack1.stackSize == itemstack.stackSize) { return null; }
            
            slot.onPickupFromSlot(playerIn, itemstack1);
        }
        
        return itemstack;
    }
    
    public boolean canMergeSlot(ItemStack p_94530_1_, Slot p_94530_2_) {
        return p_94530_2_.inventory != this.craftResult && super.canMergeSlot(p_94530_1_, p_94530_2_);
    }
}