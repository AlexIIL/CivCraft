package alexiil.mods.civ.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import alexiil.mods.civ.tech.TechTree.Tech;

public class PlayerTechData {
    private static List<PlayerTechData> list = new ArrayList<PlayerTechData>();
    public final UUID id;
    private final List<Tech> techs;
    
    public static PlayerTechData createPlayerTechData(UUID id, List<Tech> techs) {
        for (PlayerTechData dat : list)
            if (dat.id.equals(id))
                return dat;
        return new PlayerTechData(id, techs);
    }
    
    public static PlayerTechData createPlayerTechData(EntityPlayer player) {
        return createPlayerTechData(player.getPersistentID(), TechUtils.getTechs(player));
    }
    
    public static void loadData() {
        
    }
    
    public static void saveData() {
        
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