package alexiil.mods.civ.api.tech.unlock;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import alexiil.mods.civ.api.tech.TechTree;
import alexiil.mods.civ.api.tech.TechTree.Tech;
import alexiil.mods.civ.item.ItemTechnology;

/** An Unlockable is something that is unlocked whenever all the required tech's have been researched. More specifically,
 * whenever a player unlocks a tech that is required by this unlockable, assuming ALL other tech's required by this are
 * also all unlocked, the {@link #Unlockable.unlock(EntityPlayer)} method is called. This method will only ever be
 * called once per save-game, per player, assuming that no {@link #TechResearchedEvent} is fired if a player has already
 * unlocked that tech. */
public interface IUnlockable {
    /** @return A name that uniquely identifies this unlockable */
    String getName();

    /** @return The unlocalised name for this requirement. This should just be the name of the requirement most of the
     *         time */
    String getUnlocalisedName();

    /** @return The localised name for this requirement.
     *         <p>
     *         Default is just {@link I18n}.format( {@link #getUnlocalisedName()}) on the client side, server side just
     *         returns the unlocalised name */
    String getLocalisedName();

    /** @return The description for this unlockable, which is shown to the player. */
    List<String> getDescription();

    /** Called whenever the player unlocks all the tech's required for this 'thing'.
     * <p>
     * Use this if you need to do anything to the world or player, for example change the players NBT values */
    void unlock(EntityPlayer player);

    /** @return true if this should show up on shift-hover for {@link ItemTechnology} usages (for example).
     *         <p>
     *         The general idea of this method, is that if you want the player to be aware of this object, this should
     *         return true */
    boolean shouldShow();

    /** @return <code>True</code> if the player is considered to have unlocked this */
    boolean isUnlocked(EntityPlayer player);

    /** @return <code>True</code> if the list of technologies is sufficient to have unlocked this unlockable */
    boolean isUnlocked(List<Tech> techs);

    /** This should return a string that identifies this type uniquely. It is recommended that you use "modid:name", so
     * ItemCraftUnlock is "CivCraft:ItemCraftUnlock". This needs to be the same as what you have registered in the
     * TechTree */
    String getType();

    /** If you need to save any information about this requirement, then this is the NBTTagCompound that you do it in */
    void save(NBTTagCompound nbt);

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
    TechTree.Tech[] requiredTechs();

}
