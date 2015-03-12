package alexiil.mods.lib.coremod;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.8")
@IFMLLoadingPlugin.TransformerExclusions({ "alexiil.mods.lib.coremod" })
@IFMLLoadingPlugin.SortingIndex(1001)
public class LoadPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { ClassTransformer.class.getName() };
    }

    @Override
    public String getModContainerClass() {
        return null;// "alexiil.mods.basicutils.coremod.AlexIILModContainer";
    }

    @Override
    public String getSetupClass() {
        return null;// "alexiil.mods.basicutils.coremod.AlexIILSetupClass";
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
