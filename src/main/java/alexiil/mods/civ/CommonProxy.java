package alexiil.mods.civ;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy implements IGuiHandler {
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        // if (ID == Lib.Gui.LAB)
        // return new ContainerLab((TileLab) world.getTileEntity(new BlockPos(x, y, z)), player);
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        return null;
    }

    public void initRenderers() {}

    public Side getSide() {
        return Side.SERVER;
    }
}
