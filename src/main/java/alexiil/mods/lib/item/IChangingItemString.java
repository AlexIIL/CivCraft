package alexiil.mods.lib.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IChangingItemString {
    /** Get a string for that item.
     * 
     * @param i
     *            The item to get a string from
     * @param player
     *            The player, BEWARE! this player is client side, so will not have any of the NBT the exists on the
     *            server
     * @return */
    public String[] getString(ItemStack i, EntityPlayer player);
}
