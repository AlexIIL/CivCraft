package alexiil.mods.civ.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.gui.ConfigGuiFactory.ActualConfig;
import alexiil.mods.lib.LangUtils;
import alexiil.mods.lib.git.BaseConfig;

public class Config extends BaseConfig {
    private GuiButton helpClose;
    private boolean help = false;
    private int xPosHelp = 0;
    private List<List<String>> helpText;

    public Config(GuiScreen screen) {
        super(screen, CivCraft.instance);
    }

    protected void setupGui() {
        super.setupGui();
        int index = 0;
        int maxXPos = Math.min(this.width - xPosHelp, 400);

        helpText = new ArrayList<List<String>>();
        while (true) {
            String preTranslation = "civcraft.gui.config.help." + index;
            String text = LangUtils.format(preTranslation);
            if (preTranslation.equals(text))
                break;
            String[] strings1 = text.split("\n");
            for (int i = 0; i < strings1.length; i++) {
                String s = strings1[i];
                String nextLine = "";
                while (fontRendererObj.getStringWidth(s) > maxXPos && s != null) {
                    if (s.length() <= 10)
                        break;
                    nextLine = s.substring(s.length() - 1) + nextLine;
                    s = s.substring(0, s.length() - 1);
                }
                if (nextLine.length() > 0) {
                    strings1 = Arrays.copyOf(strings1, strings1.length + 1);
                    strings1[i] = s;
                    strings1[i + 1] = nextLine;
                }
            }
            String[] strings = strings1;
            helpText.add(Arrays.asList(strings));
            index++;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        String text = LangUtils.format("civcraft.config.button");
        int length = fontRendererObj.getStringWidth(text) + 20;
        int totalLength = 10;
        buttonList.add(new GuiButton(0, totalLength, 1, length, 20, text));
        totalLength += length;

        text = LangUtils.format("civcraft.config.tech.button");
        length = fontRendererObj.getStringWidth(text) + 20;
        buttonList.add(new GuiButton(1, totalLength, 1, length, 20, text));
        totalLength += length;

        text = LangUtils.format("civcraft.config.help");
        length = fontRendererObj.getStringWidth(text) + 20;
        buttonList.add(new GuiButton(2, totalLength, 1, length, 20, text));
        xPosHelp = totalLength;
        totalLength += length;

        text = LangUtils.format("civcraft.config.closeHelp");
        length = fontRendererObj.getStringWidth(text) + 20;
        helpClose = new GuiButton(3, totalLength, 1, length, 20, text);
        helpClose.visible = false;
        buttonList.add(helpClose);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        if (help) {
            int yHeight = 0;
            for (List<String> ss : helpText)
                yHeight += fontRendererObj.FONT_HEIGHT * (ss.size() + 1);
            drawGradientRect(xPosHelp, 40, xPosHelp + 440, yHeight + 80, 0xFF000000, 0xFF000000);
            int yPos = 60;
            for (List<String> strings : helpText) {
                drawHoveringText(strings, xPosHelp, yPos);
                yPos += (strings.size() + 2) * fontRendererObj.FONT_HEIGHT;
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            Minecraft.getMinecraft().displayGuiScreen(new ActualConfig(this));
        }
        if (button.id == 1) {
            Minecraft.getMinecraft().displayGuiScreen(new TechConfig(this));
        }
        if (button.id == 2) {
            help = true;
            helpClose.visible = true;
        }
        if (button.id == 3 && helpClose.visible) {
            help = false;
            helpClose.visible = false;
        }
    }
}
