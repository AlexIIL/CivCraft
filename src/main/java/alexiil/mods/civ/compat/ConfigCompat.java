package alexiil.mods.civ.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.tech.TechTreeEvent.AddTechs;
import alexiil.mods.civ.tech.TechTreeEvent.AddUnlockables;

public class ConfigCompat extends ModCompat {
    @Override public void addTechs(AddTechs t) {}
    
    @Override public void addUnlockables(AddUnlockables t) {
        NBTTagCompound n = t.treeNBTCompound;
        NBTTagCompound techs = n.getCompoundTag("techs");
        
        for (Object key : techs.getKeySet()) {
            NBTTagCompound tech = techs.getCompoundTag((String) key);
            NBTTagList parents = tech.getTagList("parents", new NBTTagString().getId());
            List<Tech> newParents = new ArrayList<Tech>();
            for (int i = 0; i < parents.tagCount(); i++) {
                NBTTagString ns = (NBTTagString) parents.get(i);
                String s = ns.getString();
                Tech t0 = t.tree.getTech(s);
                if (t0 != null)
                    newParents.add(t0);
                else
                    TechTree.log.warn("The tech \"" + s + "\" was null! Was that a mistype in the config (under \"" + key + "\")");
            }
            Tech t1 = t.tree.getTech((String) key);
            if (t1 != null)
                t1.setRequirements(newParents.toArray(new Tech[newParents.size()]));
            else
                TechTree.log.warn("The tech \"" + key + "\" was null! Was that a mistype in the config?");
        }
        
        NBTTagCompound unlock = n.getCompoundTag("unlockables");
    }
    
    @Override public String getModID() {
        return "config";
    }
}