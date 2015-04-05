package alexiil.mods.civ.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.Lib;
import alexiil.mods.lib.item.ItemBase;

public class ItemResearchProgress extends ItemBase {
    public ItemResearchProgress(String name) {
        super(name, CivCraft.instance);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        player.openGui(CivCraft.instance, Lib.Gui.RESEARCH_PROGRESS, world, 0, 0, 0);
        return itemStack;
    }
}
