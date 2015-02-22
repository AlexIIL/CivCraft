package alexiil.mods.lib.item;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;

import alexiil.mods.lib.AlexIILMod;
import alexiil.mods.lib.EChatColours;

public class ItemBase extends Item {
    private static final List<ItemBase> items = new ArrayList<ItemBase>();
    
    private final AlexIILMod mod;
    protected final String name;
    private List<Object> lines = new ArrayList<Object>();
    private List<Object> shiftLines = new ArrayList<Object>();
    private boolean hasShift, hasEffect = false;
    
    public static void initModels() {
        for (ItemBase ib : items)
            ib.initModel();
    }
    
    public ItemBase(String name, AlexIILMod mod) {
        this.mod = mod;
        this.name = name;
        setUnlocalizedName(mod.meta.modId + "_" + name);
        Property prop = mod.cfg.cfg.get("items", name, true);
        prop.comment = "Enable the " + mod.format(getUnlocalizedName() + ".name") + " item";
        if (prop.getBoolean()) {
            GameRegistry.registerItem(this, name);
            setCreativeTab(mod.tab);
            items.add(this);
        }
    }
    
    public ItemBase setCraftingPart() {
        lines.add("Crafting Item.");
        return this;
    }
    
    public ItemBase setCraftingPart(String item) {
        setCraftingPart();
        lines.add("Used for the " + item + ".");
        return this;
    }
    
    public ItemBase addInfo(String line) {
        lines.add(line);
        return this;
    }
    
    public ItemBase addInfo(IChangingItemString line) {
        lines.add(line);
        return this;
    }
    
    public ItemBase addShiftInfo(String line) {
        shiftLines.add(line);
        hasShift = true;
        return this;
    }
    
    public ItemBase addShiftInfo(IChangingItemString line) {
        shiftLines.add(line);
        hasShift = true;
        return this;
    }
    
    public ItemBase setWorkInProgress() {
        lines.add("This is a WIP item!");
        return this;
    }
    
    public ItemBase setEffect() {
        hasEffect = true;
        return this;
    }
    
    @SideOnly(Side.CLIENT) public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return hasEffect && pass == 0;
    }
    
    public void initModel() {
        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        mesher.register(this, 0, new ModelResourceLocation(mod.meta.modId + ":" + name, "inventory"));
    }
    
    /** This is a replacement for EntityPlayer.isSneaking(), as that does not seem to work inside of an inventory. */
    private boolean isShiftKeyPressed() {
        return (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));
    }
    
    public String[] getString(Object o, ItemStack i, EntityPlayer p) {
        if (o instanceof String)
            return new String[] { (String) o };
        if (o instanceof IChangingItemString)
            return ((IChangingItemString) o).getString(i, p);
        return new String[] { o.toString() };
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" }) @Override public void addInformation(ItemStack itemStack, EntityPlayer player, List list,
            boolean flag) {
        if (isShiftKeyPressed() && (hasShift))
            for (Object line : this.shiftLines) {
                String[] ls = getString(line, itemStack, player);
                if (ls != null)
                    for (String l : ls)
                        if (l != null)
                            list.add(l);
            }
        else {
            for (Object line : this.lines) {
                String[] ls = getString(line, itemStack, player);
                if (ls != null)
                    for (String l : ls)
                        if (l != null)
                            list.add(l);
            }
            if (hasShift)
                list.add("<Hold " + EChatColours.BLUE + "SHIFT" + EChatColours.GRAY + " for more info>");
        }
        super.addInformation(itemStack, player, list, flag);
    }
}
