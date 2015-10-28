package alexiil.mods.civ.tech;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

import alexiil.mods.civ.api.tech.ITechManager;
import alexiil.mods.civ.api.tech.ITechTree;
import alexiil.mods.civ.item.CivItems;

import scala.actors.threadpool.Arrays;

public enum TechManager implements ITechManager {
    INSTANCE;

    private static class Tech {
        int[] requirements;
        String[] dependencies;
        String[] researchItems;

        Tech() {
            requirements = new int[0];
            dependencies = new String[0];
            researchItems = new String[CivItems.sciencePacks.length];
            for (int i = 0; i < CivItems.sciencePacks.length; i++) {
                researchItems[i] = "civcraft:sciencePack" + i;
            }
        }
    }

    private static final Pattern PATTERN_TECH = Pattern.compile("[a-z0-9_]+");
    private static final Pattern PATTERN_UNLOCK = Pattern.compile("[a-z0-9_+]+");

    private Map<String, Tech> techs = Maps.newHashMap();

    private void validate(Pattern pattern, String name, String exception) throws InvalidArgumentException {
        Matcher match = pattern.matcher(name);
        if (!match.matches()) {
            throw new InvalidArgumentException(exception + ": \"" + name + "\" did not match the regular expression \"" + match.pattern().pattern()
                    + "\"");
        }
    }

    private void validateTechName(String name) throws InvalidArgumentException {
        validate(PATTERN_TECH, name, "Invalid tech name");
    }

    private void validateUnlockName(String name) throws InvalidArgumentException {
        validate(PATTERN_UNLOCK, name, "Invalid unlockable name");
    }

    @Override
    public void addTech(String name, int[] requirements, String[] dependancies) throws InvalidArgumentException {
        validateTechName(name);
        for (String dep : dependancies) {
            validateTechName(dep);
        }
        if (techs.containsKey(name)) {
            throw new InvalidArgumentException("A tech with the name \"" + name + "\" already exists!");
        }
        Tech t = new Tech();
        t.requirements = requirements;
        t.dependencies = dependancies;
        techs.put(name, t);
    }

    @Override
    public void removeTech(String name) throws InvalidArgumentException {
        validateTechName(name);
        if (!techs.containsKey(name)) {
            throw new InvalidArgumentException("Cannot remove a tech with the name \"" + name + "\" because no such tech exists!");
        }
        techs.remove(name);
    }

    @Override
    public void clearTechs() {
        techs.clear();
    }

    @Override
    public void addResearchRequirements(String techName, int[] requirements) throws InvalidArgumentException {
        validateTechName(techName);
        if (!techs.containsKey(techName)) {
            throw new InvalidArgumentException("Cannot add research requirements to a tech with the name \"" + techName
                    + "\" because no such tech exists!");
        }
        Tech t = techs.get(techName);
        if (t.requirements.length < requirements.length) {
            t.requirements = Arrays.copyOf(t.requirements, requirements.length);
        }
        for (int i = 0; i < requirements.length; i++) {
            if (requirements[i] < 0) {
                throw new InvalidArgumentException("The requirement " + requirements[i] + " was less than 0!");
            }
            t.requirements[i] += requirements[i];
        }
    }

    @Override
    public void subtractResearchRequirements(String techName, int[] requirements) throws InvalidArgumentException {
        validateTechName(techName);
        if (!techs.containsKey(techName)) {
            throw new InvalidArgumentException("Cannot subtract research requirements from a tech with the name \"" + techName
                    + "\" because no such tech exists!");
        }
        Tech t = techs.get(techName);
        if (t.requirements.length < requirements.length) {
            t.requirements = Arrays.copyOf(t.requirements, requirements.length);
        }
        for (int i = 0; i < requirements.length; i++) {
            if (requirements[i] < 0) {
                throw new InvalidArgumentException("The requirement " + requirements[i] + " was less than 0!");
            }
            if (t.requirements[i] - requirements[i] < 0) {
                throw new InvalidArgumentException("The requirement " + requirements[i] + " negated from " + t.requirements[i] + " was less than 0!");
            }
        }
        for (int i = 0; i < requirements.length; i++) {
            t.requirements[i] -= requirements[i];
        }
    }

    @Override
    public void setResearchRequirements(String techName, int[] requirements) throws InvalidArgumentException {
        validateTechName(techName);
        if (!techs.containsKey(techName)) {
            throw new InvalidArgumentException("Cannot set the research requirements of a tech with the name \"" + techName
                    + "\" because no such tech exists!");
        }
        for (int i = 0; i < requirements.length; i++) {
            if (requirements[i] < 0) {
                throw new InvalidArgumentException("The requirement " + requirements[i] + " was less than 0!");
            }
        }
        techs.get(techName).requirements = requirements;
    }

    @Override
    public void setResearchItems(String techName, String[] items) throws InvalidArgumentException {
        validateTechName(techName);
        // TODO Auto-generated method stub

    }

    @Override
    public void replaceResearchItems(String techName, String[] items) throws InvalidArgumentException {
        validateTechName(techName);
        // TODO Auto-generated method stub

    }

    @Override
    public void resetResearchItems(String techName) throws InvalidArgumentException {
        validateTechName(techName);
        // TODO Auto-generated method stub

    }

    @Override
    public void addTechDependencies(String techName, String[] dependancies) throws InvalidArgumentException {
        validateTechName(techName);
        // TODO Auto-generated method stub

    }

    @Override
    public void removeTechDependencies(String techName, String[] dependancies) throws InvalidArgumentException {
        validateTechName(techName);
        // TODO Auto-generated method stub

    }

    @Override
    public void setTechDependencies(String techName, String[] dependancies) throws InvalidArgumentException {
        validateTechName(techName);
        // TODO Auto-generated method stub

    }

    @Override
    public void addUnlockable(String name, String type, String[] dependencies, String[] typeArguments) throws InvalidArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearUnlockables() {
        // TODO Auto-generated method stub

    }

    @Override
    public void addUnlocakbleDependencies(String unlockName, String[] dependencies) throws InvalidArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeUnlocakbleDependencies(String unlockName, String[] dependencies) throws InvalidArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUnlocakbleDependencies(String unlockName, String[] dependencies) throws InvalidArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void addUnlockableArguments(String unlockName, String[] arguments) throws InvalidArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeUnlockableArguments(String unlockName, String[] arguments) throws InvalidArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUnlockableArguments(String unlockName, String[] arguments) throws InvalidArgumentException {
        // TODO Auto-generated method stub

    }

    @Override
    public ITechTree bake() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void reset() {
        clearTechs();
        clearUnlockables();
    }

}
