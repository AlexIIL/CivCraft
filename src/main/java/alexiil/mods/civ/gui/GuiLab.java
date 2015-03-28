package alexiil.mods.civ.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import alexiil.mods.civ.inventory.ContainerLab;

public class GuiLab extends GuiContainer {
    public final ContainerLab lab;

    public GuiLab(ContainerLab lab) {
        super(lab);
        this.lab = lab;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }
}
