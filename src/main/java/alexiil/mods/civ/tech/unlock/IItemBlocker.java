package alexiil.mods.civ.tech.unlock;

import net.minecraft.item.ItemStack;

/** Subclasses of TechUnlockable should implement this if they wish to block the use of an item at all */
public interface IItemBlocker {
    /** @return true If this unlockable blocks the usage of this item */
    public boolean doesBlockItem(ItemStack item);
}
