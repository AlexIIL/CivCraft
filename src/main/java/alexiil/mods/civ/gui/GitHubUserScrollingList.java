package alexiil.mods.civ.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import alexiil.mods.lib.LangUtils;
import alexiil.mods.lib.git.GitHubUser;

public class GitHubUserScrollingList extends TextGuiScrollingList {
    public final List<GitHubUser> userList = new ArrayList<GitHubUser>();
    public final Config parent;
    
    public GitHubUserScrollingList(Config parent, int width, int height, int top, int bottom, int left) {
        super(Minecraft.getMinecraft(), width, height, top, bottom, left);
        this.parent = parent;
    }
    
    @Override
    protected int getSize() {
        return userList.size();
    }
    
    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        
    }
    
    @Override
    protected boolean isSelected(int index) {
        return false;
    }
    
    @Override
    protected void drawBackground() {
        
    }
    
    @Override
    protected void drawSlot(int index, int var2, int var3, int var4, Tessellator tess) {
        GitHubUser user = userList.get(index);
        String text = "civcraft.github." + user.login;
        String newText = LangUtils.format(text);
        if (text.equals(newText))
            text = user.login;
        else
            text = user.login + " (" + newText + ")";
        parent.drawString(parent.getFontRenderer(), text, left + 3, var3, 0xFFFFFF);
    }
}
