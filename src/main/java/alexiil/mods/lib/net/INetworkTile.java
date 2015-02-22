package alexiil.mods.lib.net;

import alexiil.mods.lib.Coordinate;

public interface INetworkTile<M extends MessageUpdate<?, ?>> {
    public M getCustomUpdateMessage();
    
    public void setTileData(M message);
    
    public Coordinate getCor();
}
