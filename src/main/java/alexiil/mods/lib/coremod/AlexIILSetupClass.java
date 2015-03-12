package alexiil.mods.lib.coremod;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLCallHook;

public class AlexIILSetupClass implements IFMLCallHook {
    @Override
    public Void call() throws Exception {
        System.out.println("hi?");
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }
}
