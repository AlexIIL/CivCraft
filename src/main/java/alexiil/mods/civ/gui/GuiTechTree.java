package alexiil.mods.civ.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import alexiil.mods.civ.Lib;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.item.ItemTechBag;
import alexiil.mods.civ.item.ItemTechBag.TechProgress;
import alexiil.mods.civ.item.ItemTechnology;
import alexiil.mods.civ.item.ItemTechnology.EResearchState;
import alexiil.mods.civ.net.MessageHandler;
import alexiil.mods.civ.net.MessageResearchTech;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.utils.TechUtils;

public class GuiTechTree extends GuiScreen {
    public class DrawTechInfo {
        public final int state;
        public final int leaf;
        public final int posX;
        public final int posY;
        public final String name;
        public final ItemStack item;
        
        public DrawTechInfo(int state, int leaf, int posX, int posY, String name, ItemStack item) {
            this.state = state;
            this.leaf = leaf;
            this.posX = posX;
            this.posY = posY;
            this.name = name;
            this.item = item;
        }
        
        public DrawTechInfo getSelected() {
            return new DrawTechInfo(selectedState, leaf, posX, posY, name, item);
        }
        
        @SuppressWarnings("unchecked") public void draw() {
            int texX = (state % techsPerRow) * gapX;
            int texY = (state / techsPerRow) * gapY + (leaf > 0 ? techTexSizeY : 0);
            int posY = this.posY;
            mc.getTextureManager().bindTexture(techTree);
            if (leaf > 0)
                posY += leaf * techTexSizeY;
            drawTexturedModalRect(posX + startX, posY + startY, texX, texY, techTexSizeX, techTexSizeY);
            
            if (name == null || name.length() == 0 || state == selectedState)
                return;
            
            boolean changedFlag = false;
            String name = this.name;
            while (fontRendererObj.getStringWidth(name) > 56) {
                name = name.substring(0, name.length() - 1);
                changedFlag = true;
            }
            if (changedFlag)
                name += "...";
            drawString(fontRendererObj, name, posX + startX + 22, posY + startY + 7, 0xFFFFFF);
            mouseX -= startX;
            mouseY -= startY;
            
            if (mouseX >= posX && mouseX < posX + techTexSizeX)
                if (mouseY >= posY && mouseY < posY + techTexSizeY) {
                    List<String> lines;
                    if (item != null)
                        lines = item.getTooltip(player, false);
                    else {
                        lines = new ArrayList<String>();
                        lines.add(this.name);
                    }
                    toolTip = lines;
                }
            
            mouseX += startX;
            mouseY += startY;
            
        }
    }
    
    public class DrawLineInfo {
        public final int xStart, yStart, length;
        public final boolean isHorizontal, drawArrow;
        
        public DrawLineInfo(int x, int y, int l, boolean horiz, boolean arrow) {
            xStart = x;
            yStart = y;
            length = l;
            isHorizontal = horiz;
            drawArrow = arrow;
        }
        
        public void draw(boolean selected, boolean unlocked) {
            int colour = selected ? 0xFF00FF : (unlocked ? 0x00BB00 : 0x005500);
            colour = 0xFFFFFF - colour;
            colour *= -1;
            if (isHorizontal)
                GuiTechTree.this.drawHorizontalLine(startX + xStart, startX + xStart + length, startY + yStart, colour);
            else
                GuiTechTree.this.drawVerticalLine(startX + xStart, startY + yStart, startY + yStart + length, colour);
            if (drawArrow)
                GuiTechTree.this.drawTexturedModalRect(startX + xStart + length - 7, startY + yStart - 4, 114, 234, 7, 11);
        }
    }
    
    public class DrawConnectionInfo {
        public final DrawTechInfo parent, child;
        public final List<DrawLineInfo> lines;
        
