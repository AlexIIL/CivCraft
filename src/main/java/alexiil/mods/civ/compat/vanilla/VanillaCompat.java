package alexiil.mods.civ.compat.vanilla;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.tech.TechTreeEvent;
import alexiil.mods.civ.tech.TechTreeEvent.AddTechs;
import alexiil.mods.civ.tech.TechTreeEvent.AddUnlockables;
import alexiil.mods.civ.tech.unlock.ItemCraftUnlock;

/** Okay, technically this isn't a mod compatibility (vanilla minecraft is a hard dependency of this mod, how strange),
 * but it allows for a central place for vanilla tech code, in the same place as other tech code. */
public class VanillaCompat {

    private Tech techAgri;
    private Tech techSail, techAnimal, techArchery, techMining, techWriting;
    private Tech techSmelting, techMasonry, techTrap, techAutomation, techEnchanting;
    private Tech techIron, techPottery, techConstruction, techHorses;
    private Tech techGold, techEngineering;
    private Tech techDiamond;

    @SubscribeEvent
    public void preInit(TechTreeEvent.Pre t) {
        String start = Lib.Mod.ID + ":";
        t.tree.registerUnlockable(start + "ItemCraftUnlock", ItemCraftUnlock.class);
    }

    @SubscribeEvent
    public void addTechs(AddTechs event) {
        TechTree tree = event.tree;
        // Tier 0
        techAgri = tree.addTech("agriculture", new int[] { 1 });
        // Tier 1
        techSail = tree.addTech("sailing", new int[] { 2 }, techAgri);
        techAnimal = tree.addTech("animal_husbandry", new int[] { 2 }, techAgri);
        techArchery = tree.addTech("archery", new int[] { 2 }, techAgri);
        techMining = tree.addTech("mining", new int[] { 2 }, techAgri);
        techWriting = tree.addTech("writing", new int[] { 3 }, techAgri);
        // Tier 2
        techSmelting = tree.addTech("smelting", new int[] { 3 }, techMining);
        techMasonry = tree.addTech("masonry", new int[] { 3 }, techMining);
        techTrap = tree.addTech("trapping", new int[] { 3 }, techAnimal);
        techAutomation = tree.addTech("automation", new int[] { 5 }, techAnimal, techArchery, techMining);
        techEnchanting = tree.addTech("enchanting", new int[] { 10, 5 }, techWriting);
        // Tier 3
        techPottery = tree.addTech("pottery", new int[] { 4 }, techSmelting);
        techIron = tree.addTech("iron_working", new int[] { 4 }, techSmelting);
        techConstruction = tree.addTech("construction", new int[] { 4 }, techMasonry);
        techHorses = tree.addTech("horseback_riding", new int[] { 4 }, techTrap);
        // Tier 4
        techGold = tree.addTech("gold_working", new int[] { 3, 6 }, techIron);
        techEngineering = tree.addTech("engineering", new int[] { 3, 6 }, techConstruction);
        // Tier 5
        techDiamond = tree.addTech("diamond_working", new int[] { 4, 9, 4 }, techGold, techEngineering);
    }

