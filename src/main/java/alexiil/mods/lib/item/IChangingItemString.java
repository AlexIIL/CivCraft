package alexiil.mods.lib.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IChangingItemString {
    /** @param i
     *            The item to get a string from
     * @param player
     *            The player, BEWARE! this player is client side, so will not have any of the NBT the exists on the
     *            server
     * @return A localized string to be displayed to the player. */
    String[] getString(ItemStack i, EntityPlayer player);
}
