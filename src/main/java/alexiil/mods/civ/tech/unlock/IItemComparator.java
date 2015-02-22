package alexiil.mods.civ.tech.unlock;

import net.minecraft.item.ItemStack;

public interface IItemComparator {
    public boolean isConsideredEqual(ItemStack toCompare);
}