        public DrawConnectionInfo(DrawTechInfo parent, DrawTechInfo child, int[][] bars) {
            this.parent = parent;
            this.child = child;
            lines = new ArrayList<DrawLineInfo>();
            int x = bars[0][0];
            int y = bars[0][1];
            for (int[] points : bars) {
                if (x == points[0])
                    continue;
                lines.add(new DrawLineInfo(x, y, points[0] - x, true, false));
                x = points[0];
                lines.add(new DrawLineInfo(x, y, points[1] - y, false, false));
                y = points[1];
            }
            lines.add(new DrawLineInfo(x, y, child.posX - x, true, true));
        }
        
        public boolean isCurrentlySelected() {
            return parent == GuiTechTree.this.selected || child == GuiTechTree.this.selected;
        }
        
        public void draw() {
            boolean selected = isCurrentlySelected();
            boolean unlocked = parent.state == finishedState || child.state == finishedState;
            for (DrawLineInfo dli : lines)
                dli.draw(selected, unlocked);
        }
    }
    
    public static final ResourceLocation achievementPage = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    public static final int achieveTexX = 256;
    public static final int achieveTexY = 202;
    
    public static final ResourceLocation techTree = new ResourceLocation(Lib.Mod.ID + ":textures/gui/techtree.png");
    public static final int techTexSizeX = 85;
    public static final int techTexSizeY = 22;
    public static final int gapX = techTexSizeX;
    public static final int gapY = techTexSizeY * 2;
    public static final int techsPerRow = 3;
    
    public static final ResourceLocation widgets = new ResourceLocation("textures/gui/widgets.png");
    public static final int widgetHotbarSizeX = 182;
    public static final int widgetHotbarSizeY = 22;
    
    public static final int finishedState = 0;
    public static final int partialState = 1;
    public static final int availableState = 2;
    public static final int lockedState = 3;
    public static final int selectedState = 4;
    
    public static GuiTechTree currentGui = null;
    
    public final EntityPlayer player;
    
    private int startX = 0, startY = 0, mouseX, mouseY;
    private List<String> toolTip = null;
    private List<DrawTechInfo> techInfos;
    private List<DrawConnectionInfo> connectionInfo;
    private DrawTechInfo selected = null, selectedCache = null;
    private final List<Tech> playerTechs;
    private final ItemStack itemTechBag;
    private int needsRecalculating = 0;
    
    public GuiTechTree(EntityPlayer player) {
        currentGui = this;
        this.player = player;
        playerTechs = TechUtils.getTechs(player);
        ItemStack item = player.getCurrentEquippedItem();
        if (item == null || !(item.getItem() instanceof ItemTechBag))
            itemTechBag = null;
        else
            itemTechBag = item;
        recalculateTechs();
    }
    
    @Override public void setWorldAndResolution(Minecraft mc, int width, int height) {
        super.setWorldAndResolution(mc, width, height);
        calculateTechs();
    }
    
    public void calculateTechs() {
        needsRecalculating = 10;
    }
    
