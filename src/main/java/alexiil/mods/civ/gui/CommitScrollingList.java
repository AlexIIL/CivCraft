package alexiil.mods.civ.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.lib.git.Commit;

public class CommitScrollingList extends GuiScrollingList {
    public final List<Commit> commitList = new ArrayList<Commit>();
    public final Config parent;
    
    public CommitScrollingList(Config parent, int width, int height, int top, int bottom, int left, int entryHeight) {
        super(Minecraft.getMinecraft(), width, height, top, bottom, left, entryHeight);
        this.parent = parent;
    }
    
    @Override protected int getSize() {
        return commitList.size();
    }
    
    @Override protected void elementClicked(int index, boolean doubleClick) {
        
    }
    
    @Override protected boolean isSelected(int index) {
        return false;
    }
    
    @Override protected void drawBackground() {
        
    }
    
    @Override protected void drawSlot(int index, int var2, int var3, int var4, Tessellator var5) {
        Commit c = commitList.get(index);
        boolean thisOne = c == CivCraft.getCurrentCommit();
        int colour = thisOne ? 0xFFDD49 : 0xFFFFFF;
        parent.getFontRenderer().drawString(c.author.login + " " + c.commit.committer.date.split("T")[0], left + 3, var3, colour);
        int offset = parent.getFontRenderer().FONT_HEIGHT;
        String message = commitList.get(index).commit.message;
        String[] strings = message.split("\n");
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            String nextLine = "";
            while (parent.getFontRenderer().getStringWidth(s) > this.listWidth - 20) {
                if (s.length() <= 10)
                    break;
                nextLine = s.substring(s.length() - 1) + nextLine;
                s = s.substring(0, s.length() - 1);
            }
            if (nextLine.length() > 0) {
                strings = Arrays.copyOf(strings, strings.length + 1);
                strings[i] = s;
                strings[i + 1] = nextLine;
            }
        }
        for (String s : strings) {
            parent.getFontRenderer().drawString("  " + s, left + 3, var3 + offset, colour);
            offset += parent.getFontRenderer().FONT_HEIGHT;
        }
    }
}
