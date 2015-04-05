package alexiil.mods.civ.api.trans;

/** Simple implementation that will translate all strings that start with the specified string, to the other specified
 * string */
public class StartingTranslation implements IMatchingTranslation {
    public final String start, translation;

    public StartingTranslation(String start, String translation) {
        this.start = start;
        this.translation = translation;
    }

    @Override
    public boolean matches(String text) {
        return text.startsWith(start);
    }

    @Override
    public String translate(String text) {
        return translation;
    }

    @Override
    public int compareTo(IMatchingTranslation o) {
        if (o instanceof StartingTranslation) {
            int comp = ((StartingTranslation) o).start.compareTo(start);
            if (comp != 0)
                return comp;
            return (o instanceof EqualTranslation) ? 1 : 0;
        }
        return 0;
    }
}
