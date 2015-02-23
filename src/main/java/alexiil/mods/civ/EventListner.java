package alexiil.mods.civ;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import alexiil.forgechanges.FindMatchingRecipeEvent;
import alexiil.mods.civ.crafting.RecipeTech;
import alexiil.mods.civ.item.CivItems;
import alexiil.mods.civ.net.MessageHandler;
import alexiil.mods.civ.net.MessagePlayerTechUpdate;
import alexiil.mods.civ.net.MessageTechTreeUpdate;
import alexiil.mods.civ.tech.TechResearchedEvent;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.utils.CraftUtils;
import alexiil.mods.civ.utils.TechUtils;

public class EventListner {
    public static final EventListner instance = new EventListner();
    
    private EventListner() {}
    
    @SubscribeEvent public void onClientConnect(ClientConnectedToServerEvent event) {
        CivLog.info("onClientConnect");
        new Thread("AlexIIL|ClientConnectedToServer") {
            @Override public void run() {
                try {
                    Thread.sleep(5000);
                }
                catch (InterruptedException e) {
                    
                }
                CivLog.info("sending message (hopefully)");
                MessageHandler.INSTANCE.sendToServer(new MessagePlayerTechUpdate());
            }
        }.start();
        if (event.isLocal)
            return;
        MessageHandler.INSTANCE.sendToServer(new MessageTechTreeUpdate());
    }
    
    @SubscribeEvent public void onCraft(ItemCraftedEvent event) {
        if (event.craftMatrix instanceof InventoryCrafting) {
            ItemStack i = RecipeTech.instance.getOutput(event.player, (InventoryCrafting) event.craftMatrix, true);
            ArrayList<Integer> techPositions = new ArrayList<Integer>();
            InventoryCrafting craft = (InventoryCrafting) event.craftMatrix;
            for (int p = 0; p < craft.getSizeInventory(); p++) {
                ItemStack stack = craft.getStackInSlot(p);
                if (stack == null)
                    continue;
                if ((stack.getItem() == CivItems.technology && stack.getItemDamage() == 2))
                    techPositions.add(p);
            }
            if (techPositions.size() != 0 && i == null) {
                for (int p : techPositions)
                    craft.getStackInSlot(p).stackSize++;
                return;
            }
            if (i == null)
                return;
            NBTTagCompound nbt = new NBTTagCompound();
            i.writeToNBT(nbt);
            event.crafting.readFromNBT(nbt);// OMG THE HAX! I LOVE IT SO MUCH!
        }
    }
    
    @SubscribeEvent public void renderEnd(RenderTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        // Check FIRST, to avoid rendering before.
        if (mc.thePlayer == null)
            return;
        // if (mc.currentScreen != null)
        // return;
        if (CivConfig.debugMode.getBoolean()) {
            NBTTagCompound nbt = CivCraft.playerNBTData;
            NBTTagCompound nbtCiv = nbt.getCompoundTag("civcraft");
            NBTTagCompound nbtCooling = nbtCiv.getCompoundTag("cooldown");
            NBTTagCompound nbtProgress = nbtCiv.getCompoundTag("progress");
            NBTTagCompound nbtLastDone = nbtCiv.getCompoundTag("lastdone");
            // CivCraft.log.info(nbtCiv.toString());
            List<String> names = new ArrayList<String>();
            for (Object o : nbtProgress.getKeySet())
                names.add((String) o);
            
            Collections.sort(names, new Comparator<String>() {
                @Override public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
            
            // ScaledResolution res = new ScaledResolution(mc, mc.displayWidth,
            // mc.displayHeight);
            // int width = res.getScaledWidth();
            // int height = res.getScaledHeight();
            int x = 10;
            int y = 10;
            int colour = 0xFFFFFF;
            int altColour = 0x0000FF;
            DecimalFormat df = new DecimalFormat();
            // Draw box here, with a colour of -1873784752
            mc.fontRendererObj.drawString("drawing " + names.size() + " stuffs", x, y - mc.fontRendererObj.FONT_HEIGHT, colour);
            for (String n : names) {
                double cool = nbtCooling.getDouble(n);
                String cooling = df.format(cool);
                String progress = df.format(nbtProgress.getDouble(n));
                String time = Long.toString(nbtLastDone.getLong(n));
                int colourToUse = (cool == 0) ? colour : altColour;
                mc.fontRendererObj.drawString("\"" + n + "\"=[Cooling=(" + (cooling.length() > 6 ? cooling.substring(0, 5) : cooling)
                        + "),Progress=(" + (progress.length() > 6 ? progress.substring(0, 5) : progress) + "),LastDone=" + time + "]", x, y,
                        colourToUse);
                y += mc.fontRendererObj.FONT_HEIGHT;
            }
            
        }
    }
    
    @SubscribeEvent public void techResearched(TechResearchedEvent event) {
        TechUtils.addTech(event.entityPlayer, event.tech);
        if (!event.entityPlayer.worldObj.isRemote) // Server
            MessageHandler.INSTANCE.sendTo(new MessagePlayerTechUpdate(event.entityPlayer), (EntityPlayerMP) event.entityPlayer);
    }
    
    @SubscribeEvent public void itemToolTip(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (stack == null)
            return;
        List<String> strings = TechTree.currentTree.getItemTooltip(stack, event.entityPlayer);
        event.toolTip.addAll(strings);
    }
    
    @SubscribeEvent public void blockPlaced(BlockEvent.PlaceEvent event) {
        CraftUtils.addPlayerToChunk(event.world, event.pos, event.player);
    }
    
    @SubscribeEvent public void blockRightClicked(PlayerInteractEvent event) {
        if (event.action != Action.RIGHT_CLICK_BLOCK)
            return;
        CraftUtils.addPlayerToChunk(event.world, event.pos, event.entityPlayer);
    }
    
    // TODO: replace with forge event
    @SubscribeEvent public void craftAttempt(FindMatchingRecipeEvent event) {
        for (ItemStack stack : event.input) {
            if (!canUse(stack, event)) {
                event.setCanceled(true);
                return;
            }
        }
        for (ItemStack stack : event.output) {
            if (!canUse(stack, event)) {
                event.setCanceled(true);
                return;
            }
        }
    }
    
    private boolean canUse(ItemStack stack, FindMatchingRecipeEvent event) {
        List<Tech> techs = new ArrayList<Tech>();
        if (event instanceof FindMatchingRecipeEvent.Player)
            techs = TechUtils.getTechs(((FindMatchingRecipeEvent.Player) event).player);
        else if (event instanceof FindMatchingRecipeEvent.Block)
            techs = CraftUtils.getTechs(event.world, ((FindMatchingRecipeEvent.Block) event).pos);
        return CraftUtils.canUse(stack, techs);
    }
}
