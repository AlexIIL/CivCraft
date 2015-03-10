package alexiil.mods.civ.tech.unlock;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.utils.TechUtils;
import alexiil.mods.lib.EChatColours;
import alexiil.mods.lib.item.IChangingItemString;

public class ItemCraftUnlock extends TechUnlockable implements IChangingItemString, IItemBlocker {
    private final List<IItemComparator> items;
    private ItemStack singleItem = null;
    private boolean singleItemFlag = false;
    
    public ItemCraftUnlock(String name, Tech... techs) {
        super(name, techs, true);
        items = new ArrayList<IItemComparator>();
    }
    
    public ItemCraftUnlock(NBTTagCompound nbt) {
        super(nbt);
        items = new ArrayList<IItemComparator>();
    }
    
    /** Adds all variants of these items as now craftable */
    public ItemCraftUnlock addUnlocked(Item... items) {
        for (final Item item : items)
            addUnlocked(item);
        return this;
    }
    
    /** Adds all variants of this item as now craftable */
    public ItemCraftUnlock addUnlocked(final Item item) {
        if (item == null)
            return this;
        if (items.size() == 0 || singleItemFlag) {
            if (singleItem != null) {
                singleItem = null;
                singleItemFlag = false;
            }
            else {
                singleItem = new ItemStack(item);
                singleItemFlag = true;
            }
        }
        items.add(new IItemComparator() {
            @Override
            public boolean isConsideredEqual(ItemStack toCompare) {
                if (toCompare == null && item != null)
                    return false;
                return toCompare.getItem() == item;
            }
        });
        return this;
    }
    
    public ItemCraftUnlock addUnlocked(Block... blocks) {
        for (Block block : blocks)
            addUnlocked(ItemBlock.getItemFromBlock(block));
        return this;
    }
    
    public ItemCraftUnlock addUnlocked(ItemStack... stacks) {
        for (final ItemStack stack : stacks)
            addUnlocked(stack);
        return this;
    }
    
    public ItemCraftUnlock addUnlocked(final ItemStack stack) {
        if (stack == null)
            return this;
        if (singleItemFlag) {
            if (singleItem != null) {
                singleItem = null;
                singleItemFlag = false;
            }
            else
                singleItem = stack;
        }
        items.add(new IItemComparator() {
            @Override
            public boolean isConsideredEqual(ItemStack toCompare) {
                return OreDictionary.itemMatches(stack, toCompare, false);
            }
        });
        return this;
    }
    
    public ItemCraftUnlock addUnlocked(IItemComparator compare) {
        singleItemFlag = false;
        singleItem = null;
        items.add(compare);
        return this;
    }
    
    public boolean itemMatches(ItemStack stack) {
        for (IItemComparator i : items)
            if (i.isConsideredEqual(stack))
                return true;
        return false;
    }
    
    @Override
    public void unlock(EntityPlayer player) {}
    
    @Override
    public String getUnlocalisedName() {
        return "civcraft.unlock.itemcraft." + getName();
    }
    
    @Override
    public String getLocalisedName() {
        String superL = super.getLocalisedName();
        if (superL.equals(getUnlocalisedName()) && singleItemFlag)
            return CivCraft.instance.format("civcraft.unlock.itemcraft.pre") + " "
                    + CivCraft.instance.format(singleItem.getUnlocalizedName() + ".name");
        return superL;
    }
    
    @Override
    public String[] getString(ItemStack i, EntityPlayer player) {
        if (itemMatches(i)) {
            String[] strings = new String[1 + requiredTechs().length];
            strings[0] = EChatColours.GRAY + "Requires these techs to make:";
            int idx = 1;
            for (Tech t : requiredTechs()) {
                strings[idx] = TechUtils.getColour(t) + " -" + t.getLocalizedName();
                idx++;
            }
            return strings;
        }
        return new String[0];
    }
    
    @Override
    public boolean doesBlockItem(ItemStack item) {
        return itemMatches(item);
    }
    
    @Override
    public void save(NBTTagCompound nbt) {
        super.save(nbt);
    }
    
    @Override
    public String getType() {
        return Lib.Mod.ID + ":ItemCraftUnlock";
    }
}
