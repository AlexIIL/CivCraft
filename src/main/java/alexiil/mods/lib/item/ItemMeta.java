package alexiil.mods.lib.item;

import java.util.ArrayList;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import alexiil.mods.lib.block.BlockMeta;

public final class ItemMeta {
    
    public final Item item;
    public final int meta;
    
    public static final String NBT_META = "ITEM.META";
    public static final String NBT_ITEM = "ITEM.ITEM";
    private static ArrayList<ItemMeta> itemMetaPool = new ArrayList<ItemMeta>();
    
    private ItemMeta(Item item, int meta) {
        this.item = item;
        this.meta = meta;
        itemMetaPool.add(this);
    }
    
    public static ItemMeta get(Item block, int meta) {
        if (block == null)
            return null;
        for (ItemMeta bm : itemMetaPool) {
            if (bm.item.equals(block) && bm.meta == meta)
                return bm;
        }
        return new ItemMeta(block, meta);
    }
    
    @Override
    public int hashCode()// eclipse generated
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((item == null) ? 0 : item.hashCode());
        result = prime * result + meta;
        return result;
    }
    
    @Override
    public boolean equals(Object obj)// eclipse generated
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BlockMeta other = (BlockMeta) obj;
        if (item == null) {
            if (other.block != null)
                return false;
        }
        else if (!item.equals(other.block))
            return false;
        if (meta != other.meta)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        String toShow;
        try {
            toShow = I18n.format(item.getUnlocalizedName()) + ":" + meta;
        }
        catch (Throwable t) {
            toShow = item + ":" + meta;
        }
        
        return toShow;
    }
    
    public static ItemMeta readFromNBT(NBTTagCompound nbt) {
        return get((Item) Item.itemRegistry.getObject(nbt.getString(NBT_ITEM)), nbt.getInteger(NBT_META));
    }
    
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_META, meta);
        nbt.setString(NBT_ITEM, (String) Block.blockRegistry.getNameForObject(item));
    }
}
