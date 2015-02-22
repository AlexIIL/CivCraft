package alexiil.mods.civ.item;

import alexiil.mods.civ.CivCraft;
import alexiil.mods.lib.item.ItemBase;

public class CivItems {
    // BLOCK + ITEM STUFFS
    // TODO: figure out if ANY of these are actually needed...
    // AFTER-NOTE: probably not
    /** This is used to create stuff */
    public static ItemBase hammer;
    /** This is used to do research. */
    public static ItemBase[] sciencePacks = new ItemBase[5];
    /** This stays around FOR EVER... and can be used to buy stuff (like hammers, but at an abysmal rate. other things
     * TBD) */
    public static ItemBase gold;
    /** This is used to add more magical workers to you civ */
    public static ItemBase food;
    /** This is used to expand your area of influence around your capitol */
    public static ItemBase culture;
    /** This can be stored forever (like gold), and can be used to do religious stuffs */
    public static ItemBase faith;
    /** The item that holds all of the technology's */
    public static ItemTechnology technology;
    public static ItemTechBag techBag;
    public static ItemMediGun mediGun, quickFix, vacinator, krits;
    
    public static void init() {
        // TODO: decide if ANY of these are actually needed- probably not (at least, not in the current plan)
        // hammer = new ItemBase("hammer", this);
        // gold = new ItemBase("gold", this);
        // food = new ItemBase("food", this);
        // faith = new ItemBase("faith", this);
        
        // TODO: make an alternative, more minecraft like culture system (perhaps based on the players abilities?)
        // culture = new ItemBase("culture", this);
        // These are defiantly used
        sciencePacks[0] = new ItemBase("sciencePack0", CivCraft.instance);
        sciencePacks[1] = new ItemBase("sciencePack1", CivCraft.instance);
        sciencePacks[2] = new ItemBase("sciencePack2", CivCraft.instance);
        sciencePacks[3] = new ItemBase("sciencePack3", CivCraft.instance);
        sciencePacks[4] = new ItemBase("sciencePack4", CivCraft.instance);
        technology = new ItemTechnology("technology");
        techBag = new ItemTechBag("techBag");
    }
}
