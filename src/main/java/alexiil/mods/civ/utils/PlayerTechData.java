package alexiil.mods.civ.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.api.tech.TechTree;
import alexiil.mods.civ.api.tech.TechTree.Tech;

public class PlayerTechData {
    private static List<PlayerTechData> list = new ArrayList<PlayerTechData>();
    public final UUID id;
    private final List<Tech> techs;

    public static PlayerTechData createPlayerTechData(UUID id, List<Tech> techs) {
        for (PlayerTechData dat : list)
            if (dat.id.equals(id)) {
                for (Tech t : techs)
                    if (!dat.techs.contains(t))
                        dat.techs.add(t);
                return dat;
            }
        PlayerTechData ptd = new PlayerTechData(id, techs);
        list.add(ptd);
        return ptd;
    }

    public static PlayerTechData createPlayerTechData(EntityPlayer player) {
        return createPlayerTechData(player.getPersistentID(), TechUtils.getTechs(player));
    }

    public static PlayerTechData createFromUUID(UUID id) {
        return createPlayerTechData(id, new ArrayList<Tech>());
    }

    public static void load(NBTTagCompound nbt) {
        list.clear();
        for (Object obj : nbt.getKeySet()) {
            String key = (String) obj;
            NBTTagList tagList = nbt.getTagList(key, Lib.NBT.STRING);
            List<Tech> techs = new ArrayList<Tech>();
            for (int i = 0; i < tagList.tagCount(); i++)
                techs.add(TechTree.currentTree.getTech(tagList.getStringTagAt(i)));
            createPlayerTechData(UUID.fromString(key), techs);
        }
    }

    public static NBTTagCompound save() {
        NBTTagCompound nbt = new NBTTagCompound();
        for (PlayerTechData ptd : list) {
            NBTTagList techList = new NBTTagList();
            for (Tech t : ptd.techs)
                techList.appendTag(new NBTTagString(t.name));
            nbt.setTag(ptd.id.toString(), techList);
        }
        return nbt;
    }

    private PlayerTechData(UUID id, List<Tech> techs) {
        this.id = id;
        this.techs = techs;
    }

    public List<Tech> getTechs() {
        Entity entity = MinecraftServer.getServer().getEntityFromUuid(id);
        if (entity == null)
            return techs;
        if (entity instanceof EntityPlayer) {
            techs.clear();
            techs.addAll(TechUtils.getTechs((EntityPlayer) entity));
        }
        return techs;
    }
}
