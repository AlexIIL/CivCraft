package alexiil.mods.civ;

import java.util.Collections;
import java.util.List;
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

import alexiil.mods.civ.compat.ModCompat;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.net.MessageHandler;
import alexiil.mods.civ.tech.BeakerEarningListener;
import alexiil.mods.lib.AlexIILMod;
import alexiil.mods.lib.git.Commit;
import alexiil.mods.lib.git.GitHubRequester;
import alexiil.mods.lib.git.GitHubUser;
import alexiil.mods.lib.git.Release;

@Mod(modid = Lib.Mod.ID, version = Lib.Mod.VERSION, guiFactory = "alexiil.mods.civ.gui.ConfigGuiFactory") public class CivCraft extends AlexIILMod {
    public static ModMetadata modMeta;
    public static Logger log;
    // MOD STUFF
    @Instance(Lib.Mod.ID) public static CivCraft instance;
    @SidedProxy(clientSide = "alexiil.mods.civ.ClientProxy", serverSide = "alexiil.mods.civ.CommonProxy") public static CommonProxy proxy;
    public static final String chatString = "\u00A7";
    public static final Random RNG = new Random();
    
    /** Debug holder of the players NBT compound (so, only works when the server is in the same minecraft instance as the
     * client) */
    public static NBTTagCompound playerNBTData = new NBTTagCompound();
    
    private static List<GitHubUser> contributors;
    private static List<Commit> commits;
    private static List<Release> releases;
    private static Commit thisCommit;
    
    @EventHandler public void preInit(FMLPreInitializationEvent event) {
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
        
        // Pending forge
        tab = new CreativeTabs("civCraft") {
            @Override public Item getTabIconItem() {
                return CivItems.sciencePacks[0];
            }
        };
        
        CivConfig.init();
        CivItems.init();
        
        ModCompat.loadCompats();
    }
    
    @EventHandler public void init(FMLInitializationEvent event) {
        CivRecipes.init();
    }
    
    @EventHandler public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        CivTechs.loadTree();
        proxy.initRenderers();
        cfg.saveAll();
        
        initGithubRemote();
    }
    
    private void initGithubRemote() {
        new Thread("CivCraft-github") {
            @Override public void run() {
                contributors = Collections.unmodifiableList(GitHubRequester.getContributors("AlexIIL", "CivCraft"));
                if (contributors.size() == 0)
                    modMeta.authorList.add("Could not connect to GitHub to fetch the rest...");
                for (GitHubUser c : contributors) {
                    if ("AlexIIL".equals(c.login))
                        modMeta.authorList.remove("AlexIIL");
                    modMeta.authorList.add(c.login + " (" + c.commits + ")");
                }
                
                commits = Collections.unmodifiableList(GitHubRequester.getCommits("AlexIIL", "CivCraft"));
                for (Commit c : commits)
                    if (Lib.Mod.COMMIT_HASH.equals(c.sha))
                        thisCommit = c;
                if (thisCommit == null && commits.size() > 0 && Lib.Mod.buildType() == 2)
                    thisCommit = GitHubRequester.getCommit("AlexIIL", "CivCraft", Lib.Mod.COMMIT_HASH);
                
                releases = Collections.unmodifiableList(GitHubRequester.getReleases("AlexIIL", "CivCraft"));
            }
        }.start();
    }
    
    public static List<GitHubUser> getContributors() {
        return contributors;
    }
    
    public static List<Commit> getCommits() {
        return commits;
    }
    
    public static Commit getCurrentCommit() {
        return thisCommit;
    }
    
    public static List<Release> getReleases() {
        return releases;
    }
}
