package alexiil.mods.lib;

public enum EChatColours {
    BlACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GREY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOE, WHITE;
    
    public static final String chatString = "\u00A7";
    
    public static EChatColours getFromInteger(int i) {
        return values()[i];
    }
    
    @Override public String toString() {
        return chatString + Integer.toHexString(ordinal());
    }
}
