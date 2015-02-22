package alexiil.mods.lib.net;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public interface INetworkProvider {
    SimpleNetworkWrapper get();
}
