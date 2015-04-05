package alexiil.mods.lib.net;

import net.minecraft.util.BlockPos;

public interface INetworkTile<M extends MessageUpdate<?, ?>> {
    M getCustomUpdateMessage();

    void setTileData(M message);

    BlockPos getCor();
}
