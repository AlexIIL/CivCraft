package alexiil.mods.civ.tech.unlock;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import alexiil.mods.civ.tech.Unlockable;

public interface IItemComparator {
    public boolean isConsideredEqual(ItemStack toCompare);

    /** @return <code>True</code> if this can be saved and loaded from an NBTTagCompound. So, return true if you are just
     *         checking a single item or a range, but false if you need to check very specific things that you cannot be
     *         bothered to code. :P */
    public boolean canSaveAndLoad();

    public NBTTagCompound save();

    public IItemComparator load(NBTTagCompound nbt);

    /** @return A list of what this comparator does, to be shown on technological progress notes and in the tech tree gui */
    public List<String> getDescription(Unlockable unlockable);
}
