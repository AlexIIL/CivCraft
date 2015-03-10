package alexiil.mods.civ.compat;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.tech.TechTreeEvent.AddTechs;
import alexiil.mods.civ.tech.TechTreeEvent.AddUnlockables;
import alexiil.mods.civ.tech.Unlockable;
import alexiil.mods.lib.nbt.NBTUtils;

public class ConfigCompat extends ModCompat {
    @Override
    public void addTechs(AddTechs t) {
        TechTree tree = t.tree;
        NBTTagCompound n = t.treeNBTCompound;
        NBTTagCompound techs = n.getCompoundTag("techs");
        
        for (Object key : techs.getKeySet()) {
            NBTTagCompound tech = techs.getCompoundTag((String) key);
            // Almost directly copied from new TechTree.Tech()
            Tech techAdded = tree.addTech((String) key);
            NBTTagList nbtparents = tech.getTagList("parents", Lib.NBT.STRING);
            for (int idx = 0; idx < nbtparents.tagCount(); idx++) {
                String parentName = nbtparents.getStringTagAt(idx);
                Tech parent = tree.getTech(parentName);
                techAdded.addRequirement(parent);
            }
            NBTTagList sciencePacks = tech.getTagList("sciencePacks", Lib.NBT.INTEGER);
            int[] required = new int[sciencePacks.tagCount()];
            for (int idx = 0; idx < sciencePacks.tagCount(); idx++) {
                required[idx] = ((NBTTagInt) sciencePacks.get(idx)).getInt();
            }
            techAdded.setSciencePacksNeeded(required);
            if (tech.getBoolean("leaf"))
                techAdded.setLeafTech();
        }
    }
    
    @Override
    public void addUnlockables(AddUnlockables t) {
        TechTree tree = t.tree;
        NBTTagCompound n = t.treeNBTCompound;
        NBTTagCompound unlocks = n.getCompoundTag("unlockables");
        for (Object key : unlocks.getKeySet()) {
            NBTTagCompound unlock = unlocks.getCompoundTag((String) key);
            Unlockable u = Unlockable.loadUnlockable(unlock);
            if (u == null)
                CivLog.warn("The config did not generate a valid unlockable! (NBT=" + NBTUtils.toString(unlock));
            else
                tree.addUnlockable(u);
        }
    }
    
    @Override
    public String getModID() {
        return "config";
    }
    
    @Override
    public String getShortModName() {
        return "Config";
    }
}