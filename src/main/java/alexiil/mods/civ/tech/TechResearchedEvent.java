package alexiil.mods.civ.tech;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.tech.TechTree.Tech;

/** This event is fired whenever a player researches a tech, provided that they have not already unlocked that tech. This
 * is fired on the {@link MinecraftForge#EVENT_BUS}
 * 
 * @author AlexIIL */
public class TechResearchedEvent extends PlayerEvent {
    /** This event is fired whenever a player researches a tech using an {@link #ItemTechnology} item
     * 
     * @author AlexIIL */
    public static class ItemTechResearchedEvent extends TechResearchedEvent {
        public final ItemStack techItem;
        
        public ItemTechResearchedEvent(ItemStack item, EntityPlayer player) {
            super(CivItems.technology.getTech(item), player);
            techItem = item;
        }
        
    }
    
    public final Tech tech;
    
    public TechResearchedEvent(Tech tech, EntityPlayer player) {
        super(player);
        if (player == null)
            throw new NullPointerException("The player cannot be null!");
        if (player instanceof FakePlayer)
            throw new NullPointerException("The player cannot be fake!");
        this.tech = tech;
    }
}
