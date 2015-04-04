package alexiil.mods.civ.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;

import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.api.tech.TechTree;
import alexiil.mods.lib.net.MessageBase;

public class MessageTechTreeUpdate extends MessageBase<MessageTechTreeUpdate> {
    private ByteBuf stored = null;

    @Override
    public void fromBytes(ByteBuf buf) {
        CivLog.info("fromBytes()");
        int length = buf.readInt();
        stored = buf.readBytes(length);
        CivLog.info("stored = " + stored);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        CivLog.info("toBytes(), side == " + FMLCommonHandler.instance().getEffectiveSide());
        ByteBuf b = Unpooled.buffer();
        NBTTagCompound nbt = new NBTTagCompound();
        TechTree.currentTree.save(nbt);
        try {
            CompressedStreamTools.writeCompressed(nbt, new ByteBufOutputStream(b));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        int i = b.readableBytes();
        buf.writeInt(i);
        buf.writeBytes(b);
    }

    @Override
    public IMessage onMessage(MessageTechTreeUpdate message, MessageContext ctx) {
        CivLog.info("onMessage(), stored = " + message.stored);
        try {
            NBTTagCompound nbt = CompressedStreamTools.readCompressed(new DataInputStream(new ByteBufInputStream(message.stored)));
            TechTree.currentTree = new TechTree(nbt);
            CivLog.info("TechTree.currentTree == null ?" + (TechTree.currentTree == null));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
