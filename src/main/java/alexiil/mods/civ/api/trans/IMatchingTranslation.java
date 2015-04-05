package alexiil.mods.civ.api.trans;

public interface IMatchingTranslation {
    /** This should return true if this can match the string to a */
    boolean matches(String text);

    /** This should return a string suitable for looking into a language file */
    String translate(String text);
}
