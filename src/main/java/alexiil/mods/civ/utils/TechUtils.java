package alexiil.mods.civ.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alexiil.mods.civ.api.tech.TechTree;
import alexiil.mods.civ.api.tech.TechTree.Tech;
import alexiil.mods.civ.net.MessageHandler;
import alexiil.mods.civ.net.MessagePlayerTechUpdate;
import alexiil.mods.lib.EChatColours;

public class TechUtils {
    public static NBTTagCompound getPlayerCivNBT(EntityPlayer player) {
        NBTTagCompound tag = player.getEntityData();
        NBTTagCompound persistant = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        return persistant.getCompoundTag("civcraft");
    }

    public static void setPlayerCivNBT(EntityPlayer player, NBTTagCompound civ) {
        NBTTagCompound tag = player.getEntityData();
        NBTTagCompound persistant = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        persistant.setTag("civcraft", civ);
        tag.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistant);
    }

    public static List<Tech> getTechs(EntityPlayer player) {
        // TODO: a way of dealing with fake players, that tries to find out the REAL player that placed them
        List<Tech> techs = new ArrayList<Tech>();
        if (player == null)
            return techs;
        NBTTagCompound tag = player.getEntityData();
        NBTTagCompound persistant = tag.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if ((!persistant.hasKey("civcraft") && player.worldObj.isRemote)) {
            MessageHandler.INSTANCE.sendToServer(new MessagePlayerTechUpdate());
        }
        NBTTagCompound civ = persistant.getCompoundTag("civcraft");
        NBTTagCompound tagTechs = civ.getCompoundTag("researched_techs");
        for (Object obj : tagTechs.getKeySet()) {
            String key = (String) obj;
            if (tagTechs.getBoolean(key)) {
                Tech t = TechTree.currentTree.getTech(key);
                if (t == null)
                    continue;
                techs.add(t);
            }
        }
        return techs;
    }

    public static void setTechs(EntityPlayer player, List<Tech> techs) {
        if (player == null)
            return;
        NBTTagCompound civ = getPlayerCivNBT(player);
        NBTTagCompound tagTechs = new NBTTagCompound();
        for (Tech t : techs)
            tagTechs.setBoolean(t.name, true);
        civ.setTag("researched_techs", tagTechs);
        setPlayerCivNBT(player, civ);
    }

    @SideOnly(Side.CLIENT)
    public static void setClientTechs(List<Tech> techs) {
        setTechs(Minecraft.getMinecraft().thePlayer, techs);
    }

    public static void addTech(EntityPlayer player, Tech tech) {
        NBTTagCompound tagCiv = getPlayerCivNBT(player);
        NBTTagCompound tagTechs = tagCiv.getCompoundTag("researched_techs");
        String name = tech.name;
        tagTechs.setBoolean(name, true);
        tagCiv.setTag("researched_techs", tagTechs);
        setPlayerCivNBT(player, tagCiv);
    }

    /** Only use this from the client side */
    @SideOnly(Side.CLIENT)
    public static EChatColours getColour(Tech t) {
        if (hasTech(t, Minecraft.getMinecraft().thePlayer))
            return EChatColours.AQUA;
        return EChatColours.BLUE;
    }

    public static boolean hasTech(Tech tech, EntityPlayer player) {
        return getTechs(player).contains(tech);
    }
}
