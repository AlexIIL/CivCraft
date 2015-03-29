package alexiil.mods.civ.tech.unlock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.tech.Unlockable;
import alexiil.mods.civ.utils.TechUtils;
import alexiil.mods.lib.EChatColours;
import alexiil.mods.lib.LangUtils;
import alexiil.mods.lib.item.IChangingItemString;

public class ItemCraftUnlock extends TechUnlockable implements IChangingItemString, IItemBlocker {
    private static class ItemStackComparator implements IItemComparator {
        private final ItemStack stack;

        public ItemStackComparator load(NBTTagCompound nbt) {
            ItemStack stack = ItemStack.loadItemStackFromNBT(nbt);
            if (stack == null) {
                CivLog.warn("The item stack was null! (for NBT " + nbt.toString() + ")");
                return null;
            }
            return new ItemStackComparator(stack);
        }

        public ItemStackComparator(ItemStack stack) {
            this.stack = stack;
        }

        public NBTTagCompound save() {
            return stack.writeToNBT(new NBTTagCompound());
        }

        @Override
        public boolean isConsideredEqual(ItemStack toCompare) {
            return OreDictionary.itemMatches(stack, toCompare, false);
        }

        @Override
        public boolean canSaveAndLoad() {
            return true;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public List<String> getDescription(Unlockable unlockable) {
            EChatColours colour = unlockable.isUnlocked(Minecraft.getMinecraft().thePlayer) ? EChatColours.GOLD : EChatColours.BLUE;
            return Collections.singletonList(colour + " -" + LangUtils.format("civcraft.unlock.itemcraft.pre") + stack.getDisplayName());
        }
    }

    private static final ItemStackComparator basicComparer = new ItemStackComparator(null);

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
        NBTTagCompound itemsNBT = nbt.getCompoundTag("items");
        for (Object key : itemsNBT.getKeySet()) {
            String skey = (String) key;
            NBTTagCompound item = itemsNBT.getCompoundTag(skey);
            items.add(basicComparer.load(item));
        }
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
        items.add(new ItemStackComparator(new ItemStack(item, 1, OreDictionary.WILDCARD_VALUE)));
        return this;
    }

    public ItemCraftUnlock addUnlocked(Block... blocks) {
        for (Block block : blocks)
            addUnlocked(Item.getItemFromBlock(block));
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
        items.add(new ItemStackComparator(stack));
        return this;
    }

    public ItemCraftUnlock addUnlocked(IItemComparator compare) {
        singleItemFlag = false;
        singleItem = null;
        items.add(compare);
        isLoadable &= compare.canSaveAndLoad();
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
            return LangUtils.format("civcraft.unlock.itemcraft.pre") + " " + LangUtils.format(singleItem.getUnlocalizedName() + ".name");
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
        if (!isLoadable)
            return;
        NBTTagCompound items = new NBTTagCompound();
        int index = 0;
        for (IItemComparator c : this.items) {
            if (c == null)
                CivLog.warn("The item comparator was null! (" + getName() + ")");
            else
                items.setTag(Integer.toString(index), c.save());
            index++;
        }
        nbt.setTag("items", items);
    }

    @Override
    public String getType() {
        return Lib.Mod.ID + ":ItemCraftUnlock";
    }

    @Override
    public List<String> getDescription() {
        List<String> list = new ArrayList<String>();
        for (IItemComparator c : items) {
            list.addAll(c.getDescription(this));
        }
        return list;
    }
}
