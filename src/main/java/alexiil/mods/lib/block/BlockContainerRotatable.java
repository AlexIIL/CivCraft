package alexiil.mods.lib.block;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import alexiil.mods.lib.AlexIILMod;

public abstract class BlockContainerRotatable extends BlockContainerBasic {
    private static final PropertyDirection DIRECTION = PropertyDirection.create("facing");

    public BlockContainerRotatable(Material material, String name, AlexIILMod mod) {
        super(material, name, mod);
        this.setDefaultState(blockState.getBaseState().withProperty(DIRECTION, EnumFacing.UP));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 3));
    }

    public void rotate(World world, BlockPos pos, IBlockState state, boolean shift) {
        state.cycleProperty(DIRECTION);
        /* int meta = world.getBlockMetadata(x, y, z); if (!canRotate(world, x, y, z, meta)) return; boolean flag =
         * false; if (meta > 7) { flag = true; meta -= 8; } if (!shift) { meta += 1; if (meta > 5) meta = 0; } else {
         * meta -= 1; if (meta < 0) meta = 5; } if (flag) meta += 8; world.setBlockMetadataWithNotify(x, y, z, meta, 3); */
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, DIRECTION);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(DIRECTION, meta != 0);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY,
            float hitZ) {
        if (player.getHeldItem() == null && canRotate(world, pos, state)) {
            rotate(world, pos, state, player.isSneaking());
            return true;
        }
        return false;
    }

    public boolean canRotate(World world, BlockPos pos, IBlockState state) {
        return true;
    }
}
