package alexiil.mods.civ.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.item.ItemTechnology.EResearchState;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.utils.TechUtils;
import alexiil.mods.lib.EChatColours;
import alexiil.mods.lib.item.IChangingItemString;
import alexiil.mods.lib.item.ItemBase;

public class ItemTechBag extends ItemBase {
    public static class TechProgress {
        @Override public String toString() {
            return "TechProgress [tech=" + tech + ", progress=" + Arrays.toString(progress) + ", state=" + state + "]";
        }
        
        public final Tech tech;
        public final int[] progress;
        public final EResearchState state;
        
        public TechProgress(Tech t, int[] pro) {
            tech = t;
            progress = pro;
            state = EResearchState.values()[CivItems.technology.getItemDamageForScience(t, pro)];
        }
        
        public TechProgress add(int[] toAdd) {
            TechProgress p = this;
            for (int index = 0; index < toAdd.length; index++)
                p = p.add(index, toAdd[index]);
            return p;
        }
        
        public TechProgress add(int index, int toAdd) {
            int[] nProgress = new int[Math.max(progress.length, index + 1)];
            for (int i = 0; i < nProgress.length; i++) {
                if (i < progress.length)
                    nProgress[i] = progress[i];
                else
                    nProgress[i] = 0;
                if (i == index)
                    nProgress[i] += toAdd;
            }
            return new TechProgress(tech, nProgress);
        }
    }
    
    public ItemTechBag(String name) {
        super(name, CivCraft.instance);
        addShiftInfo(new IChangingItemString() {
            @Override public String[] getString(ItemStack i, EntityPlayer player) {
                TechProgress[] techs = getTechs(i);
                Arrays.sort(techs, new Comparator<TechProgress>() {
                    @Override public int compare(TechProgress o1, TechProgress o2) {
                        if (o1.state != o2.state)
                            return o1.state.compareTo(o2.state);
                        return o1.tech.name.compareTo(o2.tech.name);
                    }
                });
                String[] strings = new String[techs.length + 1];
                strings[0] = techs.length == 0 ? "Contains no techs" : "Contains the following techs:";
                for (int idx = 0; idx < techs.length; idx++) {
                    TechProgress prog = techs[idx];
                    String progress;
                    switch (prog.state) {
                        case PENDING: {
                            progress = EChatColours.BLUE + " is pending";
                            break;
                        }
                        case RESEARCHING: {
                            int[] got = prog.progress;
                            int[] needed = prog.tech.getSciencePacksNeeded();
                            if (got.length < needed.length)
                                got = Arrays.copyOf(got, needed.length);
                            progress = EChatColours.BLUE.toString();
                            for (int index = 0; index < needed.length; index++)
                                progress += " " + got[index] + "/" + needed[index];
                            break;
                        }
                        case RESEARCHED: {
                            progress = EChatColours.BLUE + " is done";
                            break;
                        }
                        default: {
                            progress = EChatColours.BLUE + " is at an invalid state. Please report this! (prog.state = " + prog.state + ")";
                            break;
                        }
                    }
                    strings[idx + 1] = prog.state.colour + " -" + prog.tech.getLocalizedName() + progress;
                }
                return strings;
            }
        });
    }
    
    public ItemStack[] getItems(ItemStack item) {
        if (!item.hasTagCompound())
            item.setTagCompound(new NBTTagCompound());
        NBTTagList items = item.getTagCompound().getTagList("item", 10);// Compound Tag
        ItemStack[] stack = new ItemStack[items.tagCount()];
        for (int i = 0; i < items.tagCount(); i++)
            stack[i] = ItemStack.loadItemStackFromNBT(items.getCompoundTagAt(i));
        return stack;
    }
    
    public void setItems(ItemStack item, ItemStack[] items) {
        NBTTagList nbtItems = new NBTTagList();
        for (ItemStack i : items)
            nbtItems.appendTag(i.writeToNBT(new NBTTagCompound()));
        if (!item.hasTagCompound())
            item.setTagCompound(new NBTTagCompound());
        item.getTagCompound().setTag("item", nbtItems);
    }
    
