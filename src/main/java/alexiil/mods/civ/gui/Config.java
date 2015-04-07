package alexiil.mods.civ.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.lib.LangUtils;
import alexiil.mods.lib.gui.BaseConfig;

public class Config extends BaseConfig {
    public Config(GuiScreen screen) {
        super(screen, CivCraft.instance);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        super.initGui();
        String text = LangUtils.format("civcraft.config.tech.button");
        int length = fontRendererObj.getStringWidth(text) + 20;
        buttonList.add(new GuiButton(3, totalLength, 1, length, 20, text));
        totalLength += length;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 3) {
            Minecraft.getMinecraft().displayGuiScreen(new TechConfig(this));
        }
    }
}
