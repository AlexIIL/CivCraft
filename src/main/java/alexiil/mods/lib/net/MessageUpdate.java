package alexiil.mods.lib.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import alexiil.mods.lib.AlexIILLib;
import alexiil.mods.lib.BlockPosUtils;

/** @param <M>
 *            The message type. should extend MessageBase, to provide custom functionality
 * @param <T>
 *            The tile that this message is referring to. */
public abstract class MessageUpdate<M extends MessageUpdate<?, ?>, T extends INetworkTile<M>> extends MessageBase<M> {
    public BlockPos pos;

    public MessageUpdate() {

    }

    public MessageUpdate(T tile) {
        pos = tile.getCor();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        pos = BlockPosUtils.readFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        BlockPosUtils.writeToByteBuf(buf, pos);
    }

    @Override
    public IMessage onMessage(M message, MessageContext ctx) {
        TileEntity tile = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.pos);

        if (tile == null) {
            AlexIILLib.instance.log.warn("A message's data could not be set because there was no tile to set it to!");
        }
        else if (tile instanceof INetworkTile) {
            @SuppressWarnings("unchecked")
            INetworkTile<M> q = (INetworkTile<M>) tile;
            q.setTileData(message);
        }
        else {
            AlexIILLib.instance.log.warn("A message's data could not be set because there was the tile was not of the expected type!");
        }
        return null;
    }
}
