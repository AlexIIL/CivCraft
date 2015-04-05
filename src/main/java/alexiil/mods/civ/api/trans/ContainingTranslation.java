package alexiil.mods.civ.api.trans;

public class ContainingTranslation implements IMatchingTranslation {
    private final String contains, end;

    public ContainingTranslation(String contains, String translation) {
        this.contains = contains;
        this.end = translation;
    }

    @Override
    public boolean matches(String text) {
        return text.contains(contains);
    }

    @Override
    public String translate(String text) {
        return text.substring(text.indexOf(contains)) + end;
    }
}
