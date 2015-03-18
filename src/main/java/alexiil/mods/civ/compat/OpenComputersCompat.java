// CivCraft- a mod for minecraft
// Copyright (C) 2015 AlexIIL
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see https://github.com/AlexIIL/CivCraft/blob/master/LICENSE

package alexiil.mods.civ.compat;

import li.cil.oc.api.detail.ItemInfo;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.tech.TechTreeEvent.AddTechs;
import alexiil.mods.civ.tech.TechTreeEvent.AddUnlockables;
import alexiil.mods.civ.tech.unlock.ItemCraftUnlock;

public class OpenComputersCompat {
    private Tech computing, robotics, expansions;
    private Tech upgrades, upgrade2;

    @SubscribeEvent
    public void addTechs(AddTechs t) {
        TechTree tree = t.tree;

        computing = tree.addTech("computing", new int[] { 5, 4 }, tree.getTech("automation"));
        robotics = tree.addTech("robotics", new int[] { 3, 4 }, computing).setLeafTech();
        expansions = tree.addTech("expansions", new int[] { 4, 3 }, computing).setLeafTech();

        upgrades = tree.addTech("computing_upgrades", new int[] { 5, 11 }, computing, tree.getTech("engineering"));
        upgrade2 = tree.addTech("computing_upgrade_2", new int[] { 6, 21, 15 }, upgrades, tree.getTech("diamond_working"));
    }

    @SubscribeEvent
    public void addUnlockables(AddUnlockables t) {
        TechTree tree = t.tree;
        tree.setUnlockablePrefix("opencomputers");

        if (tree.hasTech("power"))
            computing.addRequirement(tree.getTech("power"));

        tree.addUnlockable(new ItemCraftUnlock("computing", computing).addUnlocked(getItem("cpu1"), getItem("case1"), getItem("screen1"),
                getItem("hdd1"), getItem("server1"), getItem("ram1"), getItem("ram2"), getItem("tabletCase1")));

        tree.addUnlockable(new ItemCraftUnlock("robotics", robotics).addUnlocked(getItem("droneCase1")));

        tree.addUnlockable(new ItemCraftUnlock("expansions", expansions).addUnlocked(getItem("graphicsCard1"), getItem("lanCard"),
                getItem("wlanCard"), getItem("linkedCard"), getItem("internetCard")));

        tree.addUnlockable(new ItemCraftUnlock("upgrades", upgrades).addUnlocked(getItem("cpu2"), getItem("case2"), getItem("screen2"),
                getItem("hdd2"), getItem("server2"), getItem("ram3"), getItem("ram4"), getItem("tabletCase2")));

        tree.addUnlockable(new ItemCraftUnlock("upgrades+robotics", upgrades, robotics).addUnlocked(getItem("droneCase2")));

        tree.addUnlockable(new ItemCraftUnlock("upgrades+expansions", upgrades, expansions).addUnlocked(getItem("graphicsCard2")));

        tree.addUnlockable(new ItemCraftUnlock("upgrade2", upgrade2).addUnlocked(getItem("cpu3"), getItem("case3"), getItem("screen3"),
                getItem("hdd3"), getItem("server3"), getItem("ram5"), getItem("ram6")));

        tree.addUnlockable(new ItemCraftUnlock("upgrade2+expansions", upgrade2, expansions).addUnlocked(getItem("graphicsCard3")));
    }

    public static ItemStack getItem(String name) {
        ItemInfo ii = li.cil.oc.api.Items.get(name);
        if (ii == null) {
            CivLog.warn("Attempt to get the open computers item \"" + name + "\" failed!");
            CivLog.warn("Called by: " + Thread.currentThread().getStackTrace()[1]);
            return null;
        }
        return ii.createItemStack(1);
    }
}
