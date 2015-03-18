package alexiil.mods.civ.tech;

import java.util.List;
import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.item.ItemTechnology;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.utils.TechUtils;

/** An Unlockable is something that is unlocked whenever all the required tech's have been researched. More specifically,
 * whenever a player unlocks a tech that is required by this unlockable, assuming ALL other tech's required by this are
 * also all unlocked, the {@link #Unlockable.unlock(EntityPlayer)} method is called. This method will only ever be
 * called once per save-game, per player, assuming that no {@link #TechResearchedEvent} is fired if a player has already
 * unlocked that tech.
 * 
 * @author AlexIIL */
public abstract class Unlockable {
    private String name;
    /** If this is false, then it wont be saved in the config. Useful if you have settings that you cannot deduced from a
     * config, and need to be specially coded */
    protected boolean isLoadable = true;

    public static Unlockable loadUnlockable(NBTTagCompound nbt) {
        String type = nbt.getString("type");
        boolean isLoadable = nbt.getBoolean("isLoadable");
        if (!isLoadable)
            return null;
        Map<String, IUnlockableConstructor> map = TechTree.currentTree.getUnlockableTypes();
        IUnlockableConstructor cons = map.get(type);
        if (cons == null) {
            CivLog.warn("Tried to load an unlockable of type \"" + type + "\", but it did not exist in the map!");
            return null;
        }
        return cons.createUnlockable(nbt);
    }

    public Unlockable(String name) {
        this.name = TechTree.currentTree.currentPrefix + ":" + name;
    }

    protected Unlockable(NBTTagCompound nbt) {
        name = nbt.getString("name");
    }

    public final String getName() {
        return name;
    }

    /** @return A list of all the tech's that this 'thing' requires to have been researched, before this itself is
     *         unlocked.
     *         <p>
     *         Note that if this list is empty or null, the {@link #unlock(EntityPlayer)} method will never be called,
     *         and this object can be considered useless.
     *         <p>
     *         Note that this should return the same array contents every time it is called, and it should be stored in
     *         a variable to speed things up.
     *         <p>
     *         Note that if any of these are either invalid tech objects or null, unknown behaviour will occur. */
    public abstract TechTree.Tech[] requiredTechs();

    /** @return The unlocalised name for this requirement. This should just be the name of the requirement most of the
     *         time */
    public abstract String getUnlocalisedName();

    /** @return The localised name for this requirement.
     *         <p>
     *         Default is just {@link I18n}.format( {@link #getUnlocalisedName()}) on the client side, server side just
     *         returns the unlocalised name */
    public abstract String getLocalisedName();

    /** @return The description for this unlockable, which is shown to the player. */
    public abstract List<String> getDescription();

    /** Called whenever the player unlocks all the tech's required for this 'thing'.
     * <p>
     * Use this if you need to do anything to the world or player, for example change the players NBT values */
    public abstract void unlock(EntityPlayer player);

    /** @return true if this should show up on shift-hover for {@link ItemTechnology} usages (for example).
     *         <p>
     *         The general idea of this method, is that if you want the player to be aware of this object, this should
     *         return true */
    public abstract boolean shouldShow();

    /** If you need to save any information about this requirement, then this is the NBTTagCompound that you do it in */
    public void save(NBTTagCompound nbt) {
        nbt.setString("type", getType());
        nbt.setString("name", name);
        nbt.setBoolean("isLoadable", isLoadable);
    }

    public boolean isUnlocked(EntityPlayer player) {
        return isUnlocked(TechUtils.getTechs(player));
    }

    public boolean isUnlocked(List<Tech> techs) {
        for (Tech t : requiredTechs()) {
            boolean hasFound = false;
            for (Tech t0 : techs) {
                if (t == t0)
                    hasFound = true;
            }
            if (!hasFound)
                return false;
        }
        return true;
    }

    /** This should return a string that identifies this type uniquely. It is recommended that you use "modid:name", so
     * ItemCraftUnlock is "CivCraft:ItemCraftUnlock". This needs to be the same as what you have registered in the
     * TechTree */
    public abstract String getType();
}
