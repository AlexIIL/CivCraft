package alexiil.mods.civ.compat;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.api.tech.TechTree;
import alexiil.mods.civ.api.tech.TechTree.Tech;
import alexiil.mods.civ.api.tech.TechTreeEvent.AddTechs;
import alexiil.mods.civ.api.tech.TechTreeEvent.AddUnlockables;
import alexiil.mods.civ.api.tech.TechTreeEvent.RegisterTech;
import alexiil.mods.civ.api.tech.unlock.IUnlockable;
import alexiil.mods.civ.api.tech.unlock.Unlockable;
import alexiil.mods.lib.nbt.NBTUtils;

public class ConfigCompat {
    @SubscribeEvent
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

    @SubscribeEvent
    public void registerTech(RegisterTech event) {
        String name = event.tech.name;
        NBTTagCompound n = event.treeNBTCompound;
        NBTTagCompound techs = n.getCompoundTag("techs");
        NBTTagCompound tech = techs.getCompoundTag(name);
        boolean disabled = tech.getBoolean("disabled");
        if (disabled)
            event.setCanceled(true);
    }

    @SubscribeEvent
    public void addUnlockables(AddUnlockables t) {
        TechTree tree = t.tree;

        tree.setUnlockablePrefix("config");

        NBTTagCompound n = t.treeNBTCompound;
        NBTTagCompound unlocks = n.getCompoundTag("unlockables");
        for (Object key : unlocks.getKeySet()) {
            NBTTagCompound unlock = unlocks.getCompoundTag((String) key);
            IUnlockable u = Unlockable.loadUnlockable(unlock);
            if (u == null)
                CivLog.warn("The config did not generate a valid unlockable! (NBT=" + NBTUtils.toString(unlock));
            else
                tree.addUnlockable(u);
        }
    }
}
