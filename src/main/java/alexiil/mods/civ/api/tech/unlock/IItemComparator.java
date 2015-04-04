package alexiil.mods.civ.api.tech.unlock;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public interface IItemComparator {
    public boolean isConsideredEqual(ItemStack toCompare);

    /** @return <code>True</code> if this can be saved and loaded from an NBTTagCompound. So, return true if you are just
     *         checking a single item or a range, but false if you need to check very specific things that <s>you cannot
     *         be bothered to code </s> cannot be saved in a simple manor */
    public boolean canSaveAndLoad();

    /** Actually save this comparator. This should return an NBT Tag Compound that could be reinserted into
     * {@link #load(NBTTagCompound)} to give a comparator that is equivalent to this one. This method will never be
     * called if {@link #canSaveAndLoad()} returns false */
    public NBTTagCompound save();

    /** @see #save() */
    public IItemComparator load(NBTTagCompound nbt);

    /** @return A list of what this comparator does, to be shown on technological progress notes and in the tech tree GUI */
    public List<String> getDescription(Unlockable unlockable);
}
