package alexiil.mods.civ;

import java.util.Collections;
import java.util.Comparator;
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

import alexiil.mods.civ.block.CivBlocks;
import alexiil.mods.civ.compat.ModCompat;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.net.MessageHandler;
import alexiil.mods.civ.tech.BeakerEarningListener;
import alexiil.mods.lib.AlexIILMod;
import alexiil.mods.lib.git.Commit;
import alexiil.mods.lib.git.GitHubRequester;
import alexiil.mods.lib.git.GitHubUser;
import alexiil.mods.lib.git.Release;

@Mod(modid = Lib.Mod.ID, version = Lib.Mod.VERSION, guiFactory = "alexiil.mods.civ.gui.ConfigGuiFactory")
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

    private static List<GitHubUser> contributors = Collections.emptyList();
    private static List<Commit> commits = Collections.emptyList();
    private static List<Release> releases = Collections.emptyList();
    private static Commit thisCommit;

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

        initGithubRemote();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        CivRecipes.init();
        proxy.initRenderers();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
        cfg.saveAll();
    }

    private void initGithubRemote() {
        if (!CivConfig.connectExternally.getBoolean())
            return;
        new Thread("CivCraft-github") {
            @Override
            public void run() {
                String droneSite = "https://drone.io/github.com/AlexIIL/CivCraft/files/VersionInfo/build/libs/version/";
                contributors = Collections.unmodifiableList(GitHubRequester.getContributors(droneSite + "contributors.json"));
                if (contributors.size() == 0)
                    modMeta.authorList.add("Could not connect to GitHub to fetch the rest...");
                for (GitHubUser c : contributors) {
                    if ("AlexIIL".equals(c.login))
                        continue;
                    modMeta.authorList.add(c.login);
                }

                commits = Collections.unmodifiableList(GitHubRequester.getCommits(droneSite + "commits.json"));
                Collections.sort(commits, new Comparator<Commit>() {
                    @Override
                    public int compare(Commit c0, Commit c1) {
                        return c1.commit.committer.date.compareTo(c0.commit.committer.date);
                    }
                });
                commits = Collections.unmodifiableList(commits);

                for (Commit c : commits)
                    if (Lib.Mod.COMMIT_HASH.equals(c.sha))
                        thisCommit = c;
                if (thisCommit == null && commits.size() > 0 && Lib.Mod.buildType() == 2) {
                    CivLog.warn("Didn't find my commit! This is unexpected, consider this a bug!");
                    CivLog.warn("Commit Hash : \"" + Lib.Mod.COMMIT_HASH + "\"");
                }

                releases = Collections.unmodifiableList(GitHubRequester.getReleases(droneSite + "releases.json"));
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
