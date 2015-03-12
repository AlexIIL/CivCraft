package alexiil.mods.lib.coremod;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.potion.PotionEffect;
import alexiil.mods.lib.AlexIILLib;

public class VanillaMethods {
    private static Map<Integer, String> romanNumerals = new HashMap<Integer, String>();
    private static int[] romanImportance = new int[] { 5, 10, 50, 100, 500, 1000 };

    public static void init() {
        romanNumerals.put(1, "I");
        romanNumerals.put(5, "V");
        romanNumerals.put(10, "X");
        romanNumerals.put(50, "L");
        romanNumerals.put(100, "C");
        romanNumerals.put(500, "D");
        romanNumerals.put(1000, "M");
    }

    public static String getEnchantmentLevel(String preText, PotionEffect potionEffect) {
        if (potionEffect.getAmplifier() <= 4)
            return preText;
        if (AlexIILLib.betterPotions.getBoolean())
            return preText + " " + getRomanNumeral(potionEffect.getAmplifier() + 1);
        return preText;
    }

    public static String getRomanNumeral(int num) {
        if (num <= 0)
            return "";
        if (romanNumerals.containsKey(num))
            return romanNumerals.get(num);
        int lessThan = 1;
        for (int ri : romanImportance) {
            if (ri < num) {
                lessThan = ri;
                continue;
            }
            String toUse = romanNumerals.get(lessThan) + getRomanNumeral(num - lessThan);
            // System.out.println(num + " -> " + toUse);
            romanNumerals.put(num, toUse);
            return toUse;
        }
        return "M" + getRomanNumeral(num - 1000);
    }
}
