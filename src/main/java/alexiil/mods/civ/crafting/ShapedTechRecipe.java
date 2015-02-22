package alexiil.mods.civ.crafting;

public class ShapedTechRecipe/* implements IRecipe */{
    /* private ItemStack[] input; private ItemStack output; public static ArrayList<ShapedTechRecipe> recipes = new
     * ArrayList<ShapedTechRecipe>(); public static void addRecipe(ItemStack output, Object... inputObjects) {
     * CivCraft.log .info("Starting to create a shaped tech recipe..."); String recipe = ""; int i = 0; int xSize = 0;
     * int ySize = 0; while (inputObjects[i] instanceof String) { xSize = ((String) inputObjects[i]).length(); ySize++;
     * recipe += (String) inputObjects[i]; i++; } HashMap<Character, ItemStack> hashmap = new HashMap<Character,
     * ItemStack>(); for (; i < inputObjects.length; i += 2) { Character character = (Character) inputObjects[i];
     * ItemStack item = null; if (inputObjects[i + 1] instanceof Item) item = new ItemStack((Item) inputObjects[i + 1]);
     * else if (inputObjects[i + 1] instanceof Block) item = new ItemStack((Block) inputObjects[i + 1], 1, 32767); else
     * if (inputObjects[i + 1] instanceof ItemStack) item = (ItemStack) inputObjects[i + 1]; else if (inputObjects[i +
     * 1] instanceof String) { Tech t = TechTree.currentTree.getTech((String) inputObjects[i + 1]); if (t == null) {
     * CivCraft.log.warn("tried to add a recipe refering to \"" + ((String) inputObjects[i + 1]) +
     * "\", but could not find that tech"); return; } inputObjects[i + 1] = t; } if (inputObjects[i + 1] instanceof
     * Tech) { Tech t = (Tech) inputObjects[i + 1]; item = new ItemStack(CivItems.technology, 1, 2); NBTTagCompound nbt
     * = new NBTTagCompound(); nbt.setInteger(Tech.BEAKERS, t.getBeakersNeeded()); nbt.setString(Tech.NAME, t.name);
     * item.setTagCompound(nbt); } hashmap.put(character, item); } ItemStack[] inputItems = new ItemStack[xSize *
     * ySize]; for (i = 0; i < xSize * ySize; ++i) { char c0 = recipe.charAt(i); if
     * (hashmap.containsKey(Character.valueOf(c0))) inputItems[i] = ((ItemStack)
     * hashmap.get(Character.valueOf(c0))).copy(); else inputItems[i] = null; }
     * CivCraft.log.info("Created a tech recipe! Inputs:"); for (ItemStack t : inputItems) { if (t == null)
     * CivCraft.log.info("  -null"); else CivCraft.log.info("  -" + t.toString()); } ShapedTechRecipe t = new
     * ShapedTechRecipe(output, inputItems); recipes.add(t); GameRegistry.addRecipe(t); } private
     * ShapedTechRecipe(ItemStack output, ItemStack[] input) { this.output = output; this.input = input; }
     * @Override public boolean matches(InventoryCrafting craft, World world) { return getOutput(craft, false) != null;
     * }
     * @Override public ItemStack getCraftingResult(InventoryCrafting craft) { return getOutput(craft, false); } public
     * ItemStack getOutput(InventoryCrafting craft, boolean edit) { if (craft == null) return null; ArrayList<ItemStack>
     * techs = new ArrayList<ItemStack>(); ArrayList<Integer> techPositions = new ArrayList<Integer>(); for (int p = 0;
     * p < craft.getSizeInventory(); p++) { ItemStack stack = craft.getStackInSlot(p); if (stack == null && input[p] ==
     * null) continue; if ((stack != null && input[p] == null) || (stack == null && input[p] != null)) return null; if
     * ((stack.getItem() == CivItems.technology && stack.getItemDamage() == 2)) { Tech t =
     * CivItems.technology.getTech(stack); Tech t2 = CivItems.technology.getTech(input[p]); if (t != t2) return null;
     * techs.add(stack.copy()); techPositions.add(p); } else if (!OreDictionary.itemMatches(input[p], stack, false) ||
     * input[p].stackSize > stack.stackSize) return null; } if (edit) { for (int i : techPositions)
     * craft.getStackInSlot(i).stackSize++; for (int i = 0; i < craft.getSizeInventory(); i++) if
     * (!techPositions.contains(i)) craft.getStackInSlot(i).stackSize -= input[i].stackSize - 1; } return output; }
     * @Override public int getRecipeSize() { return input.length; }
     * @Override public ItemStack getRecipeOutput() { return null; }
     * @Override public ItemStack[] getRemainingItems(InventoryCrafting p_179532_1_) { return null; } */
}
