package alexiil.mods.civ.coremod;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@MCVersion("1.8")
@IFMLLoadingPlugin.TransformerExclusions({ "alexiil.mods.civ.coremod" })
@IFMLLoadingPlugin.SortingIndex(1001)
public class LoadPlugin implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "alexiil.mods.civ.coremod.CivCraftTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
