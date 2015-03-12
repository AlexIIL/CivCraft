package alexiil.mods.civ.compat.nei;

import alexiil.mods.civ.CivCraft;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;

public class NEIConfig implements IConfigureNEI {
    public NEIConfig() {
        CivCraft.log.info("Started loading NEI compat");
    }

    @Override
    public void loadConfig() {
        API.registerRecipeHandler(new TechCraftingHandler());
        API.registerUsageHandler(new TechCraftingHandler());
    }

    @Override
    public String getName() {
        return "civCraftNeiAddon";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }
}
