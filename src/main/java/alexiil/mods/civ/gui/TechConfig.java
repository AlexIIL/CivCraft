package alexiil.mods.civ.gui;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.Configuration;

public class TechConfig extends GuiScreen {
    private final Config parent;

    public TechConfig(Config parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        final TechTreeConfig cfg = new TechTreeConfig(parent, new Configuration(new File("./config/civcraftDefaultTechTree.cfg")));
        Minecraft.getMinecraft().addScheduledTask(new Runnable() {
            @Override
            public void run() {
                Minecraft.getMinecraft().displayGuiScreen(cfg);
            }
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
