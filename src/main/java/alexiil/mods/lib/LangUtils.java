package alexiil.mods.lib;

public class LangUtils {
    public static String format(String toFormat, Object... objects) {
        return AlexIILLib.instance.format(toFormat, objects);
    }
}
