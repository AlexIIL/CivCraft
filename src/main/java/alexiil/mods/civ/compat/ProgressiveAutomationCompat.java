package alexiil.mods.civ.compat;

import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.tech.TechTreeEvent.AddTechs;
import alexiil.mods.civ.tech.TechTreeEvent.AddUnlockables;
import alexiil.mods.civ.tech.unlock.ItemCraftUnlock;

import com.vanhal.progressiveautomation.blocks.BlockChopper;
import com.vanhal.progressiveautomation.blocks.BlockCrafter;
import com.vanhal.progressiveautomation.blocks.BlockGenerator;
import com.vanhal.progressiveautomation.blocks.BlockMiner;
import com.vanhal.progressiveautomation.blocks.BlockPlanter;
import com.vanhal.progressiveautomation.blocks.PABlocks;

public class ProgressiveAutomationCompat extends ModCompat {
    private Tech techLogging, techPower, techRF;
    
    @Override public void addTechs(AddTechs t) {
        TechTree tree = t.tree;
        techLogging = tree.addTech("logging", new int[] { 2 }, tree.getTech("mining"));
        techPower = tree.addTech("power", new int[] { 4 }, tree.getTech("smelting"), tree.getTech("automation"));
        techRF = tree.addTech("redstone_flux", new int[] { 2 }, techPower).setLeafTech();
    }
    
    @Override public void addUnlockables(AddUnlockables t) {
        TechTree tree = t.tree;
        Tech automation = tree.getTech("automation");
        Tech mining = tree.getTech("mining");
        
        tree.addUnlockable(new ItemCraftUnlock("logging", techLogging, automation).addUnlocked(PABlocks.chopper.toArray(new BlockChopper[0])));
        tree.addUnlockable(new ItemCraftUnlock("mining", mining, automation).addUnlocked(PABlocks.miner.toArray(new BlockMiner[0])));
        tree.addUnlockable(new ItemCraftUnlock("crafting", automation, tree.getTech("construction")).addUnlocked(PABlocks.crafter
                .toArray(new BlockCrafter[0])));
        tree.addUnlockable(new ItemCraftUnlock("generating", tree.getTech("smelting")).addUnlocked(PABlocks.generator.toArray(new BlockGenerator[0])));
        tree.addUnlockable(new ItemCraftUnlock("planting", tree.getTech("agriculture"), automation).addUnlocked(PABlocks.planter
                .toArray(new BlockPlanter[0])));
        
        tree.addUnlockable(new ItemCraftUnlock("redstone_flux", techRF));
    }
    
    @Override public String getModID() {
        return "progressiveautomation";
    }
}
