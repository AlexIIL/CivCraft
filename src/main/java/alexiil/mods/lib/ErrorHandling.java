package alexiil.mods.lib;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import alexiil.mods.civ.CivLog;

public final class ErrorHandling {
    /** @param t
     *            the error in question
     * @param message
     *            what was meant to be happening (this is appended to the word "while") */
    public static void printStackTrace(Throwable t, String message) {
        AlexIILLib.instance.log.info(t.getClass().getName() + " while " + message);
        StackTraceElement[] ste = t.getStackTrace();
        int i = 0;
        while (isMyClass(ste[i].getClassName())) {
            AlexIILLib.instance.log.info("at " + ste[i].toString());
            i++;
        }
    }

    public static void printStackTrace(Throwable t, String message, boolean stopAtMine) {
        AlexIILLib.instance.log.info(t.getClass().getName() + " while " + message);
        StackTraceElement[] ste = t.getStackTrace();
        int i = 0;
        while (stopAtMine ? isMyClass(ste[i].getClassName()) : !isVanillaClass(ste[i].getClassName())) {
            AlexIILLib.instance.log.info("at " + ste[i].toString());
            i++;
        }
    }

    public static boolean isMyClass(String clazz) {
        return "alexiil".startsWith(clazz);
    }

    public static boolean isVanillaClass(String clazz) {
        return (clazz.startsWith("net.minecraft") || clazz.startsWith("java"));
    }

    public static void printClassInfo(String clsName) {
        CivLog.info("Showing info for " + clsName);
        try {
            Class<?> cls = Class.forName(clsName);

            Annotation[] annos = cls.getDeclaredAnnotations();
            for (Annotation an : annos)
                CivLog.info(an.toString());
            CivLog.info(cls.toString());

            Field[] flds = cls.getFields();
            CivLog.info("  Fields:");
            for (Field fld : flds) {
                CivLog.info(" ");
                annos = fld.getDeclaredAnnotations();
                for (Annotation an : annos)
                    CivLog.info("    " + an.toString());
                CivLog.info("    " + fld.toString());
                if (Modifier.isStatic(fld.getModifiers()) && Modifier.isPublic(fld.getModifiers())) {
                    try {
                        Object obj = fld.get(null);
                        CivLog.info("     = " + obj);
                    }
                    catch (IllegalArgumentException e) {
                        CivLog.info("      IllegalArgumentException was thrown! (" + e.getMessage() + ")");
                    }
                    catch (IllegalAccessException e) {
                        CivLog.info("      IllegalAccessException was thrown! (" + e.getMessage() + ")");
                    }
                    catch (Throwable t) {
                        CivLog.info("      Throwable was thrown (" + t.getMessage() + ")");
                    }
                }
            }

            Method[] methods = cls.getMethods();
            CivLog.info("  Methods:");
            for (Method meth : methods) {
                annos = meth.getDeclaredAnnotations();
                CivLog.info(" ");
                for (Annotation an : annos)
                    CivLog.info("    " + an.toString());
                CivLog.info("    " + meth.toString());
            }
        }
        catch (ClassNotFoundException e) {
            CivLog.info("The class was not found!");
        }
    }
}
