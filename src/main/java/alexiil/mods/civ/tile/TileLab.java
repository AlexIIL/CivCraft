package alexiil.mods.civ.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.block.BlockLab;
import alexiil.mods.civ.inventory.InventoryLab;
import alexiil.mods.civ.net.MessageLab;
import alexiil.mods.lib.tile.TileEntityUpdated;

public class TileLab extends TileEntityUpdated<MessageLab> implements ISidedInventory {
    private final InventoryLab inventory = new InventoryLab();
    private int cooldown = 0;

    public TileLab() {
        super(CivCraft.instance);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        inventory.load(nbt.getCompoundTag("inventory"));
        cooldown = nbt.getInteger("cooldown");
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("inventory", inventory.save());
        nbt.setInteger("cooldown", cooldown);
    }

    @Override
    public void onTick() {
        super.onTick();
        cooldown--;
        if (cooldown <= 0)
            cooldown = 0;
        if (isReady()) {
            usePower();
            worldObj.setBlockState(getPos(), worldObj.getBlockState(getPos()).withProperty(BlockLab.RESEARCHING, true), 3);
            if (inventory.researchTech()) {
                cooldown = 80;
            }
        }
        else if (cooldown <= 0) {
            worldObj.setBlockState(getPos(), worldObj.getBlockState(getPos()).withProperty(BlockLab.RESEARCHING, false), 3);
        }
    }

    @Override
    public String toString() {
        return "TileLab [inventory=" + inventory + ", cooldown=" + cooldown + "]";
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
        return (oldState.getBlock() != newState.getBlock());
    }

    private boolean isReady() {
        return cooldown <= 0 && hasPower() && inventory.canResearch();
    }

    /* Power requirements- not used so far, but here for easy implementation later (say, whenever a mod that provides
     * power properly exists) */

    private boolean hasPower() {
        return true;
    }

    private void usePower() {

    }

    // TileEntityUpdated

    @Override
    public MessageLab getCustomUpdateMessage() {
        return new MessageLab(this);
    }

    @Override
    public void setTileData(MessageLab message) {}

    @Override
    public BlockPos getCor() {
        return pos;
    }

    @Override
    public boolean needsNetworkUpdate() {
        return false;
    }

    @Override
    public void dropItems() {
        inventory.dropItems(this);
    }

    // ISidedInventory delegate methods

    @Override
    public ItemStack getStackInSlot(int index) {
        return inventory.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return inventory.decrStackSize(index, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        return inventory.getStackInSlotOnClosing(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        inventory.setInventorySlotContents(index, stack);
    }

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }
    
    @Override
    public String getCommandSenderName() {
        return inventory.getCommandSenderName();
    }

    @Override
    public boolean hasCustomName() {
        return inventory.hasCustomName();
    }

    @Override
    public IChatComponent getDisplayName() {
        return inventory.getDisplayName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public void markDirty() {
        inventory.markDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        inventory.openInventory(player);
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        inventory.closeInventory(player);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return inventory.isItemValidForSlot(index, stack);
    }

    @Override
    public int getField(int id) {
        return inventory.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventory.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventory.getFieldCount();
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return inventory.getSlotsForFace(side);
    }

    @Override
    public boolean canInsertItem(int index, ItemStack stack, EnumFacing direction) {
        return inventory.canInsertItem(index, stack, direction);
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return inventory.canExtractItem(index, stack, direction);
    }
}
