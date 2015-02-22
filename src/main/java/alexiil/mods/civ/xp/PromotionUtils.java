package alexiil.mods.civ.xp;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.utils.TechUtils;

public class PromotionUtils {
    public static List<Promotion> getPromotions(EntityPlayer player) {
        NBTTagCompound nbt = TechUtils.getPlayerCivNBT(player);
        NBTTagCompound promo = nbt.getCompoundTag("promotions");
        List<Promotion> promotions = new ArrayList<Promotion>();
        for (Object obj : promo.getKeySet()) {
            String name = (String) obj;
            NBTTagCompound tag = promo.getCompoundTag(name);
            String clsName = tag.getString("_CLASS");
            Promotion toAdd = TechTree.currentTree.promotions.tryMakeNew(clsName, tag);
            if (toAdd != null)
                promotions.add(toAdd);
        }
        return promotions;
    }
}
