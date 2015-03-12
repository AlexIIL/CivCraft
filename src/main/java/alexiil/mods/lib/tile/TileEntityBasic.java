package alexiil.mods.lib.tile;

import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;

/** This is the base class for tile entity's */
public abstract class TileEntityBasic extends TileEntity implements IUpdatePlayerListBox {
    @Override
    public final void update() {
        if (worldObj.isRemote)
            onClientTick();
        else
            onTick();
    }

    public abstract void onTick();

    public void onClientTick() {

    }
}