    private void recalculateTechs() {
        if (needsRecalculating > 0)
            needsRecalculating--;
        techInfos = new ArrayList<DrawTechInfo>();
        
        List<List<Tech>> techList = new ArrayList<List<Tech>>();
        Map<Tech, Integer> tempMap = new HashMap<Tech, Integer>();
        
        TechTree tree = TechTree.currentTree;
        techList.add(new ArrayList<Tech>());
        for (Tech t : tree.getTechs().values()) {
            if (t.isLeafTech())
                continue;
            techList.get(0).add(t);
            tempMap.put(t, 0);
        }
        boolean isDirty = true;
        
        int ttl = 1000;
        while (isDirty && ttl > 0) {
            isDirty = false;
            for (int lstIdx = 0; lstIdx < techList.size(); lstIdx++) {
                List<Tech> techs = techList.get(lstIdx);
                for (int techIndex = 0; techIndex < techs.size(); techIndex++) {
                    Tech t = techs.get(techIndex);
                    for (Tech child : t.getChildTechs()) {
                        if (child.isLeafTech())
                            continue;
                        int idx = tempMap.get(child);
                        int newIndex = tempMap.get(t);
                        if (idx <= newIndex) {
                            tempMap.put(child, newIndex + 1);
                            while (techList.size() <= newIndex + 1)
                                techList.add(new ArrayList<Tech>());
                            List<Tech> nextList = techList.get(newIndex + 1);
                            nextList.add(child);
                            List<Tech> prevList = techList.get(idx);
                            prevList.remove(child);
                            isDirty = true;
                        }
                    }
                }
            }
            ttl--;
        }
        
        // Order all of the lists into an order for nicer displaying
        // Firstly, in alphabetical order, to try and get a general trend
        for (List<Tech> lst : techList)
            lst.sort(new Comparator<Tech>() {
                @Override public int compare(Tech t1, Tech t2) {
                    return t1.name.compareTo(t2.name);
                }
            });
        
        // Next, try and group tech parents and children, only moving the children around
        List<Tech> lastList;
        List<Tech> nextList;
        for (int idx = 1; idx < techList.size(); idx++) {
            lastList = techList.get(idx - 1);
            if (techList.size() > idx + 1)
                nextList = techList.get(idx + 1);
            else
                nextList = null;
            
            // TODO: stuffs here
        }
        
        // Lastly, calculate the positions of all the techs, so that we don't have to every rendering tick
        Map<DrawTechInfo, Tech> tempDrawMap = new HashMap<DrawTechInfo, Tech>();
        Map<Tech, DrawTechInfo> tempDrawMapOther = new HashMap<Tech, DrawTechInfo>();
        ItemTechnology techItem = CivItems.technology;
        
        int xPos = 0;
        for (List<Tech> lst : techList) {
            int num = lst.size();
            int yPos = height / 2 - num * techTexSizeY;
            for (int idx = 0; idx < num; idx++) {
                Tech tech = lst.get(idx);
                ItemStack item = techItem.getItemForTech(getProgress(tech));
                DrawTechInfo dti = new DrawTechInfo(getState(tech), 0, xPos, yPos, tech.getLocalizedName(), item);
                techInfos.add(dti);
                tempDrawMap.put(dti, tech);
                tempDrawMapOther.put(tech, dti);
                List<Tech> leafs = new ArrayList<Tech>();
                for (Tech pl : tech.getChildTechs())
                    if (pl.isLeafTech())
                        leafs.add(pl);
                for (Tech leaf : leafs) {
                    item = techItem.getItemForTech(getProgress(leaf));
                    dti = new DrawTechInfo(getState(leaf), leafs.indexOf(leaf) + 1, xPos, yPos, leaf.getLocalizedName(), item);
                    techInfos.add(dti);
                    tempDrawMap.put(dti, leaf);
                    tempDrawMapOther.put(leaf, dti);
                }
                yPos += techTexSizeY * (2 + leafs.size());
            }
            xPos += techTexSizeX * (3 / 2F);
        }
        
        // And the positions of all the connections
        connectionInfo = new ArrayList<DrawConnectionInfo>();
        for (DrawTechInfo dti : techInfos) {
            Tech t = tempDrawMap.get(dti);
            for (Tech child : t.getChildTechs()) {
                if (child.isLeafTech())
                    continue;
                
                // Parent stuffs
                int totalTechs = techList.get(tempMap.get(t)).size();
                int pos = techList.get(tempMap.get(t)).indexOf(t);
                int parentXDiff = pos * 2;
                parentXDiff -= totalTechs;
                
                // ChildStuffs
                DrawTechInfo dtiChild = tempDrawMapOther.get(child);
                totalTechs = techList.get(tempMap.get(child)).size();
                pos = techList.get(tempMap.get(child)).indexOf(child);
                int xDiff = pos * 2;
                xDiff -= totalTechs;
                
                int[] coord1 = new int[] { dti.posX + techTexSizeX, dti.posY + techTexSizeY / 2 + parentXDiff * 2 + 2 };
                int[] coord2 = new int[] { dtiChild.posX - techTexSizeX / 4 + xDiff * 2, dtiChild.posY + techTexSizeY / 2 + xDiff };
                int[][] arr = new int[][] { coord1, coord2 };
                connectionInfo.add(new DrawConnectionInfo(dti, dtiChild, arr));
            }
        }
    }
    
