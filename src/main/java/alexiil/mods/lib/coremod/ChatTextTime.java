package alexiil.mods.lib.coremod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.config.Property;
import alexiil.mods.lib.AlexIILLib;

public class ChatTextTime {
    private static Property doSecs, doSecsWhileMin, doTimeChar, preTimeChar, postTimeChar;

    public static void init() {
        doSecs = AlexIILLib.instance.cfg.getProp("textTime.doSecs.enabled", "true");
        doSecsWhileMin = AlexIILLib.instance.cfg.getProp("textTime.doSecs.whileMin", "true");
        doTimeChar = AlexIILLib.instance.cfg.getProp("textTime.timeChar.enabled", "true");
        preTimeChar = AlexIILLib.instance.cfg.getProp("textTime.timeChar.pre", "<");
        postTimeChar = AlexIILLib.instance.cfg.getProp("textTime.timeChar.post", ">");

    }

    private static String getUnformatedText(IChatComponent chat) {
        String formatted = chat.getFormattedText();
        String unformatted = "";
        for (int i = 0; i < formatted.length(); i++) {
            if (167 == formatted.codePointAt(i))
                i++;
            else
                unformatted += formatted.charAt(i);
        }
        return unformatted;
    }

    private static String getTime(int ticks) {
        WorldClient wc = Minecraft.getMinecraft().theWorld;
        long age = wc.getTotalWorldTime() % 20;
        ticks -= age;
        if (ticks < 20)
            return "0s";
        int secs = (ticks / 20) % 60;
        int mins = (ticks / 1200) % 60;
        int hours = (ticks / 72000);
        String total = (hours == 0 ? "" : (hours + "h"));
        total += (mins == 0 ? "" : (mins + "m"));
        if (doSecs.getBoolean() && ((doSecsWhileMin.getBoolean()) || mins <= 0))
            total += (secs == 0 ? "" : (secs + "s"));
        return total;
    }

    private static String getPrePostChars(String time) {
        if (!doTimeChar.getBoolean())
            return time;
        String pre = preTimeChar.getString();
        String post = postTimeChar.getString();
        return pre + time + post;
    }

    private static boolean shouldDoPlayerStuff(String unformatted) {
        return unformatted.charAt(0) == '<';
    }

    public static String getTimeText(IChatComponent chatLine, int ticks) {
        if (!AlexIILLib.timeText.getBoolean())
            return chatLine.getFormattedText();
        String unformatted = getUnformatedText(chatLine);
        if (!shouldDoPlayerStuff(unformatted))
            return chatLine.getFormattedText();
        String time = getTime(ticks);
        return getPrePostChars(time) + chatLine.getFormattedText();
    }
}
