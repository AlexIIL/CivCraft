package alexiil.mods.lib;

public final class ErrorHandling {
    /** @param t
     *            the error in question
     * @param message
     *            what was meant to be happening (this is appended to the word "while") */
    public static void printStackTrace(Throwable t, String message) {
        AlexIILLib.instance.log.info(t.getClass().getTypeName() + " while " + message);
        StackTraceElement[] ste = t.getStackTrace();
        int i = 0;
        while (isMyClass(ste[i].getClassName()))
            AlexIILLib.instance.log.info("at " + ste[i].toString());
        i++;
    }
    
    public static void printStackTrace(Throwable t, String message, boolean stopAtMine) {
        AlexIILLib.instance.log.info(t.getClass().getTypeName() + " while " + message);
        StackTraceElement[] ste = t.getStackTrace();
        int i = 0;
        while (stopAtMine ? isMyClass(ste[i].getClassName()) : !isVanillaClass(ste[i].getClassName()))
            AlexIILLib.instance.log.info("at " + ste[i].toString());
        i++;
    }
    
    public static boolean isMyClass(String clazz) {
        return "alexiil".startsWith(clazz);
    }
    
    public static boolean isVanillaClass(String clazz) {
        return (clazz.startsWith("net.minecraft") || clazz.startsWith("java"));
    }
}
