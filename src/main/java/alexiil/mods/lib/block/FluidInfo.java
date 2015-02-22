package alexiil.mods.lib.block;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import alexiil.mods.lib.Coordinate;

public class FluidInfo {
    public static enum FluidType {
        NONE, WATER_SOURCE, WATER_FLOW, LAVA_FLOW, LAVA_SOURCE, STATIONARY, FLUID_BLOCK
    }
    
    private static List<FluidInfo> fluids = new ArrayList<FluidInfo>();
    
    public final FluidInfo.FluidType type;
    public final IBlockState block;
    
    public static FluidInfo getFluidInfo(FluidInfo.FluidType type, IBlockState b) {
        for (FluidInfo fluid : fluids)
            if (fluid.type == type && fluid.block.equals(b))
                return fluid;
        return new FluidInfo(type, b);
    }
    
    public static FluidInfo getFluidInfo(World world, Coordinate cor) {
        IBlockState block = world.getBlockState(cor);
        FluidInfo.FluidType type;
        
        if (block == null)
            type = FluidInfo.FluidType.NONE;
        else if (block == Blocks.flowing_lava || block == Blocks.lava)
            type = getLevel(block) == 0 ? FluidInfo.FluidType.LAVA_SOURCE : FluidInfo.FluidType.LAVA_FLOW;
        else if (block == Blocks.flowing_water || block == Blocks.water)
            type = getLevel(block) == 0 ? FluidInfo.FluidType.WATER_SOURCE : FluidInfo.FluidType.WATER_FLOW;
        // else if (block instanceof IFluidBlock)// REQUIRED: forge
        // type = FluidInfo.FluidType.FLUID_BLOCK;
        else if (block instanceof BlockLiquid)
            type = FluidInfo.FluidType.STATIONARY;
        else
            type = FluidInfo.FluidType.NONE;
        for (FluidInfo fluid : fluids)
            if (fluid.type == type && fluid.block.equals(block))
                return fluid;
        return new FluidInfo(type, block);
    }
    
    private static int getLevel(IBlockState state) {
        Comparable<?> c = state.getValue(BlockLiquid.LEVEL);
        Integer i = (Integer) c;
        return i;
    }
    
    private FluidInfo(FluidInfo.FluidType type, IBlockState b) {
        this.block = b;
        this.type = type;
        fluids.add(this);
    }
}