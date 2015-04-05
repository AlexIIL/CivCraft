package alexiil.mods.civ.api.tech.unlock;

import net.minecraft.item.ItemStack;

/** Classes implementing IUnlockable should implement this if they wish to block the creation of an item */
public interface IItemBlocker {
    /** @return true If this unlockable blocks the usage of this item. Unfortunately its not possible to give any hints as
     *         to where its being blocked */
    boolean doesBlockItem(ItemStack item);
}
