package alexiil.mods.civ.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.tile.TileLab;
import alexiil.mods.lib.block.BlockContainerBasic;

public class BlockLab extends BlockContainerBasic {
    public static final PropertyBool RESEARCHING = PropertyBool.create("researching");

    public BlockLab(String name) {
        super(Material.iron, name, CivCraft.instance);
        setHardness(4F);
        setLightOpacity(1);
        setDefaultState(blockState.getBaseState().withProperty(RESEARCHING, false));
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileLab();
    }

    @Override
    public Class<? extends TileEntity> getTileClass() {
        return TileLab.class;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { RESEARCHING });
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(RESEARCHING, meta != 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (Boolean) state.getValue(RESEARCHING) ? 1 : 0;
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    @Override
    public void initModel() {
        // super.initModel();
        // ModelLoader.addVariantName(Item.getItemFromBlock(this), "civcraft:lab_off", "civcraft:lab_on");
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher()
                .register(Item.getItemFromBlock(this), 0, new ModelResourceLocation("civcraft:lab"));
    }
}
