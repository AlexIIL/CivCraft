package alexiil.mods.civ.api.trans;

/** Simple implementation that will only translate strings that equal the given one */
public class EqualTranslation extends StartingTranslation {
    public EqualTranslation(String start, String translation) {
        super(start, translation);
    }

    @Override
    public boolean matches(String text) {
        return text.equals(start);
    }
}
