package alexiil.mods.lib;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class Coordinate extends BlockPos {
    public Coordinate(int x, int y, int z) {
        super(x, y, z);
    }

    public Coordinate(TileEntity tile) {
        super(tile.getPos());
    }

    public Coordinate(Entity ent) {
        super(ent.posX, ent.posY, ent.posZ);
    }

    public Coordinate(NBTTagCompound tag, String tagPart) {
        super(tag.getIntArray(tagPart)[0], tag.getIntArray(tagPart)[1], tag.getIntArray(tagPart)[2]);
    }

    public Coordinate(BlockPos pos) {
        super(pos.getX(), pos.getY(), pos.getZ());
    }

    public Coordinate(ByteBuf buf) {
        super(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public Coordinate moveCoordinate(EnumFacing side) {
        int x = side.getFrontOffsetX();
        int y = side.getFrontOffsetY();
        int z = side.getFrontOffsetZ();
        return new Coordinate(x, y, z);
    }

    @Override
    public String toString() {
        return "(" + getX() + "," + getY() + "," + getZ() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BlockPos))
            return false;
        BlockPos c = (BlockPos) o;
        return (c.getX() == getX() && c.getY() == getY() && c.getZ() == getZ());
    }

    public void writeToNBT(NBTTagCompound nbt, String loc) {
        nbt.setIntArray("Coordinate_" + loc, new int[] { getX(), getY(), getZ() });
    }

    public void writeToByteBuf(ByteBuf buf) {
        buf.writeInt(getX());
        buf.writeInt(getY());
        buf.writeInt(getZ());
    }

    /** @param r
     *            the radius
     * @param o
     *            the offset
     * 
     * @return */
    public AxisAlignedBB getBB(double r, double o) {
        return AxisAlignedBB.fromBounds(getX() + o - r, getY() + o - r, getZ() + o - r, getX() + o + r, getY() + o + r, getZ() + o + r);
    }

    public AxisAlignedBB getBB(double r) {
        return getBB(r, 0);
    }
}
