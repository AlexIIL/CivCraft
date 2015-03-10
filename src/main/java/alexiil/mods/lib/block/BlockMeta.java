package alexiil.mods.lib.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;

public class BlockMeta {
    public final Block block;
    public final int meta;
    
    public static final String NBT_META = "BLOCK_METADATA";
    public static final String NBT_BLOCK = "BLOCK_BLOCK";
    private static ArrayList<BlockMeta> blockMetaPool = new ArrayList<BlockMeta>();
    
    private BlockMeta(Block block, int meta) {
        this.block = block;
        this.meta = meta;
        blockMetaPool.add(this);
    }
    
    public static BlockMeta get(Block block, int meta) {
        if (block == null || block == Blocks.air)
            return null;
        for (BlockMeta bm : blockMetaPool) {
            if (bm.block.equals(block) && bm.meta == meta)
                return bm;
        }
        return new BlockMeta(block, meta);
    }
    
    @Override
    public int hashCode()// eclipse generated
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((block == null) ? 0 : block.hashCode());
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
        if (block == null) {
            if (other.block != null)
                return false;
        }
        else if (!block.equals(other.block))
            return false;
        if (meta != other.meta)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        String toShow;
        try {
            toShow = block.getLocalizedName() + ":" + meta;
        }
        catch (Throwable t) {
            toShow = block + ":" + meta;
        }
        
        return toShow;
    }
    
    public static BlockMeta readFromNBT(NBTTagCompound nbt) {
        return get((Block) Block.blockRegistry.getObject(nbt.getString(NBT_BLOCK)), nbt.getInteger(NBT_META));
    }
    
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger(NBT_META, meta);
        nbt.setString(NBT_BLOCK, (String) Block.blockRegistry.getNameForObject(block));
    }
}
