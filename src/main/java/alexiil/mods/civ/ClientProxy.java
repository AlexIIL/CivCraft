package alexiil.mods.civ;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import alexiil.mods.civ.gui.GuiTechTree;
import alexiil.mods.lib.block.BlockContainerBasic;
import alexiil.mods.lib.item.ItemBase;

public class ClientProxy extends CommonProxy {
    @Override
    public void initRenderers() {
        ItemBase.initModels();
        BlockContainerBasic.initModels();
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == Lib.Gui.TECH_TREE)
            return new GuiTechTree(player);
        // if (ID == Lib.Gui.LAB)
        // return new GuiLab(new ContainerLab((TileLab) world.getTileEntity(new BlockPos(x, y, z)), player));
        return null;
    }

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }
}
