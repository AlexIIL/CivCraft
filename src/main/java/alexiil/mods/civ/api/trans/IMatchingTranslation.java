package alexiil.mods.civ.api.trans;

public interface IMatchingTranslation extends Comparable<IMatchingTranslation> {
    /** This should return true if this should translate the string */
    boolean matches(String text);

    /** This should return a string suitable for looking into a language file */
    String translate(String text);
}
