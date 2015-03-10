package alexiil.mods.civ.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.Lib;
import alexiil.mods.lib.LangUtils;

public class ConfigGuiFactory implements IModGuiFactory {
    public static class ActualConfig extends GuiConfig {
        public ActualConfig(GuiScreen parent) {
            super(parent, getConfigElements(), Lib.Mod.ID, false, false, LangUtils.format("civcraft.config.title"));
        }
        
        private static List<IConfigElement> getConfigElements() {
            List<IConfigElement> elements = new ArrayList<IConfigElement>();
            Configuration cfg = CivCraft.instance.cfg.cfg;
            elements.add(new ConfigElement(cfg.getCategory(Configuration.CATEGORY_GENERAL)));
            elements.add(new ConfigElement(cfg.getCategory("blocks")));
            elements.add(new ConfigElement(cfg.getCategory("items")));
            return elements;
        }
    }
    
    @Override
    public void initialize(Minecraft minecraftInstance) {}
    
    @Override
    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return Config.class;
    }
    
    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
    
    @Override
    public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
        return null;
    }
    
}
