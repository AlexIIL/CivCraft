package alexiil.mods.civ.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.Event;
import alexiil.mods.civ.compat.vanilla.VanillaCompat;
import alexiil.mods.civ.tech.TechTreeEvent;

public abstract class ModCompat {
    /** Please note: this event is NOT fired on any event busses, so listening for it will not work */
    public static class AddEvent extends Event {
        private List<ModCompat> list = new ArrayList<ModCompat>();
        
        public void addModCompat(ModCompat modCompat) {
            if (modCompat != null)
                list.add(modCompat);
        }
    }
    
    private static List<ModCompat> compats = new ArrayList<ModCompat>();
    
    /** The main entry point for adding stuff to a tech tree. Call this with your ModCompat instance, and it will be
     * added. If your mod has been loaded before CivCraft, then all of the ones you add will be added before any techs
     * are in the tech tree */
    public static void addCompat(ModCompat compat) {
        if (compats.size() > 0)
            compats.add(compats.size() - 1, compat);
        else
            compats.add(compat);
    }
    
    public static void loadCompats() {
        // The first one, unless someone has added their own into it
        compats.add(new VanillaCompat());
        if (Loader.isModLoaded("progressiveautomation"))
            compats.add(new ProgressiveAutomationCompat());
        
        // ALWAYS the last one
        compats.add(new ConfigCompat());
    }
    
    public static void sendAddTechsEvent(TechTreeEvent.AddTechs t) {
        for (ModCompat c : compats)
            c.addTechs(t);
    }
    
    public static void sendAddUnlockableEvent(TechTreeEvent.AddUnlockables t) {
        for (ModCompat c : compats) {
            t.tree.setModCompat(c);
            c.addUnlockables(t);
        }
        t.tree.setModCompat(null);
    }
    
    public static void sendRegisterTechEvent(TechTreeEvent.RegisterTech t) {
        for (ModCompat c : compats)
            c.registerTech(t);
    }
    
    public static void sendRegisterUnlockableEvent(TechTreeEvent.RegisterUnlockable t) {
        for (ModCompat c : compats)
            c.registerUnlockable(t);
    }
    
    public static void sendPreEvent(TechTreeEvent.Pre t) {
        for (ModCompat c : compats)
            c.preInit(t);
    }
    
    public static void sendPostEvent(TechTreeEvent.Post t) {
        for (ModCompat c : compats)
            c.postInit(t);
    }
    
    /** @see {@link TechTreeEvent.AddTechs} */
    public abstract void addTechs(TechTreeEvent.AddTechs t);
    
    /** @see {@link TechTreeEvent.AddUnlockables} */
    public abstract void addUnlockables(TechTreeEvent.AddUnlockables t);
    
    /** @see {@link TechTreeEvent.RegisterTech} */
    public void registerTech(TechTreeEvent.RegisterTech t) {}
    
    /** @see {@link TechTreeEvent.RegisterUnlockable} */
    public void registerUnlockable(TechTreeEvent.RegisterUnlockable t) {}
    
    /** @see {@link TechTreeEvent.Pre} */
    public void preInit(TechTreeEvent.Pre t) {}
    
    /** @see {@link TechTreeEvent.Post} */
    public void postInit(TechTreeEvent.Post t) {}
    
    public abstract String getModID();
    
    public String getUnlockableName(String name) {
        return getModID() + ":" + name;
    }
}
