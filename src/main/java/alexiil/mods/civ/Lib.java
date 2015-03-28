package alexiil.mods.civ;

public class Lib {
    public static class Mod {
        public static final String ID = "civcraft";
        public static final String NAME = "Civilization in Minecraft";
        public static final String VERSION = "@VERSION@";
        public static final String COMMIT_HASH = "@COMMIT_HASH@";

        public static int buildType() {
            if (COMMIT_HASH.startsWith("@"))
                return 0;
            if (COMMIT_HASH.startsWith("manual "))
                return 1;
            return 2;
        }
    }

    public static class Gui {
        public static final int TECH_TREE = 0;
        public static final int LAB = 1;
    }

    public static class NBT {
        public static final int BYTE = 1;
        public static final int SHORT = 2;
        public static final int INTEGER = 3;
        public static final int LONG = 4;
        public static final int FLOAT = 5;
        public static final int DOUBLE = 6;
        public static final int BYTE_ARRAY = 7;
        public static final int STRING = 8;
        public static final int LIST = 9;
        public static final int COMPOUND = 10;
        public static final int INT_ARRAY = 11;
    }
}
