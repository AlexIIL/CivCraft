package alexiil.mods.civ;

import java.util.Random;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import org.apache.logging.log4j.Logger;

import alexiil.mods.civ.block.CivBlocks;
import alexiil.mods.civ.compat.ModCompat;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.net.MessageHandler;
import alexiil.mods.civ.tech.BeakerEarningListener;
import alexiil.mods.lib.AlexIILMod;

@Mod(modid = Lib.Mod.ID, version = Lib.Mod.VERSION, guiFactory = "alexiil.mods.civ.gui.ConfigGuiFactory", useMetadata = true,
        dependencies = "required-after:alexiillib")
public class CivCraft extends AlexIILMod {
    public static ModMetadata modMeta;
    public static Logger log;
    // MOD STUFF
    @Instance(Lib.Mod.ID)
    public static CivCraft instance;
    @SidedProxy(clientSide = "alexiil.mods.civ.ClientProxy", serverSide = "alexiil.mods.civ.CommonProxy")
    public static CommonProxy proxy;
    public static final String chatString = "\u00A7";
    public static final Random RNG = new Random();

    /** Debug holder of the players NBT compound (so, only works when the server is in the same minecraft instance as the
     * client) */
    public static NBTTagCompound playerNBTData = new NBTTagCompound();

    @Override
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
        modMeta = super.meta;
        log = super.log;
        log.info("This is " + Lib.Mod.NAME + ", version " + modMeta.version + ", build type = " + Lib.Mod.buildType() + ", commit hash = "
            + Lib.Mod.COMMIT_HASH);

        MinecraftForge.EVENT_BUS.register(EventListner.instance);
        MinecraftForge.EVENT_BUS.register(BeakerEarningListener.instance);
        FMLCommonHandler.instance().bus().register(EventListner.instance);
        FMLCommonHandler.instance().bus().register(BeakerEarningListener.instance);
        provider = MessageHandler.instance;
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);

        tab = new CreativeTabs("civCraft") {
            @Override
            public Item getTabIconItem() {
                return CivItems.sciencePacks[0];
            }
        };

        CivConfig.init();
        CivItems.init();
        CivBlocks.init();

        ModCompat.loadCompats();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        CivRecipes.init();
        proxy.initRenderers();
    }

    @Override
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        cfg.saveAll();
    }

    @Override
    public String getCommitHash() {
        return Lib.Mod.COMMIT_HASH;
    }

    @Override
    public int getBuildType() {
        return Lib.Mod.buildType();
    }

    @Override
    public String getUser() {
        return "AlexIIL";
    }

    @Override
    public String getRepo() {
        return "CivCraft";
    }
}
