package alexiil.mods.civ;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CivLog {
    private static Logger log;

    public static void debugInfo(String toLog) {
        if (CivConfig.debugMode.getBoolean())
            info(toLog);
    }

    public static void debugLevel(String text, Level level) {
        if (CivConfig.debugMode.getBoolean())
            log(text, level);
    }

    public static void info(String toLog) {
        log(toLog, Level.INFO);
    }

    public static void warn(String text) {
        log(text, Level.WARN);
    }

    public static void log(String text, Level level) {
        log().log(level, text);
    }

    private static Logger log() {
        if (log == null)
            log = LogManager.getLogger("civcraft");
        return log;
    }

    /** Use this for temporary logging, so eclipse helps see where every temporary logging statement is (so cleanup is
     * much easier) */
    @Deprecated
    public static void temp(String text) {
        info(text);
    }
}
