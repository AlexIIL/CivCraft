package alexiil.mods.civ.net;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.lib.net.INetworkProvider;

public class MessageHandler implements INetworkProvider {
    public static SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(CivCraft.modMeta.modId);
    public static MessageHandler instance = new MessageHandler();

    static {
        INSTANCE.registerMessage(MessageTechTreeUpdate.class, MessageTechTreeUpdate.class, 0, Side.CLIENT);
        // INSTANCE.registerMessage(MessageTechTreeUpdate.class, MessageTechTreeUpdate.class, 1, Side.SERVER);
        INSTANCE.registerMessage(MessagePlayerTechUpdate.class, MessagePlayerTechUpdate.class, 2, Side.CLIENT);
        // INSTANCE.registerMessage(MessagePlayerTechUpdate.class, MessagePlayerTechUpdate.class, 3, Side.SERVER);
        INSTANCE.registerMessage(MessageResearchTech.class, MessageResearchTech.class, 4, Side.CLIENT);
        INSTANCE.registerMessage(MessageResearchTech.class, MessageResearchTech.class, 5, Side.SERVER);
    }

    @Override
    public SimpleNetworkWrapper get() {
        return INSTANCE;
    }
}
