package alexiil.mods.civ;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.lib.nbt.NBTUtils;

public class CivTechs {
    public static void loadTree() {
        
        Property resetTechs = CivCraft.instance.cfg.getProp("resetTechs", true);
        resetTechs.comment = "If this is true, then the tech tree will be redone, and any customisations you have made will be deleted";
        boolean resetTechFlag = resetTechs.getBoolean();
        
        // Actual Loading
        
        ConfigCategory cat = CivCraft.instance.cfg.cfg.getCategory("TechTree");
        NBTTagCompound nbt = NBTUtils.convertToNBT(cat);
        
        if (resetTechFlag)
            nbt = new NBTTagCompound();
        resetTechs.set(false);
        
        TechTree.currentTree.init(nbt);
        TechTree.currentTree.save(nbt);
        // log.info(NBTUtils.toString(nbt));
        NBTUtils.convertToConfigCategory(CivCraft.instance.cfg.cfg.getCategory("TechTree"), nbt);
        
        // Testing
        
        if (CivConfig.debugMode.getBoolean()) {
            NBTTagCompound n = new NBTTagCompound();
            TechTree.currentTree.save(n);
            
            CivLog.debugInfo("Saved tree as:\n" + NBTUtils.toString(n));
            
            ConfigCategory cat2 = new ConfigCategory("hi");
            NBTUtils.convertToConfigCategory(cat2, n);
            
            NBTTagCompound n2 = NBTUtils.convertToNBT(cat2);
            
            CivLog.debugInfo("Convert to and from Config Category:\n" + NBTUtils.toString(n2));
        }
    }
}
