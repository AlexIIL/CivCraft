package alexiil.mods.civ.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import alexiil.mods.civ.api.FindMatchingRecipeEvent;

// TODO: remove these IF forge incorporates my events
public class VanillaEventHooks {
    public static ItemStack canSmeltEvent(ItemStack in, World world, BlockPos pos) {
        ItemStack out = FurnaceRecipes.instance().getSmeltingResult(in);
        FindMatchingRecipeEvent evt =
                new FindMatchingRecipeEvent.Block(world, pos, new ItemStack[] { in }, new ItemStack[] { out }, FindMatchingRecipeEvent.EType.SMELT);
        MinecraftForge.EVENT_BUS.post(evt);
        return evt.isCanceled() ? null : out;
    }

    public static ItemStack canCraftPlayerEvent(InventoryCrafting ic, EntityPlayer player) {
        World world = player.worldObj;
        ItemStack out = CraftingManager.getInstance().findMatchingRecipe(ic, world);
        ItemStack[] in = new ItemStack[ic.getSizeInventory()];
        for (int i = 0; i < in.length; i++)
            in[i] = ic.getStackInSlot(i);
        Event event = new FindMatchingRecipeEvent.Player(world, player, in, new ItemStack[] { out }, FindMatchingRecipeEvent.EType.CRAFT);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled())
            return null;
        return out;
    }

    public static ItemStack canCraftBlockEvent(InventoryCrafting ic, World world, BlockPos pos) {
        ItemStack out = CraftingManager.getInstance().findMatchingRecipe(ic, world);
        ItemStack[] in = new ItemStack[ic.getSizeInventory()];
        for (int i = 0; i < in.length; i++)
            in[i] = ic.getStackInSlot(i);
        Event event = new FindMatchingRecipeEvent.Block(world, pos, in, new ItemStack[] { out }, FindMatchingRecipeEvent.EType.CRAFT);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isCanceled() ? null : out;
    }
}
