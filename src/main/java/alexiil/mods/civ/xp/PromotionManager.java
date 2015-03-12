package alexiil.mods.civ.xp;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.EState;

public class PromotionManager {
    // TODO: promotions properly
    private List<Promotion> promotions = new ArrayList<Promotion>();
    private final TechTree techTree;

    public PromotionManager(TechTree tree) {
        techTree = tree;
    }

    public void addPromotion(Promotion toAdd) {
        if (techTree.getState() != EState.FINALISED)
            promotions.add(toAdd);
    }

    public NBTTagCompound saveToNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        return nbt;
    }

    // Ouch. This feels... wrong, in so many ways
    public Promotion tryMakeNew(String clsName, NBTTagCompound data) {
        try {
            Class<?> cls = Class.forName(clsName);
            if (Promotion.class.isAssignableFrom(cls)) {
                Class<? extends Promotion> clazz = cls.asSubclass(Promotion.class);
                Constructor<? extends Promotion> c = clazz.getConstructor(NBTTagCompound.class);
                return c.newInstance(data);
            }
        }
        catch (ClassCastException e) {
            CivLog.warn("The class " + clsName + " did not ");
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            CivLog.warn("The class " + clsName + " was not found");
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            CivLog.warn("The class " + clsName + " did not have a constructor with a single NBTTagCompound argument");
            e.printStackTrace();
        }
        catch (SecurityException e) {
            CivLog.warn("For some reason, we are not allowed to search for a constructor in the class " + clsName);
            e.printStackTrace();
        }
        catch (InstantiationException e) {
            CivLog.warn("For some reason, we were not able to instate the class " + clsName);
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            CivLog.warn("For some reason, we did not have access to the constructor of " + clsName);
            e.printStackTrace();
        }
        catch (IllegalArgumentException e) {
            CivLog.warn("For some reason, the argument " + data + " was illegal for the constructor of " + clsName);
            e.printStackTrace();
        }
        catch (InvocationTargetException e) {
            CivLog.warn("An exception was thrown in the constructor of " + clsName);
            e.printStackTrace();
        }
        return null;
    }
}
