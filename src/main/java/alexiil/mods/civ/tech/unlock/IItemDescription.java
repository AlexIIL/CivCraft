package alexiil.mods.civ.tech.unlock;

import net.minecraft.item.ItemStack;

public interface IItemDescription {
    public String[] getDescription(ItemStack stack);
}
