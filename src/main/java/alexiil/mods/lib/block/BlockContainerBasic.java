package alexiil.mods.lib.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alexiil.mods.lib.AlexIILMod;
import alexiil.mods.lib.item.ItemBlockMeta;
import alexiil.mods.lib.tile.TileEntityBasic;

public abstract class BlockContainerBasic extends BlockContainer {
    private static final List<BlockContainerBasic> blocks = new ArrayList<BlockContainerBasic>();

    public final boolean enabled;
    public final String name;

    protected AlexIILMod mod;

    public static void initModels() {
        for (BlockContainerBasic ib : blocks)
            ib.initModel();
    }

    public BlockContainerBasic(Material material, String name, AlexIILMod mod) {
        super(material);
        this.name = name;
        this.mod = mod;
        setUnlocalizedName(mod.meta.modId + "_" + name);

        Property prop = mod.cfg.cfg.get("blocks", name, "true");
        prop.comment = "Enable the " + mod.format(getUnlocalizedName() + ".name") + " block";
        enabled = prop.getBoolean();
        if (enabled) {
            GameRegistry.registerBlock(this, ItemBlockMeta.class, name);
            GameRegistry.registerTileEntity(getTileClass(), mod.meta.modId + "_tile_" + name);
            this.setCreativeTab(mod.tab);
            blocks.add(this);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (int i = 0; i < getMetaBlocks(); i++)
            list.add(new ItemStack(item, 1, i));
    }

    public int getMetaBlocks() {
        return 1;
    }

    public abstract Class<? extends TileEntityBasic> getTileClass();

    public void initModel() {
        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        mesher.register(Item.getItemFromBlock(this), 0, new ModelResourceLocation(mod.meta.modId + ":" + name));
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        ((TileEntityBasic) world.getTileEntity(pos)).dropItems();
        super.breakBlock(world, pos, state);
    }
}
