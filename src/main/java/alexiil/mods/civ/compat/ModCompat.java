package alexiil.mods.civ.compat;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import alexiil.mods.civ.compat.vanilla.VanillaCompat;

public class ModCompat {
    public static void loadCompats() {
        MinecraftForge.EVENT_BUS.register(new VanillaCompat());

        if (Loader.isModLoaded("progressiveautomation"))
            MinecraftForge.EVENT_BUS.register(new ProgressiveAutomationCompat());

        if (Loader.isModLoaded("OpenComputers"))
            MinecraftForge.EVENT_BUS.register(new OpenComputersCompat());
        
        if (Loader.isModLoaded("BuildCraft|Core")) {
            
        }

        // ALWAYS the last one
        MinecraftForge.EVENT_BUS.register(new ConfigCompat());
    }
}
