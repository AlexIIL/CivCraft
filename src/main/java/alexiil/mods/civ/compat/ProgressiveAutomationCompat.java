package alexiil.mods.civ.compat;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.tech.TechTreeEvent.AddTechs;
import alexiil.mods.civ.tech.TechTreeEvent.AddUnlockables;
import alexiil.mods.civ.tech.unlock.ItemCraftUnlock;
import alexiil.mods.lib.ErrorHandling;

public class ProgressiveAutomationCompat extends ModCompat {
    private Tech techLogging, techPower, techRF;

    @Override
    public void addTechs(AddTechs t) {
        TechTree tree = t.tree;
        techLogging = tree.addTech("logging", new int[] { 2 }, tree.getTech("mining"));
        techPower = tree.addTech("power", new int[] { 4 }, tree.getTech("smelting"), tree.getTech("automation"));
        techRF = tree.addTech("redstone_flux", new int[] { 2 }, techPower).setLeafTech();
    }

    @Override
    public void addUnlockables(AddUnlockables t) {
        TechTree tree = t.tree;
        Tech automation = tree.getTech("automation");
        Tech mining = tree.getTech("mining");

        tree.addUnlockable(new ItemCraftUnlock("logging", techLogging, automation).addUnlocked(getBlocks("chopper")));
        tree.addUnlockable(new ItemCraftUnlock("mining", mining, automation).addUnlocked(getBlocks("miner")));
        tree.addUnlockable(new ItemCraftUnlock("crafting", automation, tree.getTech("construction")).addUnlocked(getBlocks("crafter")));
        tree.addUnlockable(new ItemCraftUnlock("generating", tree.getTech("smelting")).addUnlocked(getBlocks("generator")));
        tree.addUnlockable(new ItemCraftUnlock("planting", tree.getTech("agriculture"), automation).addUnlocked(getBlocks("planter")));

        tree.addUnlockable(new ItemCraftUnlock("redstone_flux", techRF).addUnlocked(getItem("rfEngine")));
    }

    @Override
    public String getModID() {
        return "progressiveautomation";
    }

    @Override
    public String getShortModName() {
        return "PA";
    }

    private Block[] getBlocks(String name) {
        String clsName = "com.vanhal.progressiveautomation.blocks.PABlocks";
        try {
            Class<?> cls = Class.forName(clsName);
            Field fld = cls.getField(name);
            @SuppressWarnings("unchecked")
            List<Block> blocks = (List<Block>) fld.get(null);
            return blocks.toArray(new Block[0]);

        }
        catch (Throwable e) {
            CivLog.warn("An error was thrown while trying to get a Progressive Automation Block list");
            ErrorHandling.printClassInfo(clsName);
            e.printStackTrace();
        }

        return new Block[0];
    }

    private Item getItem(String name) {
        String clsName = "com.vanhal.progressiveautomation.items.PAItems";
        try {
            Class<?> cls = Class.forName(clsName);
            Field fld = cls.getField(name);
            return (Item) fld.get(null);
        }
        catch (Throwable e) {
            ErrorHandling.printClassInfo(clsName);
            e.printStackTrace();
        }

        return null;
    }
}
