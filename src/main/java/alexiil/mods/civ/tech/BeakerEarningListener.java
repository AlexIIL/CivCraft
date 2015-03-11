package alexiil.mods.civ.tech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import alexiil.mods.civ.CivConfig;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.lib.EChatColours;

public class BeakerEarningListener {
    private static Map<String, Double> beakerGetting = Collections.synchronizedMap(new HashMap<String, Double>());
    public static double COOLDOWN_MULTIPLIER = 1.01;
    public static double COOLDOWN_ADDITION = 0.05;
    public static double COOLDOWN_DIVISION = 40;
    public static double PROGRESS_REQUIRED = 10;
    public static double COOLDOWN_TIME_DIVISON = 300;
    public static final BeakerEarningListener instance = new BeakerEarningListener();
    
    static {
        addBeakerAmount("block", 0.001F);
        addBeakerAmount("block.break", 0.002F);
        addBeakerAmount("block.break.obsidian", 0.01F);// TODO: do this one properly
        addBeakerAmount("block.break.oreGold", 0.005F);
        addBeakerAmount("block.harvest", 0.1F);
        addBeakerAmount("block.harvest.oreDiamond", 0.5F);
        addBeakerAmount("block.harvest.oreCoal", 0.2F);
        addBeakerAmount("block.harvest.oreEmerald", 1.5F);
        addBeakerAmount("block.harvest.oreRedstone", 0.4F);
        addBeakerAmount("entity", 0.01F);
        addBeakerAmount("entity.arrowHit", 0.1F);
        addBeakerAmount("entity.breed", 0.1F);
        addBeakerAmount("entity.kill", 0.1F);
        addBeakerAmount("entity.attack", 0.04F);
        // TODO: make this config based! (maybe stop doing the techs per world? kinda not sure of its uses anymore)
        addBeakerAmount("craft", 0.1F);
    }
    
