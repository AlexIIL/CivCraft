package alexiil.mods.lib.net;

import net.minecraft.util.BlockPos;

public interface INetworkTile<M extends MessageUpdate<?, ?>> {
    public M getCustomUpdateMessage();

    public void setTileData(M message);

    public BlockPos getCor();
}
