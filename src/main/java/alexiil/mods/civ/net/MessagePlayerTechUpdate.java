package alexiil.mods.civ.net;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.api.tech.TechTree;
import alexiil.mods.civ.api.tech.TechTree.Tech;
import alexiil.mods.civ.utils.TechUtils;
import alexiil.mods.lib.net.MessageBase;

public class MessagePlayerTechUpdate extends MessageBase<MessagePlayerTechUpdate> implements IMessage {
    private static int num = 0;
    private List<Tech> techs = new ArrayList<Tech>();
    private final int n = num++;

    public MessagePlayerTechUpdate() {}

    public MessagePlayerTechUpdate(EntityPlayer player) {
        techs = TechUtils.getTechs(player);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int length = buf.readInt();
        for (int i = 0; i < length; i++) {
            int len = buf.readInt();
            byte[] bytes = new byte[len];
            buf.readBytes(bytes);
            String s = new String(bytes);
            CivLog.debugInfo(n + "=" + len + "->" + Arrays.toString(bytes) + "=" + s);
            Tech t = TechTree.currentTree.getTech(s);
            if (t == null)
                CivLog.warn(n + "=" + "A tech \"" + s + "\" was not found in the tech tree! Is this a bug?");
            else
                techs.add(t);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(techs.size());
        for (Tech t : techs) {
            byte[] bytes = t.name.getBytes();
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);
            CivLog.debugInfo(n + "=" + bytes.length + "->" + Arrays.toString(bytes) + "=" + t.name);
        }
    }

    @Override
    public IMessage onMessage(MessagePlayerTechUpdate message, MessageContext ctx) {
        List<Tech> ts = message.techs;
        if (ctx.side == Side.CLIENT) {
            TechUtils.setClientTechs(ts);
            return null;
        }
        return new MessagePlayerTechUpdate(ctx.getServerHandler().playerEntity);
    }
}
