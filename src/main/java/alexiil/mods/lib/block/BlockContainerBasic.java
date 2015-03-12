package alexiil.mods.lib.block;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alexiil.mods.lib.AlexIILMod;
import alexiil.mods.lib.item.ItemBlockMeta;

public abstract class BlockContainerBasic extends BlockContainer {
    @SideOnly(Side.CLIENT)
    protected TextureAtlasSprite[] icons;
    public final boolean enabled;
    protected AlexIILMod mod;

    public BlockContainerBasic(Material material, String name, AlexIILMod mod) {
        super(material);
        this.mod = mod;
        setUnlocalizedName(mod.meta.modId + "_" + name);
        Property prop = mod.cfg.cfg.get("blocks", name, "true");
        // prop.comment = "Enable the " + mod.format(getUnlocalizedName() + ".name") + " block";
        enabled = prop.getBoolean();
        if (enabled) {
            GameRegistry.registerBlock(this, ItemBlockMeta.class, name);
            GameRegistry.registerTileEntity(getTileClass(), mod.meta.modId + "_tile_" + name);
            this.setCreativeTab(mod.tab);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    @Override
    // Add blocks to the creative
    // inventory
            public
            void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < getMetaBlocks(); i++)
            list.add(new ItemStack(item, 1, i));
    }

    public int getMetaBlocks() {
        return 1;
    }

    public abstract Class<? extends TileEntity> getTileClass();
}
