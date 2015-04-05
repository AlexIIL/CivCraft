package alexiil.mods.civ.item;

import alexiil.mods.civ.CivCraft;
import alexiil.mods.lib.item.ItemBase;

public class CivItems {
    public static ItemBase[] sciencePacks = new ItemBase[5];
    /** The item that holds all of the technology's */
    public static ItemTechnology technology;
    public static ItemTechBag techBag;
    public static ItemResearchProgress researchProgress;

    public static void init() {
        sciencePacks[0] = new ItemBase("sciencePack0", CivCraft.instance);
        sciencePacks[1] = new ItemBase("sciencePack1", CivCraft.instance);
        sciencePacks[2] = new ItemBase("sciencePack2", CivCraft.instance);
        sciencePacks[3] = new ItemBase("sciencePack3", CivCraft.instance);
        sciencePacks[4] = new ItemBase("sciencePack4", CivCraft.instance);
        technology = new ItemTechnology("technology");
        techBag = new ItemTechBag("techBag");
        researchProgress = new ItemResearchProgress("researchProgress");
    }
}
