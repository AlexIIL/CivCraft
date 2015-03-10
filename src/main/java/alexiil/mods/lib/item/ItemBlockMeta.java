package alexiil.mods.lib.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockMeta extends ItemBlock {
    public ItemBlockMeta(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }
    
    @Override
    public int getMetadata(int par1) {
        return par1;
    }
}
