package alexiil.mods.lib.git;

import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.lib.AlexIILLib;
import alexiil.mods.lib.LangUtils;

public abstract class BaseConfig extends GuiScreen {
    private GitHubUserScrollingList contributors;
    private CommitScrollingList commits;

    public BaseConfig(GuiScreen screen, String user, String repo) {
        fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
        setupGui();
    }

    @Override
    public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        setupGui();
    }

    protected void setupGui() {
        int width = 0;

        for (GitHubUser usr : CivCraft.getContributors()) {
            String text = "alexiil.utils.github." + usr.login;
            String newText = LangUtils.format(text);
            if (text.equals(newText))
                text = usr.login;
            else
                text = usr.login + " (" + newText + ")";
            width = Math.max(width, fontRendererObj.getStringWidth(text));
        }

        contributors = new GitHubUserScrollingList(this, width + 40, this.height, 40, this.height - 40, 10);
        for (GitHubUser c : CivCraft.getContributors())
            contributors.userList.add(c);

        commits = new CommitScrollingList(this, this.width - width - 80, this.height, 40, this.height - 40, width + 60);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);

        if (AlexIILLib.connectExternally.getBoolean()) {
            commits.drawScreen(mouseX, mouseY, partialTicks);
            contributors.drawScreen(mouseX, mouseY, partialTicks);
            drawString(fontRendererObj, LangUtils.format("civcraft.gui.contributors"), 8, 30, 0xFFFFFF);
            String text = LangUtils.format("alexiil.utils.gui.commits");
            drawString(fontRendererObj, text, this.width - fontRendererObj.getStringWidth(text) - 10, 30, 0xFFFFFF);
        }
        else {
            String text = LangUtils.format("alexiil.utils.gui.connectExternallyDisabled");
            int textWidth = fontRendererObj.getStringWidth(text);
            drawHoveringText(Collections.singletonList(text), (this.width - textWidth) / 2, this.height / 2);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public FontRenderer getFontRenderer() {
        return fontRendererObj;
    }
}
