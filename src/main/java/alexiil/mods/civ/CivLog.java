package alexiil.mods.civ;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class CivLog {
    public static Logger log = CivCraft.log;
    
    public static void debugInfo(String toLog) {
        if (CivConfig.debugMode.getBoolean())
            info(toLog);
    }
    
    public static void debugLevel(String text, Level level) {
        if (CivConfig.debugMode.getBoolean())
            log(text, level);
    }
    
    public static void info(String toLog) {
        log.info(toLog);
    }
    
    public static void warn(String text) {
        log.warn(text);
    }
    
    public static void log(String text, Level level) {
        log.log(level, text);
    }
    
    /** Use this for temporary logging, so eclipse helps see where every temporary logging statement is (so cleanup is
     * much easier) */
    @Deprecated public static void temp(String text) {
        info(text);
    }
}