    private int getState(Tech t) {
        if (playerTechs.contains(t))
            return finishedState;
        boolean hasAll = true;
        if (itemTechBag != null) {
            TechProgress tp = getProgress(t);
            if (tp != null && tp.state == EResearchState.RESEARCHING)
                return partialState;
        }
        for (Tech p : t.getParentTechs())
            if (!playerTechs.contains(p))
                hasAll = false;
        
        return hasAll ? availableState : lockedState;
    }
    
    private TechProgress getProgress(Tech t) {
        if (itemTechBag != null) {
            for (TechProgress tp : CivItems.techBag.getTechs(itemTechBag))
                if (tp.tech == t)
                    return tp;
        }
        return null;
    }
    
    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (needsRecalculating > 0)
            recalculateTechs();
        toolTip = null;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        
        drawBackground(0);
        
        for (DrawTechInfo dti : techInfos)
            dti.draw();
        if (selectedCache != null)
            selectedCache.draw();
        
        mc.getTextureManager().bindTexture(achievementPage);
        
        for (DrawConnectionInfo dci : connectionInfo)
            if (!dci.isCurrentlySelected())
                dci.draw();
        
        for (DrawConnectionInfo dci : connectionInfo)
            if (dci.isCurrentlySelected())
                dci.draw();
        
        mc.getTextureManager().bindTexture(widgets);
        
        GlStateManager.color(1F, 1F, 1F);
        
        // Draw the hotbar
        drawTexturedModalRect((width - widgetHotbarSizeX) / 2, height - widgetHotbarSizeY, 0, 0, widgetHotbarSizeX, widgetHotbarSizeY);
        int x = width / 2 - widgetHotbarSizeX / 2 + 3;
        int y = height - 19;
        for (int i = 0; i < CivItems.sciencePacks.length && i < 9; i++) {
            int num = 0;
            for (ItemStack stack : player.inventory.mainInventory)
                if (stack != null && stack.getItem() == CivItems.sciencePacks[i])
                    num += stack.stackSize;
            ItemStack stack = new ItemStack(CivItems.sciencePacks[i], num);
            itemRender.renderItemAndEffectIntoGUI(stack, x, y);
            itemRender.renderItemOverlayIntoGUI(fontRendererObj, stack, x, y, null);
            x += 20;
        }
        
        if (toolTip != null)
            drawHoveringText(toolTip, mouseX, mouseY);
        
        List<String> help = new ArrayList<String>();
        help.add("Left click a technology to select it, and show it's dependencies");
        help.add("Right click a technology to put as many science packs into researching it as possible");
        drawHoveringText(help, 10, 20);
    }
    
    @Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        mouseX -= startX;
        mouseY -= startY;
        
        boolean hasSelected = false;
        for (DrawTechInfo techInfo : techInfos) {
            int altY = techInfo.posY + techInfo.leaf * techTexSizeY;
            if (mouseX >= techInfo.posX && mouseX < techInfo.posX + techTexSizeX)
                if (mouseY >= altY && mouseY < altY + techTexSizeY) {
                    if (mouseButton == 0) {
                        hasSelected = true;
                        selected = techInfo;
                        selectedCache = selected.getSelected();
                    }
                    else if (mouseButton == 1) {
                        MessageHandler.INSTANCE.sendToServer(new MessageResearchTech(CivItems.technology.getTech(techInfo.item)));
                    }
                }
        }
        if (!hasSelected && mouseButton == 0) {
            selected = null;
            selectedCache = null;
        }
        
        mouseX += startX;
        mouseY += startY;
    }
    
    @Override protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        int kDiff = 4;
        if (keyCode == Keyboard.KEY_W || keyCode == Keyboard.KEY_UP)
            startY += kDiff;
        if (keyCode == Keyboard.KEY_A || keyCode == Keyboard.KEY_LEFT)
            startX += kDiff;
        if (keyCode == Keyboard.KEY_S || keyCode == Keyboard.KEY_DOWN)
            startY -= kDiff;
        if (keyCode == Keyboard.KEY_D || keyCode == Keyboard.KEY_RIGHT)
            startX -= kDiff;
    }
}
