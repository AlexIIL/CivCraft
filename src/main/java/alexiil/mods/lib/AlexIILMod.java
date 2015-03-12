package alexiil.mods.lib;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.logging.log4j.Logger;

import alexiil.mods.lib.net.INetworkProvider;

public class AlexIILMod {
    public static Property netRate, netDistance;
    public Logger log;
    public ModMetadata meta;
    public CreativeTabs tab;
    public ConfigAccess cfg;
    public INetworkProvider provider;

    public void preInit(FMLPreInitializationEvent event) {
        log = event.getModLog();
        meta = event.getModMetadata();
        cfg = new ConfigAccess(event.getSuggestedConfigurationFile(), this);
    }

    public void postInit(FMLPostInitializationEvent event) {
        cfg.saveAll();
    }

    public String format(String toFormat, Object... objects) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return I18n.format(toFormat, objects);
        return toFormat;
    }
}