    public void setTechs(ItemStack item, TechProgress[] progress) {
        ItemStack[] stacks = new ItemStack[progress.length];
        for (int i = 0; i < stacks.length; i++)
            stacks[i] = CivItems.technology.getItemForTech(progress[i]);
        setItems(item, stacks);
        updateTechs(item);
    }
    
    public TechProgress[] getTechs(ItemStack item) {
        ItemStack[] items = getItems(item);
        List<ItemStack> techs = new ArrayList<ItemStack>();
        for (ItemStack i : items) {
            if (i == null || i.getItem() == null)
                continue;
            if (i.getItem() == CivItems.technology)
                techs.add(i);
        }
        List<TechProgress> techProg = new ArrayList<TechProgress>();
        for (ItemStack s : techs) {
            Tech t = CivItems.technology.getTech(s);
            if (t == null)
                continue;
            techProg.add(new TechProgress(t, CivItems.technology.getScienceCount(s)));
        }
        return techProg.toArray(new TechProgress[0]);
    }
    
    private void updateTechs(ItemStack stack) {
        ItemStack[] techs = getItems(stack);
        TechProgress[] prog = getTechs(stack);
        List<TechProgress> newProg = new ArrayList<TechProgress>();
        for (Tech t : TechTree.currentTree.getTechs().values()) {
            boolean alreadyExists = false;
            for (TechProgress t1 : prog)
                if (t1.tech == t)
                    alreadyExists = true;
            if (alreadyExists)
                continue;
            boolean hasAllParents = true;
            Tech[] parents = t.getParentTechs();
            for (Tech parent : parents) {
                boolean found = false;
                for (TechProgress t1 : prog)
                    if (parent == t1.tech && t1.state == EResearchState.RESEARCHED)
                        found = true;
                if (!found) {
                    hasAllParents = false;
                    break;
                }
            }
            if (!hasAllParents)
                continue;
            newProg.add(new TechProgress(t, new int[0]));
        }
        int index = techs.length;
        techs = Arrays.copyOf(techs, techs.length + newProg.size());
        for (TechProgress tp : newProg) {
            techs[index] = CivItems.technology.getItemForTech(tp.tech, tp.progress);
            index++;
        }
        setItems(stack, techs);
    }
    
    private void updateTechs(ItemStack stack, EntityPlayer player) {
        TechProgress[] progresses = this.getTechs(stack);
        for (Tech t : TechUtils.getTechs(player)) {
            boolean has = false;
            int index = -1;
            int i = 0;
            for (TechProgress tp : progresses) {
                if (tp.tech == t) {
                    if (tp.state == EResearchState.RESEARCHED)
                        has = true;
                    else {
                        index = i;
                        break;
                    }
                }
                i++;
            }
            if (has)
                continue;
            if (index != -1)
                progresses[index] = new TechProgress(t, t.getSciencePacksNeeded());
        }
        setTechs(stack, progresses);
    }
    
    @Override public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (world.isRemote) {
            if (!player.isSneaking())
                player.openGui(CivCraft.instance, Lib.Gui.TECH_TREE, world, 0, 0, 0);
            return itemStack;
        }
        if (player.isSneaking()) {// Sneak right click to empty
            ItemStack[] items = getItems(itemStack);
            setItems(itemStack, new ItemStack[0]);
            for (ItemStack item : items)
                world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, item));
            return itemStack;
        }// Right click to fill
        List<ItemStack> items = new ArrayList<ItemStack>();
        for (ItemStack i : getItems(itemStack))
            items.add(i);
        int index = 0;
        for (ItemStack i : player.inventory.mainInventory) {
            if (i != null && i.getItem() == CivItems.technology) {
                items.add(i);
                player.inventory.setInventorySlotContents(index, null);
            }
            index++;
        }
        ItemStack[] ims = items.toArray(new ItemStack[items.size()]);
        setItems(itemStack, ims);
        updateTechs(itemStack, player);
        updateTechs(itemStack);
        player.inventoryContainer.detectAndSendChanges();
        return itemStack;
    }
}
