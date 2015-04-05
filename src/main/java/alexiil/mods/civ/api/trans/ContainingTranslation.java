package alexiil.mods.civ.api.trans;

/** Simple implementation that will translate any string that contains the string with that string, appending the
 * {@link #end} */
public class ContainingTranslation implements IMatchingTranslation {
    private final String contains, prefix, postfix;

    public ContainingTranslation(String contains, String prefix, String postfix) {
        this.contains = contains;
        this.prefix = prefix;
        this.postfix = postfix;
    }

    @Override
    public boolean matches(String text) {
        return text.contains(contains);
    }

    @Override
    public String translate(String text) {
        return prefix + text.substring(text.indexOf(contains) + contains.length()) + postfix;
    }

    @Override
    public int compareTo(IMatchingTranslation o) {
        if (o instanceof ContainingTranslation)
            return ((ContainingTranslation) o).contains.compareTo(contains);
        return 0;
    }
}
