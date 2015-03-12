package alexiil.mods.lib.net;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;

public abstract class MessageBase<M extends MessageBase<?>> implements IMessage, IMessageHandler<M, IMessage> {
    protected NBTTagCompound readNBT(ByteBuf buf) {
        int len = buf.readInt();
        if (len <= 0)
            return null;
        byte[] bytes = buf.readBytes(len).array();
        try {
            return CompressedStreamTools.readCompressed(new ByteArrayInputStream(bytes));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected ByteBuf writeNBT(NBTTagCompound nbt, ByteBuf buf) {
        if (nbt == null) {
            buf.writeInt(-1);
            return buf;
        }
        byte[] bytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CompressedStreamTools.writeCompressed(nbt, baos);
            bytes = baos.toByteArray();
        }
        catch (IOException e) {
            e.printStackTrace();
            return buf;
        }
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        return buf;
    }
}