    @SubscribeEvent
    public void addUnlockables(AddUnlockables t) {
        // Agriculture
        TechTree tree = t.tree;

        tree.setUnlockablePrefix("civcraft");

        tree.addUnlockable(new ItemCraftUnlock("agriculture", techAgri).addUnlocked(Items.wooden_hoe));
        tree.addUnlockable(new ItemCraftUnlock("agriculture+masonry", techAgri, techMasonry).addUnlocked(Items.stone_hoe));
        tree.addUnlockable(new ItemCraftUnlock("agriculture+iron", techAgri, techIron).addUnlocked(Items.iron_hoe));
        tree.addUnlockable(new ItemCraftUnlock("agriculture+gold", techAgri, techGold).addUnlocked(Items.golden_hoe));
        tree.addUnlockable(new ItemCraftUnlock("agriculture+diamond", techAgri, techDiamond).addUnlocked(Items.diamond_hoe));
        // Sailing
        tree.addUnlockable(new ItemCraftUnlock("sailing+wood", techSail).addUnlocked(Items.boat));
        // Animal Husbandry
        tree.addUnlockable(new ItemCraftUnlock("animal+husbandry", techAnimal).addUnlocked(Items.saddle, Items.lead));
        // Archery
        tree.addUnlockable(new ItemCraftUnlock("archery+wood", techArchery).addUnlocked(Items.bow));
        // Mining
        tree.addUnlockable(new ItemCraftUnlock("mining", techMining).addUnlocked(Items.wooden_pickaxe));
        tree.addUnlockable(new ItemCraftUnlock("mining+masonry", techMining, techMasonry).addUnlocked(Items.stone_pickaxe));
        tree.addUnlockable(new ItemCraftUnlock("mining+iron", techMining, techIron).addUnlocked(Items.iron_pickaxe));
        tree.addUnlockable(new ItemCraftUnlock("mining+gold", techMining, techGold).addUnlocked(Items.golden_pickaxe));
        tree.addUnlockable(new ItemCraftUnlock("mining+diamond", techMining, techDiamond).addUnlocked(Items.diamond_pickaxe));
        // Writing
        tree.addUnlockable(new ItemCraftUnlock("writing", techWriting).addUnlocked(Items.paper, Items.book, Items.writable_book, Items.written_book,
                CivItems.techBag).addUnlocked(Blocks.bookshelf));
        // Smelting
        tree.addUnlockable(new ItemCraftUnlock("smelting", techSmelting).addUnlocked(Blocks.furnace));
        // Masonry
        tree.addUnlockable(new ItemCraftUnlock("masonry", techMasonry).addUnlocked(Blocks.stone));
        tree.addUnlockable(new ItemCraftUnlock("masonry+construction", techMasonry, techConstruction).addUnlocked(Blocks.stone_stairs,
                Blocks.stone_pressure_plate, Blocks.stone_slab2));
        // Trapping
        tree.addUnlockable(new ItemCraftUnlock("trapping", techTrap).addUnlocked(Items.lead));
        // Automation
        tree.addUnlockable(new ItemCraftUnlock("automation", techAutomation).addUnlocked(Blocks.hopper, Blocks.dispenser, Blocks.dropper));
        // Enchanting
        tree.addUnlockable(new ItemCraftUnlock("enchanting", techEnchanting).addUnlocked(Blocks.enchanting_table).addUnlocked(Items.enchanted_book));
        // Pottery
        tree.addUnlockable(new ItemCraftUnlock("pottery", techPottery).addUnlocked(Items.brick));
        // Iron Working
        tree.addUnlockable(new ItemCraftUnlock("iron_working", techIron).addUnlocked(Items.iron_ingot, Items.iron_axe, Items.iron_boots,
                Items.iron_chestplate, Items.iron_door, Items.iron_helmet, Items.iron_horse_armor, Items.iron_leggings, Items.iron_shovel,
                Items.iron_sword).addUnlocked(Blocks.iron_block, Blocks.iron_ore));
        // Construction
        tree.addUnlockable(new ItemCraftUnlock("construction", techConstruction).addUnlocked(Blocks.oak_stairs, Blocks.acacia_stairs,
                Blocks.birch_stairs, Blocks.dark_oak_stairs, Blocks.jungle_stairs, Blocks.spruce_stairs));
        tree.addUnlockable(new ItemCraftUnlock("construction+iron_working", techConstruction, techIron).addUnlocked(Blocks.iron_bars));
        // Horses
        tree.addUnlockable(new ItemCraftUnlock("horseback_riding", techHorses).addUnlocked(Items.saddle));
        // Gold
        tree.addUnlockable(new ItemCraftUnlock("gold_working", techGold).addUnlocked(Items.gold_ingot, Items.golden_axe, Items.golden_boots,
                Items.golden_chestplate, Items.golden_helmet, Items.golden_horse_armor, Items.golden_leggings, Items.golden_shovel,
                Items.golden_sword));
        // Engineering
        tree.addUnlockable(new ItemCraftUnlock("engineering", techEngineering).addUnlocked(Items.comparator, Items.repeater).addUnlocked(
                Blocks.redstone_torch, Blocks.piston));
        // Diamond
        tree.addUnlockable(new ItemCraftUnlock("diamond_working", techDiamond).addUnlocked(Items.diamond, Items.diamond_axe, Items.diamond_boots,
                Items.diamond_chestplate, Items.diamond_helmet, Items.diamond_horse_armor, Items.diamond_leggings, Items.diamond_shovel,
                Items.diamond_sword).addUnlocked(Blocks.diamond_block));
    }
}
