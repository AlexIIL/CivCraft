package alexiil.mods.civ;

import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.RecipeSorter;
import alexiil.mods.civ.crafting.RecipeTech;
import alexiil.mods.civ.item.CivItems;

public class CivRecipes {
    public static void init() {
        RecipeSorter.register("civcraft:tech", RecipeTech.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");

        GameRegistry.addRecipe(RecipeTech.instance);
        GameRegistry.addRecipe(CivItems.technology.getStartingTech(), new Object[] { "SSS", "PBP", "SSS", 'S', Items.stick, 'P', Blocks.planks, 'B',
                CivItems.sciencePacks[0] });
        addCraftingRecipe(CivItems.techBag, new Object[] { "LRP", "PBP", "PCL", 'L', Items.stick, 'C', Blocks.chest, 'R', CivItems.sciencePacks[0],
                'B', Items.book, 'P', Items.paper }, false, true);
        addCraftingRecipe(CivItems.sciencePacks[1], new Object[] { "SGS", "GIG", "LRW", 'I', Items.iron_ingot, 'G', Blocks.glass, 'R',
                Items.redstone, 'L', Blocks.log, 'W', Blocks.planks, 'S', new ItemStack(Blocks.stone, 1, 0) }, false, false);
    }

    private static String flipString(String s) {
        String s2 = "";
        int l = s.length() - 1;
        for (int i = 0; i < s.length(); i++, l--)
            s2 += s.substring(l, l + 1);
        return s2;
    }

    public static void addCraftingRecipe(Item i, Object[] inputs, boolean flipVertically, boolean flipHorizontally) {
        addCraftingRecipe(new ItemStack(i, 1), inputs, flipVertically, flipHorizontally);
    }

    public static void addCraftingRecipe(Block i, Object[] inputs, boolean flipVertically, boolean flipHorizontally) {
        addCraftingRecipe(new ItemStack(i, 1), inputs, flipVertically, flipHorizontally);
    }

    private static void addCraftingRecipe(ItemStack is, Object[] inputs, boolean flipVertically, boolean flipHorizontally) {
        String n0 = is.getItem().getUnlocalizedName();
        String name = I18n.format(n0.substring(("item." + Lib.Mod.ID + "_").length()));
        Property prop = CivCraft.instance.cfg.cfg.get("crafting", name, "true");
        prop.comment = "Allow the " + is.getDisplayName() + " to be crafted";
        boolean isAllowed = prop.getBoolean();
        if (!isAllowed)
            return;
        GameRegistry.addShapedRecipe(is, inputs);
        if (flipVertically) {
            Object[] obj = inputs.clone();
            obj[0] = inputs[2];
            obj[2] = inputs[0];
            GameRegistry.addShapedRecipe(is, obj);
        }
        if (flipHorizontally) {
            Object[] obj = inputs.clone();
            obj[0] = flipString((String) inputs[0]);
            obj[1] = flipString((String) inputs[1]);
            obj[2] = flipString((String) inputs[2]);
            GameRegistry.addShapedRecipe(is, obj);
        }
        if (flipHorizontally && flipVertically) {
            Object[] obj = inputs.clone();
            obj[0] = flipString((String) inputs[2]);
            obj[1] = flipString((String) inputs[1]);
            obj[2] = flipString((String) inputs[0]);
            GameRegistry.addShapedRecipe(is, obj);
        }
    }
}
