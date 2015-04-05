package alexiil.mods.civ.tech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import alexiil.mods.civ.CivConfig;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.api.ResearchNoteObjectives;
import alexiil.mods.civ.item.CivItems;

public class PlayerResearchHelper {
    public static double cooldownMultiplier = 1.01;
    public static double cooldownAddition = 0.05;
    public static double cooldownDivision = 40;
    public static double progressRequired = 3;
    public static double cooldownTimeDivision = 300;

    /** @param player
     *            The player who might earn a beaker
     * @param name
     *            The name of this earning (like, "break.block.oreEmerald" or "entity.kill.entity_skeleton") this is
     *            done on a parent based system, so calling this with "a.b.c" will call this again with "a.b", which
     *            would call this again with "a". Calling this with a "a.tile.b" will cause the "tile" to be removed,
     *            leaving just "a.b" (the same thing happens if you have "a.item.b")
     * @param beakerNum
     *            The number of beakers to get from doing this (assuming the cooldown = 0). If this is 0, then the
     *            default is used form the map. If it does not exist in the map, then nothing happens
     * @param multiplier
     *            The multiplier to add to this- use this if (say) you should earn differing amounts for the same name-
     *            say hitting an arrow from really far away, as opposed to close
     * @param shouldCool
     *            Whether or not to add to the cooldown for this. This should only be false for things that cannot be
     *            automated in any way (say, exploring new chunks) */
    public static void progressResearch(EntityPlayer player, String name, double multiplier, boolean shouldCool) {
        String unlocalizedName = name.replace("tile", "").replace("item", "").replace("..", ".");
        if (unlocalizedName.endsWith("."))
            unlocalizedName = unlocalizedName.substring(0, unlocalizedName.length() - 1);
        String[] names = name.split("\\.");
        CivLog.debugInfo("\"" + name + "\"=" + Arrays.toString(names));
        if (names.length > 1)
            progressResearch(player, name.substring(0, name.lastIndexOf(".")));
        double beakers = ResearchNoteObjectives.getAmountOrParent(unlocalizedName) * multiplier;
        if (beakers <= 0)
            return;

        NBTTagCompound nbt = player.getEntityData();
        NBTTagCompound persisted = nbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        NBTTagCompound nbtCiv = persisted.getCompoundTag("civcraft");
        NBTTagCompound nbtCooldown = nbtCiv.getCompoundTag("cooldown");
        NBTTagCompound nbtProgress = nbtCiv.getCompoundTag("progress");
        NBTTagCompound nbtLastDone = nbtCiv.getCompoundTag("lastdone");
        double cooldown = nbtCooldown.getDouble(unlocalizedName);
        double progress = nbtProgress.getDouble(unlocalizedName);
        if (cooldown > 1)
            progress += beakers / cooldown / cooldown;
        else
            progress += beakers;
        if (progress >= progressRequired) {
            progress -= progressRequired;
            IChatComponent cc = new ChatComponentTranslation("civcraft.chat.earnBeaker");
            String pre = ResearchNoteObjectives.getPreTranslation(name);
            if (pre != null && pre.length() > 0)
                cc.appendSibling(new ChatComponentTranslation(pre));
            String post = ResearchNoteObjectives.getPostTranslation(name).toLowerCase();
            if (post != null && post.length() > 0) {
                cc.appendSibling(new ChatComponentTranslation(post).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
            }
            player.addChatMessage(cc);
            nbtCiv.setInteger("beakers_to_give", nbtCiv.getInteger("beakers_to_give") + 1);
        }
        cooldown += cooldownAddition * (1 + beakers);
        cooldown *= cooldownMultiplier;
        if (shouldCool)
            nbtCooldown.setDouble(unlocalizedName, cooldown);
        nbtProgress.setDouble(unlocalizedName, progress);
        nbtLastDone.setLong(unlocalizedName, 0);
        CivLog.debugInfo("cooldown = " + cooldown + ", progress = " + progress);
        nbtCiv.setTag("cooldown", nbtCooldown);
        nbtCiv.setTag("progress", nbtProgress);
        nbtCiv.setTag("lastdone", nbtLastDone);
        persisted.setTag("civcraft", nbtCiv);
        nbt.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }

    public static void progressResearch(EntityPlayer player, String name) {
        progressResearch(player, name, 1);
    }

    public static void progressResearch(EntityPlayer player, String name, double multiplier) {
        progressResearch(player, name, multiplier, true);
    }

    public static void decrementCooldown(EntityPlayer player) {
        NBTTagCompound nbt = player.getEntityData();
        NBTTagCompound persisted = nbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (CivConfig.debugMode.getBoolean() && !player.worldObj.isRemote)
            CivCraft.playerNBTData = persisted;
        NBTTagCompound nbtCiv = persisted.getCompoundTag("civcraft");
        NBTTagCompound nbtCooldown = nbtCiv.getCompoundTag("cooldown");
        NBTTagCompound nbtLastDone = nbtCiv.getCompoundTag("lastdone");

        Iterator<?> iter = nbtCooldown.getKeySet().iterator();
        ArrayList<String> strings = new ArrayList<String>();
        while (iter.hasNext())
            strings.add((String) iter.next());

        for (String name : strings) {
            double cooldown = nbtCooldown.getDouble(name);
            long time = nbtLastDone.getLong(name) + 1;
            nbtLastDone.setLong(name, time);
            cooldown -= cooldownAddition / cooldownDivision * ((int) (time / (cooldownTimeDivision)));
            if (cooldown <= 0)
                nbtCooldown.removeTag(name);
            else
                nbtCooldown.setDouble(name, cooldown);
        }
        int beakersToGive = nbtCiv.getInteger("beakers_to_give");
        if (beakersToGive > 0) {
            // Search the players inventory, every tick that they are owed a research note
            InventoryPlayer inv = player.inventory;
            if (inv.hasItem(CivItems.sciencePacks[0]) || inv.getFirstEmptyStack() >= 0) {
                if (inv.addItemStackToInventory(new ItemStack(CivItems.sciencePacks[0]))) {
                    player.inventoryContainer.detectAndSendChanges();
                    beakersToGive -= 1;
                    nbtCiv.setInteger("beakers_to_give", beakersToGive);
                }
            }
        }
        nbtCiv.setTag("cooldown", nbtCooldown);
        persisted.setTag("civcraft", nbtCiv);
        nbt.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }
}
