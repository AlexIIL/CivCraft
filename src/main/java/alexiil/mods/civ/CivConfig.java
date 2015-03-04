package alexiil.mods.civ;

import net.minecraftforge.common.config.Property;
import alexiil.mods.civ.tech.BeakerEarningListener;
import alexiil.mods.lib.ConfigAccess;

public class CivConfig {
    public static Property debugMode, cooldownMultiplier, cooldownAddition, cooldownDivision, cooldownTimeDivision, progressRequired,
            connectExternally;
    
    public static void init() {
        ConfigAccess cfg = CivCraft.instance.cfg;
        cooldownMultiplier = cfg.getProp("cooldownMultiplier", BeakerEarningListener.COOLDOWN_MULTIPLIER);
        cooldownMultiplier.comment = "What multiple of the current cooldown to multiply it by, whenever a beaker is partially earned";
        BeakerEarningListener.COOLDOWN_MULTIPLIER = cooldownMultiplier.getDouble();
        
        cooldownAddition = cfg.getProp("cooldownAddition", BeakerEarningListener.COOLDOWN_ADDITION);
        cooldownAddition.comment = "What to add to the current cooldown, whenever a beaker is partially earned";
        BeakerEarningListener.COOLDOWN_ADDITION = cooldownAddition.getDouble();
        
        cooldownDivision = cfg.getProp("cooldownDivision", BeakerEarningListener.COOLDOWN_DIVISION);
        cooldownDivision.comment = "What divisor to negate from the current cooldown, every tick";
        BeakerEarningListener.COOLDOWN_DIVISION = cooldownDivision.getDouble();
        
        cooldownTimeDivision = cfg.getProp("cooldownTimeDivision", BeakerEarningListener.COOLDOWN_TIME_DIVISON);
        cooldownTimeDivision.comment = "What division of the amount of time since it has been unlocked";
        BeakerEarningListener.COOLDOWN_TIME_DIVISON = cooldownTimeDivision.getDouble();
        
        progressRequired = cfg.getProp("progressRequired", BeakerEarningListener.PROGRESS_REQUIRED);
        progressRequired.comment = "How much progress is required to get a new set of research notes";
        BeakerEarningListener.PROGRESS_REQUIRED = progressRequired.getDouble();
        
        debugMode = cfg.getProp("debug", false);
        debugMode.comment = "Enables debug mode. Will be annoying if you do not want to debug!";
        
        connectExternally = cfg.getProp("connectExternally", true);
        connectExternally.comment = "Allows this mod to connect to github to fetch a list of contributors, commits and releases for this mod.";
        if (debugMode.getBoolean()) {}
    }
}