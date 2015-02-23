package alexiil.mods.lib;

import java.io.File;
import java.text.DecimalFormat;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import alexiil.mods.lib.coremod.ChatTextTime;
import alexiil.mods.lib.coremod.RoamingIPAddress;
import alexiil.mods.lib.coremod.VanillaMethods;

@Mod(modid = AlexIILLib.MODID, version = "INVALID! BUILD ERROR!", useMetadata = true) public class AlexIILLib extends AlexIILMod {
    public static final String MODID = "AlexIILLib";
    public static final DecimalFormat df = new DecimalFormat();
    
    @Instance(MODID) public static AlexIILLib instance;
    
    public static Property betterPotions, timeText, roamingIP;
    
    static {
        loadConfigs();
    }
    
    public static void loadConfigs() {
        Configuration cfg = new Configuration(new File("./config/" + MODID + ".cfg"));
        cfg.load();
        betterPotions = cfg.get("general", "betterPotions", false);
        timeText = cfg.get("general", "textTime", false);
        roamingIP = cfg.get("general", "roamingIP", false);
    }
    
    @EventHandler public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        
        log.info("This is AlexIIL Lib, version " + meta.version);
        
        betterPotions = cfg.getProp("betterPotions", "true");
        timeText = cfg.getProp("textTime", "true");
        roamingIP = cfg.getProp("roamingIP", "true");
        
        cfg.saveAll();
        
        event.getModConfigurationDirectory().mkdirs();
        
        VanillaMethods.init();
        ChatTextTime.init();
        RoamingIPAddress.init();
    }
    
    @EventHandler public void init(FMLInitializationEvent event) {
        
    }
}
