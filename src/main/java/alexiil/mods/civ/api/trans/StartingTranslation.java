package alexiil.mods.civ.api.trans;

public class StartingTranslation implements IMatchingTranslation {
    private final String start, translation;

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
}
