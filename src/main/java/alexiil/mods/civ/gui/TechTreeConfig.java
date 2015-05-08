package alexiil.mods.civ.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import alexiil.mods.civ.Lib;
import alexiil.mods.lib.LangUtils;

public class TechTreeConfig extends GuiConfig {
    private final Config parent;

    public TechTreeConfig(Config parent, Configuration techTree) {
        super(parent, getConfigElements(techTree), Lib.Mod.ID, true, false, LangUtils.format("civcraft.config.tech.title"));
        this.parent = parent;
    }

    private static List<IConfigElement> getConfigElements(Configuration techTree) {
        List<IConfigElement> elements = new ArrayList<IConfigElement>();
        elements.add(new ConfigElement(techTree.getCategory("TechTree.techs")));
        elements.add(new ConfigElement(techTree.getCategory("TechTree.unlockables")));
        return elements;
    }
}