    public static void addBeakerAmount(String type, double amount) {
        beakerGetting.put(type, amount);
    }
    
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
     *            say hitting an arrow from really far away, as opposed to close */
    public static void earnBeaker(EntityPlayer player, String name, double multiplier) {
        String unlocalizedName = name.replace("tile", "").replace("item", "").replace("..", ".");
        if (unlocalizedName.endsWith("."))
            unlocalizedName = unlocalizedName.substring(0, unlocalizedName.length() - 1);
        String[] names = name.split("\\.");
        CivLog.debugInfo("\"" + name + "\"=" + Arrays.toString(names));
        if (names.length > 1)
            earnBeaker(player, name.substring(0, name.lastIndexOf(".")));
        double beakers;
        if (beakerGetting.containsKey(unlocalizedName))
            beakers = beakerGetting.get(unlocalizedName) * multiplier;
        else {
            beakers = 0;
            for (int i = names.length - 1; i >= 0; i--) {
                String n = "";
                for (int j = 0; j <= i; j++) {
                    n += names[j];
                    if (j != 0)
                        n += ".";
                }
                if (beakerGetting.containsKey(n)) {
                    beakers = beakerGetting.get(n) * multiplier;
                    break;
                }
            }
            if (beakers <= 0)
                return;
        }
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
        if (progress >= PROGRESS_REQUIRED) {
            progress -= PROGRESS_REQUIRED;
            IChatComponent cc = new ChatComponentTranslation("civcraft.chat.earnBeaker");
            cc.appendSibling(new ChatComponentTranslation(getPreTranslation(name)));
            String post = getPostTranslation(name);
            if (post != null && post.length() > 0) {
                cc.appendSibling(new ChatComponentText(EChatColours.GOLD + " ["));
                cc.appendSibling(new ChatComponentTranslation(post).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
                cc.appendSibling(new ChatComponentText(EChatColours.GOLD + "]"));
            }
            player.addChatMessage(cc);
            nbtCiv.setInteger("beakers_to_give", nbtCiv.getInteger("beakers_to_give") + 1);
        }
        cooldown += COOLDOWN_ADDITION * (1 + beakers);
        cooldown *= COOLDOWN_MULTIPLIER;
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
    
    public static void earnBeaker(EntityPlayer player, String name) {
        earnBeaker(player, name, 1);
    }
    
    public static String getPreTranslation(String in) {
        if (in.startsWith("craft."))
            return "civcraft.chat.earnBeaker.craft";
        if (in.startsWith("block.break."))
            return "civcraft.chat.earnBeaker.block.break";
        if (in.startsWith("block.harvest."))
            return "civcraft.chat.earnBeaker.block.harvest";
        if (in.startsWith("entity.kill"))
            return "civcraft.chat.earnBeaker.entity.kill";
        if (in.startsWith("entity.attack"))
            return "civcraft.chat.earnBeaker.entity.attack";
        return in;
    }
    
    public static String getPostTranslation(String in) {
        if (in.contains("tile."))
            return in.substring(in.indexOf("tile.")) + ".name";
        if (in.contains("item."))
            return in.substring(in.indexOf("item.")) + ".name";
        return in;
    }
    
    @SubscribeEvent
    public void playerTick(PlayerTickEvent event) {
        if (event.player.worldObj.isRemote)
            return;
        if (event.phase != Phase.END)
            return;
        NBTTagCompound nbt = event.player.getEntityData();
        NBTTagCompound persisted = nbt.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
        if (CivConfig.debugMode.getBoolean())
            CivCraft.playerNBTData = persisted;
        NBTTagCompound nbtCiv = persisted.getCompoundTag("civcraft");
        NBTTagCompound nbtCooldown = nbtCiv.getCompoundTag("cooldown");
        NBTTagCompound nbtLastDone = nbtCiv.getCompoundTag("lastdone");
        
        Iterator<?> iter = nbtCooldown.getKeySet().iterator();
        ArrayList<String> strings = new ArrayList<String>();
        while (iter.hasNext())
            strings.add((String) iter.next());
        
        for (String key : strings) {// java.util.ConcurrentModificationException here!
            String name = (String) key;
            double cooldown = nbtCooldown.getDouble(name);
            long time = nbtLastDone.getLong(name) + 1;
            nbtLastDone.setLong(name, time);
            cooldown -= COOLDOWN_ADDITION / COOLDOWN_DIVISION * ((int) (time / (COOLDOWN_TIME_DIVISON)));
            if (cooldown <= 0)
                nbtCooldown.removeTag(name);
            else
                nbtCooldown.setDouble(name, cooldown);
        }
        int beakersToGive = nbtCiv.getInteger("beakers_to_give");
        if (beakersToGive > 0) {
            // Search the players inventory, every tick that they are owed a beaker
            InventoryPlayer inv = event.player.inventory;
            if (inv.hasItem(CivItems.sciencePacks[0]) || inv.getFirstEmptyStack() >= 0) {
                if (inv.addItemStackToInventory(new ItemStack(CivItems.sciencePacks[0]))) {
                    event.player.inventoryContainer.detectAndSendChanges();
                    beakersToGive -= 1;
                    nbtCiv.setInteger("beakers_to_give", beakersToGive);
                }
            }
        }
        nbtCiv.setTag("cooldown", nbtCooldown);
        persisted.setTag("civcraft", nbtCiv);
        nbt.setTag(EntityPlayer.PERSISTED_NBT_TAG, persisted);
    }
    
    /** @return true if the player is a real player (so not a fake one) */
    public static boolean isPlayerReal(EntityPlayer player) {
        if (player == null)
            return false;
        if (player instanceof FakePlayer)
            return false;
        return true;
    }
    
    // Beaker earning events
    
    @SubscribeEvent
    public void playerBreakBlock(BreakEvent event) {
        if (event.world.isRemote)
            return;
        EntityPlayer player = event.getPlayer();
        if (!isPlayerReal(player))
            return;
        int fortune = EnchantmentHelper.getFortuneModifier(player);
        String name = event.state.getBlock().getUnlocalizedName();
        boolean harvest = false;
        List<ItemStack> stacks = event.state.getBlock().getDrops(event.world, event.pos, event.state, fortune);
        if (stacks.size() == 1) {
            ItemStack stack = stacks.get(0);
            Item item = stack.getItem();
            if (item == null)
                ;
            else if (item instanceof ItemBlock) {
                ItemBlock ib = (ItemBlock) item;
                Block b = ib.block;
                harvest = b != event.state.getBlock();
            }
            else
                harvest = true;
        }
        else
            harvest = stacks.size() != 0;
        if (!harvest)
            earnBeaker(player, "block.break." + name);
        else
            earnBeaker(player, "block.harvest." + name);
    }
    
    @SubscribeEvent
    public void playerCrafted(ItemCraftedEvent event) {
        if (event.player.worldObj.isRemote)
            return;
        if (!isPlayerReal(event.player))
            return;
        if (event.crafting == null || event.crafting.getItem() == null)
            return;
        String itemName = "craft." + event.crafting.getItem().getUnlocalizedName(event.crafting);
        earnBeaker(event.player, itemName);
    }
    
    @SubscribeEvent
    public void entityAttack(LivingHurtEvent event) {
        if (event.isCanceled())
            return;
        if (event.entity.worldObj.isRemote)
            return;
        EntityPlayer player = null;
        boolean arrow = false;
        double distance = 0;
        if (event.source.getSourceOfDamage() instanceof EntityPlayer)
            player = (EntityPlayer) event.source.getSourceOfDamage();
        else if (event.source.getSourceOfDamage() instanceof EntityArrow) {
            if (((EntityArrow) event.source.getSourceOfDamage()).shootingEntity instanceof EntityPlayer) {
                player = (EntityPlayer) ((EntityArrow) event.source.getSourceOfDamage()).shootingEntity;
                arrow = true;
                distance = event.entity.getDistanceSqToEntity(player);
            }
            else
                return;
        }
        else
            return;
        if (!isPlayerReal(player))
            return;
        if (event.entityLiving == null)
            return;
        String entName = "entity.attack.tile." + EntityList.getEntityString(event.entityLiving);
        earnBeaker(player, entName);
        if (arrow)
            earnBeaker(player, "entity.arrowHit", distance);
    }
    
    @SubscribeEvent
    public void entityDeath(LivingDeathEvent event) {
        if (event.isCanceled())
            return;
        if (event.entity.worldObj.isRemote)
            return;
        if (event.source.getSourceOfDamage() == null)
            return;
        EntityPlayer player;
        Entity ent = event.source.getSourceOfDamage();
        boolean arrow = false;
        double distance = 0;
        if (ent instanceof EntityPlayer)
            player = (EntityPlayer) event.source.getSourceOfDamage();
        else if (ent instanceof EntityArrow) {
            if (((EntityArrow) ent).shootingEntity instanceof EntityPlayer) {
                player = (EntityPlayer) ((EntityArrow) ent).shootingEntity;
                arrow = true;
                distance = event.entity.getDistanceSqToEntity(player);
            }
            else
                return;
        }
        else
            return;
        if (!isPlayerReal(player))
            return;
        String name = "entity.kill.tile." + EntityList.getEntityString(event.entity);
        earnBeaker(player, name);
        if (arrow)
            earnBeaker(player, "entity.arrowHit", distance);
    }
    
    // TODO: item related stuffs. So, firing (where an infinity enchantment is less than normal) of bows and
    // breaking of tools
}
