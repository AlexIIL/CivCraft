package alexiil.mods.civ.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import alexiil.mods.lib.AlexIILMod;
import alexiil.mods.lib.item.ItemBase;

public class ItemMediGun extends ItemBase {
    public ItemMediGun(String name, AlexIILMod mod) {
        super(name, mod);
    }
    
    @Override public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        return stack;
    }
    
    @Override public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        return itemStackIn;
    }
    
}
