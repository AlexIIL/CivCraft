package alexiil.mods.civ;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class CivLog {
    public static Logger log = CivCraft.log;
    private static Deque<String> stack = new ArrayDeque<String>();
    private static String stackCache = "";

    public static void pushStack(String newInfo) {
        stack.push(newInfo);
        workOutStack();
    }

    public static void popStack() {
        if (!stack.isEmpty())
            stack.pop();
        workOutStack();
    }

    private static void workOutStack() {
        StringBuilder builder = new StringBuilder();
        for (String s : stack.toArray(new String[0])) {
            builder.append(s);
            builder.append(" | ");
        }
        stackCache = builder.toString();
    }

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
        log.log(level, stackCache + text);
    }

    /** Use this for temporary logging, so eclipse helps see where every temporary logging statement is (so cleanup is
     * much easier) */
    @Deprecated
    public static void temp(String text) {
        info(text);
    }
}
