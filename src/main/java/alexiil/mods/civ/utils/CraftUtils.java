package alexiil.mods.civ.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.tech.Unlockable;
import alexiil.mods.civ.tech.unlock.IItemBlocker;

public class CraftUtils {
    // Ignore different dimensions for some reason... for now
    
    private static Map<ChunkCoordIntPair, List<PlayerTechData>> chunkPlayers = new HashMap<ChunkCoordIntPair, List<PlayerTechData>>();
    
    public static void addPlayerToChunk(World world, BlockPos pos, EntityPlayer player) {
        if (pos == null || player == null)
            return;
        addPlayerToChunk(world, pos, PlayerTechData.createPlayerTechData(player));
    }
    
    public static void addPlayerToChunk(World world, BlockPos pos, PlayerTechData techs) {
        ChunkCoordIntPair ccip = new ChunkCoordIntPair(pos.getX() << 4, pos.getY() << 4);
        if (!chunkPlayers.containsKey(ccip))
            chunkPlayers.put(ccip, new ArrayList<PlayerTechData>());
        if (!chunkPlayers.get(ccip).contains(techs))
            chunkPlayers.get(ccip).add(techs);
    }
    
    public static void removeTileEntity(TileEntity tile) {
        if (tile == null)
            return;
        removeTileEntity(tile.getWorld(), tile.getPos());
    }
    
    public static void removeTileEntity(World world, BlockPos pos) {
        if (pos == null)
            return;
        chunkPlayers.remove(pos);
    }
    
    public static List<PlayerTechData> getPlayers(World world, BlockPos pos) {
        return getPlayers(world, new ChunkCoordIntPair(pos.getX() << 4, pos.getZ() << 4));
    }
    
    public static List<PlayerTechData> getPlayers(World world, ChunkCoordIntPair ccip) {
        if (chunkPlayers.containsKey(ccip))
            return chunkPlayers.get(ccip);
        return Collections.<PlayerTechData> emptyList();
    }
    
    public static List<Tech> getTechs(World world, BlockPos pos) {
        return getTechs(world, new ChunkCoordIntPair(pos.getX() << 4, pos.getY() << 4));
    }
    
    public static List<Tech> getTechs(World world, ChunkCoordIntPair ccip) {
        if (world.isRemote) // If its on the client
            return TechUtils.getTechs(Minecraft.getMinecraft().thePlayer);
        List<PlayerTechData> players = getPlayers(world, ccip);
        List<Tech> techs = new ArrayList<Tech>();
        for (PlayerTechData data : players)
            for (Tech t : data.getTechs())
                if (!techs.contains(t))
                    techs.add(t);
        return techs;
    }
    
    /** @param in
     *            The IInventory to craft from (usually an instance of InventoryCrafting)
     * @param pos
     *            The position that this is at
     * @return The recipe that matches it */
    public static ItemStack findMatchingRecipe(InventoryCrafting in, World world, BlockPos pos) {
        for (int i = 0; i < in.getSizeInventory(); i++) {
            ItemStack item = in.getStackInSlot(i);
            if (!canUse(world, pos, item)) {
                CivCraft.log.info("Could not use " + item + ", aborting");
                return null;
            }
        }
        ItemStack item = CraftingManager.getInstance().findMatchingRecipe(in, world);
        if (canUse(world, pos, item))
            return item;
        CivCraft.log.info("Could not use " + item + ", aborting");// TODO: remove these
        return null;
    }
    
    public static boolean canCraft(ItemStack out, ItemStack[] in, TileEntity tile) {
        if (tile == null)
            return true;
        if (!canUse(tile.getWorld(), tile.getPos(), out))
            return false;
        for (ItemStack i : in)
            if (i != null)
                if (!canUse(tile.getWorld(), tile.getPos(), i))
                    return false;
        return true;
    }
    
    public static boolean canUse(ItemStack item, List<Tech> techs) {
        if (item == null)
            return true;
        for (Unlockable u : TechTree.currentTree.getUnlockables().values()) {
            if (u instanceof IItemBlocker) {
                IItemBlocker ib = (IItemBlocker) u;
                if ((!u.isUnlocked(techs)) && ib.doesBlockItem(item)) {
                    List<Tech> required = new ArrayList<Tech>();
                    for (Tech t : u.requiredTechs())
                        if (!techs.contains(t))
                            required.add(t);
                    CivLog.debugInfo("Could not use " + item + ", because \"" + u.getLocalisedName() + "\" is blocking it! (requires "
                            + Arrays.toString(required.toArray()) + ")");
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boolean canUse(World world, BlockPos pos, ItemStack item) {
        return canUse(item, getTechs(world, pos));
    }
    
    public static boolean canUse(EntityPlayer player, ItemStack item) {
        return canUse(item, TechUtils.getTechs(player));
    }
}
