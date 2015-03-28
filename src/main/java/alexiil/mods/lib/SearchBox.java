package alexiil.mods.lib;

import java.util.ArrayDeque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/** Currently unused, this class allowed for searching a give volume, incrementing the position at any time. This also
 * allows for adding coordinates to search at any time. */
public class SearchBox {
    public BlockPos min, max, cur;
    /** This determines whether to increment or decrement the value. */
    public boolean posX, posY, posZ;
    public ArrayDeque<BlockPos> toSearch;
    public boolean doOnce, isDone = false;
    public long progress = 0;
    public long size;

    public SearchBox(SearchBox box) {
        super();
        max = box.max;
        min = box.min;
        cur = box.cur;
        resetCurrent();
        calcSize();
    }

    public SearchBox(NBTTagCompound nbt) {
        this.readFromNBT(nbt);
    }

    public SearchBox(BlockPos min, BlockPos max) {
        this.min = min;
        this.max = max;
        this.cur = min;
    }

    public SearchBox(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        min = new BlockPos(minX, minY, minZ);
        max = new BlockPos(maxX, maxY, maxZ);
        resetCurrent();
        calcSize();
    }

    public void writeToNBT(NBTTagCompound nbt) {
        BlockPosUtils.saveToNBT(min, "min");
        BlockPosUtils.saveToNBT(max, "max");
        BlockPosUtils.saveToNBT(cur, "cur");
        nbt.setBoolean("DirectionX", posX);
        nbt.setBoolean("DirectionY", posY);
        nbt.setBoolean("DirectionZ", posZ);
        nbt.setInteger("ToSearch", toSearch.size());
        nbt.setLong("Progress", progress);
        for (int a = toSearch.size() - 1; a >= 0; a--)
            BlockPosUtils.saveToNBT(toSearch.pop(), Integer.toString(a));
        nbt.setBoolean("doOnce", doOnce);
    }

    public void readFromNBT(NBTTagCompound nbt) {
        min = BlockPosUtils.readFromNBT(nbt, "min");
        max = BlockPosUtils.readFromNBT(nbt, "min");
        cur = BlockPosUtils.readFromNBT(nbt, "min");
        posX = nbt.getBoolean("DirectionX");
        posY = nbt.getBoolean("DirectionY");
        posZ = nbt.getBoolean("DirectionZ");
        progress = nbt.getLong("Progress");
        int cors = nbt.getInteger("ToSearch");
        toSearch = new ArrayDeque<BlockPos>(cors);// TODO: Test this!!!!!!!!!!!
        for (int a = 0; a < cors; a++)
            toSearch.push(BlockPosUtils.readFromNBT(nbt, Integer.toString(a)));
        calcSize();
        doOnce = nbt.getBoolean("doOnce");
    }

    public void calcSize() {
        size = (max.getX() - min.getX() + 1) * (max.getY() - min.getY() + 1) * (max.getZ() - min.getZ() + 1);
    }

    public boolean isDone() {
        return isDone;// progress == size;
    }

    public void expand(int amount) {
        min = min.offset(EnumFacing.DOWN).offset(EnumFacing.NORTH).offset(EnumFacing.WEST);
        max = max.offset(EnumFacing.UP).offset(EnumFacing.SOUTH).offset(EnumFacing.EAST);
        calcSize();
    }

    public void resetCurrent() {
        cur = new BlockPos(min.getX(), max.getY(), min.getZ());
        posX = true;
        posY = false;
        posZ = true;
        toSearch = new ArrayDeque<BlockPos>();
    }

    public void contract(int amount) {
        expand(-amount);
    }

    private void calcNext() {
        progress++;
        if (cur.getX() == max.getX() && posX)
            posX = false;
        else if (cur.getX() == min.getX() && !posX)
            posX = true;
        else {
            cur = cur.offset(posX ? EnumFacing.EAST : EnumFacing.WEST);
            return;
        }

        if (cur.getZ() == max.getZ() && posZ)
            posZ = false;
        else if (cur.getZ() == min.getZ() && !posZ)
            posZ = true;
        else {
            cur = cur.offset(posZ ? EnumFacing.SOUTH : EnumFacing.NORTH);
            return;
        }

        if (doOnce && cur.getY() == min.getY()) {
            isDone = true;
            return;
        }
        if (cur.getY() == max.getY() && posY)
            posY = false;
        else if (cur.getY() == min.getY() && !posY)
            posY = true;
        else {
            cur = cur.offset(posY ? EnumFacing.UP : EnumFacing.DOWN);
            return;
        }
    }

    public boolean isOnEdge(BlockPos pos) {
        return getMatches(pos) > 0;
    }

    public boolean isInside(BlockPos pos) {
        return pos.getX() >= min.getX() && pos.getX() <= max.getX() && pos.getY() >= min.getY() && pos.getY() <= max.getY()
                && pos.getZ() >= min.getZ() && pos.getZ() <= max.getZ();
    }

    public boolean isOnVertex(BlockPos pos) {
        return getMatches(pos) > 1;
    }

    public int getMatches(BlockPos pos) {
        int matches = 0;
        if (pos.getX() == min.getX() || pos.getX() == max.getX())
            matches++;
        if (pos.getY() == min.getY() || pos.getY() == max.getY())
            matches++;
        if (pos.getZ() == min.getZ() || pos.getZ() == max.getZ())
            matches++;
        return matches;
    }

    public BlockPos current() {
        if (toSearch.isEmpty())
            return cur;
        return toSearch.peek();
    }

    public BlockPos next() {
        if (toSearch.isEmpty()) {
            calcNext();
            return current();
        }
        return toSearch.pop();
    }

    @Override
    public String toString() {
        return progress + "/" + size;
    }

    public void pushNext(BlockPos pos) {
        toSearch.push(pos);
    }
}
