package alexiil.mods.civ.api.tech;

public interface ITechManager {
    public static class InvalidArgumentException extends Exception {
        private static final long serialVersionUID = -2797272073076724695L;

        public InvalidArgumentException(String message) {
            super(message);
        }
    }

    // TECH BASE
    void addTech(String name, int[] requirements, String[] dependancies) throws InvalidArgumentException;

    void removeTech(String name) throws InvalidArgumentException;

    void clearTechs();

    // RESEARCH COSTS (TECH)
    void addResearchRequirements(String techName, int[] requirements) throws InvalidArgumentException;

    void subtractResearchRequirements(String techName, int[] requirements) throws InvalidArgumentException;

    void setResearchRequirements(String techName, int[] requirements) throws InvalidArgumentException;

    // RESEARCH ITEMS (TECH)
    void setResearchItems(String techName, String[] items) throws InvalidArgumentException;

    void replaceResearchItems(String techName, String[] items) throws InvalidArgumentException;

    void resetResearchItems(String techName) throws InvalidArgumentException;

    // DEPENDENCIES (TECH)
    void addTechDependencies(String techName, String[] dependancies) throws InvalidArgumentException;

    void removeTechDependencies(String techName, String[] dependancies) throws InvalidArgumentException;

    void setTechDependencies(String techName, String[] dependancies) throws InvalidArgumentException;

    // UNLOCKABLE BASE
    void addUnlockable(String name, String type, String[] dependencies, String[] typeArguments) throws InvalidArgumentException;

    void clearUnlockables();

    // DEPENDENCIES (UNLOCAKBLES)
    void addUnlocakbleDependencies(String unlockName, String[] dependencies) throws InvalidArgumentException;

    void removeUnlocakbleDependencies(String unlockName, String[] dependencies) throws InvalidArgumentException;

    void setUnlocakbleDependencies(String unlockName, String[] dependencies) throws InvalidArgumentException;

    // GENERIC MODIFIERS (UNLOCKABLES)

    void addUnlockableArguments(String unlockName, String[] arguments) throws InvalidArgumentException;

    void removeUnlockableArguments(String unlockName, String[] arguments) throws InvalidArgumentException;

    void setUnlockableArguments(String unlockName, String[] arguments) throws InvalidArgumentException;

    // BAKING
    ITechTree bake();

    void reset();
}
