package alexiil.forgechanges;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

// TODO: remove these IF forge incorporates my events
public class MinecraftForgeNewHooks {
    public static boolean canSmeltEvent(ItemStack in, ItemStack out, World world, BlockPos pos) {
        Event evt =
                new FindMatchingRecipeEvent.Block(world, pos, new ItemStack[] { in }, new ItemStack[] { out }, FindMatchingRecipeEvent.EType.SMELT);
        MinecraftForge.EVENT_BUS.post(evt);
        return !evt.isCanceled();
    }
    
    public static boolean canCraftPlayerEvent(InventoryCrafting ic, ItemStack out, World world, EntityPlayer player) {
        ItemStack[] in = new ItemStack[ic.getSizeInventory()];
        for (int i = 0; i < in.length; i++)
            in[i] = ic.getStackInSlot(i);
        Event event = new FindMatchingRecipeEvent.Player(world, player, in, new ItemStack[] { out }, FindMatchingRecipeEvent.EType.CRAFT);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }
    
    public static boolean canCraftBlockEvent(InventoryCrafting ic, ItemStack out, World world, BlockPos pos) {
        ItemStack[] in = new ItemStack[ic.getSizeInventory()];
        for (int i = 0; i < in.length; i++)
            in[i] = ic.getStackInSlot(i);
        Event event = new FindMatchingRecipeEvent.Block(world, pos, in, new ItemStack[] { out }, FindMatchingRecipeEvent.EType.CRAFT);
        MinecraftForge.EVENT_BUS.post(event);
        return !event.isCanceled();
    }
}