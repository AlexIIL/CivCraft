package alexiil.mods.civ.compat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import alexiil.mods.civ.compat.vanilla.VanillaCompat;

public class ModCompat {
    public static void loadCompats() {
        // The first one, unless someone has added their own into it
        MinecraftForge.EVENT_BUS.register(new VanillaCompat());

        if (Loader.isModLoaded("progressiveautomation"))
            MinecraftForge.EVENT_BUS.register(new ProgressiveAutomationCompat());

        if (Loader.isModLoaded("OpenComputers"))
            MinecraftForge.EVENT_BUS.register(new OpenComputersCompat());

        // ALWAYS the last one
        MinecraftForge.EVENT_BUS.register(new ConfigCompat());
    }
}
