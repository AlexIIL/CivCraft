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
import net.minecraftforge.fml.relauncher.Side;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.lib.net.MessageBase;

public class MessageTechTreeUpdate extends MessageBase<MessageTechTreeUpdate> {
    private ByteBuf stored = null;

    @Override
    public void fromBytes(ByteBuf buf) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            int length = buf.readInt();
            stored = buf.readBytes(length);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
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
    }

    @Override
    public IMessage onMessage(MessageTechTreeUpdate message, MessageContext ctx) {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            TechTree.currentTree = new TechTree();
            try {
                NBTTagCompound nbt = CompressedStreamTools.read(new DataInputStream(new ByteBufInputStream(stored)));
                TechTree.currentTree.init(nbt);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        return new MessageTechTreeUpdate();
    }
}
