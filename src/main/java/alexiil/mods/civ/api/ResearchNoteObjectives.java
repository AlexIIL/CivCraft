package alexiil.mods.civ.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.api.trans.ContainingTranslation;
import alexiil.mods.civ.api.trans.IMatchingTranslation;
import alexiil.mods.civ.api.trans.StartingTranslation;

/** This stores all of the objectives for research notes. Use this if you add any mobs which can be killed, or any
 * special blocks that you want to reward the player for */
public class ResearchNoteObjectives {
    private static Map<String, Double> objectives = new HashMap<String, Double>();
    private static List<IMatchingTranslation> preTranslations = new ArrayList<IMatchingTranslation>();
    private static List<IMatchingTranslation> postTranslations = new ArrayList<IMatchingTranslation>();

    static {
        putObjective("block", 0.001);
        putObjective("block.break", 0.002);
        putObjective("block.break.obsidian", 0.1);
        putObjective("block.break.oreGold", 0.005);
        putObjective("block.harvest", 0.1);
        putObjective("block.harvest.oreDiamond", 0.5);
        putObjective("block.harvest.oreCoal", 0.2);
        putObjective("block.harvest.oreEmerald", 1.5);
        putObjective("block.harvest.oreRedstone", 0.4);

        putObjective("entity", 0.01);
        putObjective("entity.attack", 0.03);
        putObjective("entity.arrowHit", 0.1);
        putObjective("entity.breed", 0.1);
        putObjective("entity.kill", 0.1);
        putObjective("entity.kill.Enderman", 1);
        putObjective("entity.kill.Skeleton", 0.2);
        putObjective("entity.kill.Spider", 0.2);
        putObjective("entity.kill.Zombie", 0.2);
        putObjective("entity.attack", 0.04);

        putObjective("craft", 0.1);

        putObjective("explore", 0.01);

        addPreTranslation(new StartingTranslation("craft", "civcraft.chat.earnBeaker.craft"));
        addPreTranslation(new StartingTranslation("block.break", "civcraft.chat.earnBeaker.block.break"));
        addPreTranslation(new StartingTranslation("block.harvest", "civcraft.chat.earnBeaker.block.harvest"));
        addPreTranslation(new StartingTranslation("block", "civcraft.chat.earnBeaker.block"));
        addPreTranslation(new StartingTranslation("entity.kill", "civcraft.chat.earnBeaker.entity.kill"));
        addPreTranslation(new StartingTranslation("entity.attack", "civcraft.chat.earnBeaker.entity.attack"));
        addPreTranslation(new StartingTranslation("entity", "civcraft.chat.earnBeaker.entity"));
        addPreTranslation(new StartingTranslation("explore", "civcraft.chat.earnBeaker.explore"));

        addPostTranslation(new StartingTranslation("explore", ""));
        addPostTranslation(new ContainingTranslation("tile.", ".name"));
        addPostTranslation(new ContainingTranslation("item.", ".name"));

    }

    /** Add an amount earned (out of PlayerResearchHelper.progressRequired, default is 3) for each time this objective is
     * called. It is recommended that you use a notation similar to "grandparent.parent.child" for the names */
    public static void putObjective(String name, double amount) {
        objectives.put(name, amount);
    }

    /** @param trans */
    public static void addPreTranslation(IMatchingTranslation trans) {
        preTranslations.add(trans);
    }

    /** @param trans */
    public static void addPostTranslation(IMatchingTranslation trans) {
        postTranslations.add(trans);
    }

    /** Gets the amount earned from this objective */
    public static double getAmount(String name) {
        if (objectives.containsKey(name))
            return objectives.get(name);
        return 0;
    }

    /** Gets the amount earned by this objective, or the first of its parent if it has any (so, if this was called with
     * entity.kill.bear and there wasn't an entry for bear, it would call entity.kill next and use that value) */
    public static double getAmountOrParent(String name) {
        String[] names = name.split("\\.");
        if (objectives.containsKey(name))
            return objectives.get(name);
        for (int i = names.length - 1; i >= 0; i--) {
            String n = "";
            for (int j = 0; j <= i; j++) {
                n += names[j];
                if (j != 0)
                    n += ".";
            }
            if (objectives.containsKey(n))
                return objectives.get(n);
        }
        return 0;
    }

    public static String getPreTranslation(String s) {
        for (IMatchingTranslation trans : preTranslations)
            if (trans.matches(s))
                return trans.translate(s);
        CivLog.warn("Could not pre-translate \"" + s + "\"");
        return s;
    }

    public static String getPostTranslation(String s) {
        for (IMatchingTranslation trans : postTranslations)
            if (trans.matches(s))
                return trans.translate(s);
        CivLog.warn("Could not post-translate \"" + s + "\"");
        return s;
    }
}
