package alexiil.mods.civ.gui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.gui.ConfigGuiFactory.ActualConfig;
import alexiil.mods.lib.LangUtils;
import alexiil.mods.lib.git.Commit;
import alexiil.mods.lib.git.GitHubUser;

public class Config extends GuiScreen {
    private GitHubUserScrollingList contributors;
    private CommitScrollingList commits;
    
    public Config(GuiScreen screen) {
        fontRendererObj = Minecraft.getMinecraft().fontRendererObj;
        setupGui();
    }
    
    @Override public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        setupGui();
    }
    
    private void setupGui() {
        int height = 0;
        int maxHeight = fontRendererObj.FONT_HEIGHT;
        int width = 0;
        
        for (GitHubUser usr : CivCraft.getContributors()) {
            height += fontRendererObj.FONT_HEIGHT;
            width = Math.max(width, fontRendererObj.getStringWidth(usr.login + " (" + usr.commits + ")"));
        }
        
        contributors = new GitHubUserScrollingList(this, width + 40, this.height, 40, this.height - 40, 10, maxHeight);
        for (GitHubUser c : CivCraft.getContributors())
            contributors.userList.add(c);
        
        height = 0;
        maxHeight = 0;
        
        for (Commit c : CivCraft.getCommits()) {
            int h = fontRendererObj.FONT_HEIGHT * (c.commit.message.split("\n").length + 2);
            maxHeight = Math.max(maxHeight, h);
            height += h;
        }
        commits = new CommitScrollingList(this, this.width - width - 80, this.height, 40, this.height - 40, width + 60, maxHeight);
        for (Commit c : CivCraft.getCommits())
            commits.commitList.add(c);
    }
    
    @SuppressWarnings("unchecked") @Override public void initGui() {
        buttonList.add(new GuiButton(0, 1, 1, LangUtils.format("civcraft.config.button")));
    }
    
    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        commits.drawScreen(mouseX, mouseY, partialTicks);
        contributors.drawScreen(mouseX, mouseY, partialTicks);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
    
    @Override protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            Minecraft.getMinecraft().displayGuiScreen(new ActualConfig(this));
        }
    }
    
    public FontRenderer getFontRenderer() {
        return fontRendererObj;
    }
}