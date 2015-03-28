package alexiil.mods.lib;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

public class BlockPosUtils {
    public static BlockPos readFromNBT(NBTTagCompound nbt, String tagPart) {
        int[] arr = nbt.getIntArray(tagPart);
        return new BlockPos(arr[0], arr[1], arr[2]);
    }

    public static BlockPos readFromNBT(NBTTagCompound nbt) {
        return readFromNBT(nbt, "pos");
    }

    public static NBTTagCompound saveToNBT(BlockPos pos, String tagPart) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setIntArray(tagPart, new int[] { pos.getX(), pos.getY(), pos.getZ() });
        return nbt;
    }

    public static NBTTagCompound saveToNBT(BlockPos pos) {
        return saveToNBT(pos, "pos");
    }

    public static BlockPos readFromByteBuf(ByteBuf buffer) {
        return new BlockPos(buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public static void writeToByteBuf(ByteBuf buffer, BlockPos pos) {
        buffer.writeInt(pos.getX());
        buffer.writeInt(pos.getY());
        buffer.writeInt(pos.getZ());
    }
}
