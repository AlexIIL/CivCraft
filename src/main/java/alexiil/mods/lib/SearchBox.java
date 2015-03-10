package alexiil.mods.lib;

import java.util.ArrayDeque;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/** Currently unused, this class allowed for searching a give volume, incrementing the position at any time. This also
 * allows for adding coordinates to search at any time. */
public class SearchBox {
    public Coordinate min, max, cur;
    /** This determines whether to increment or decrement the value. */
    public boolean posX, posY, posZ;
    public ArrayDeque<Coordinate> toSearch;
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
        this.min = new Coordinate(min);
        this.max = new Coordinate(max);
        this.cur = new Coordinate(min.getX(), max.getY(), min.getZ());
    }
    
    public SearchBox(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        min = new Coordinate(minX, minY, minZ);
        max = new Coordinate(maxX, maxY, maxZ);
        resetCurrent();
        calcSize();
    }
    
    public void writeToNBT(NBTTagCompound nbt) {
        min.writeToNBT(nbt, "PosMin");
        max.writeToNBT(nbt, "PosMax");
        cur.writeToNBT(nbt, "PosCur");
        nbt.setBoolean("DirectionX", posX);
        nbt.setBoolean("DirectionY", posY);
        nbt.setBoolean("DirectionZ", posZ);
        nbt.setInteger("ToSearch", toSearch.size());
        nbt.setLong("Progress", progress);
        for (int a = toSearch.size() - 1; a >= 0; a--)
            toSearch.pop().writeToNBT(nbt, Integer.toString(a));
        nbt.setBoolean("doOnce", doOnce);
    }
    
    public void readFromNBT(NBTTagCompound nbt) {
        min = new Coordinate(nbt, "PosMin");
        max = new Coordinate(nbt, "PosMax");
        cur = new Coordinate(nbt, "PosCur");
        posX = nbt.getBoolean("DirectionX");
        posY = nbt.getBoolean("DirectionY");
        posZ = nbt.getBoolean("DirectionZ");
        progress = nbt.getLong("Progress");
        int cors = nbt.getInteger("ToSearch");
        toSearch = new ArrayDeque<Coordinate>(cors);// TODO: Test this!!!!!!!!!!!
        for (int a = 0; a < cors; a++)
            toSearch.push(new Coordinate(nbt, Integer.toString(a)));
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
        min = min.moveCoordinate(EnumFacing.DOWN).moveCoordinate(EnumFacing.NORTH).moveCoordinate(EnumFacing.WEST);
        max = max.moveCoordinate(EnumFacing.UP).moveCoordinate(EnumFacing.SOUTH).moveCoordinate(EnumFacing.EAST);
        calcSize();
    }
    
    public void resetCurrent() {
        cur = new Coordinate(min.getX(), max.getY(), min.getZ());
        posX = true;
        posY = false;
        posZ = true;
        toSearch = new ArrayDeque<Coordinate>();
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
            cur = cur.moveCoordinate(posX ? EnumFacing.EAST : EnumFacing.WEST);
            return;
        }
        
        if (cur.getZ() == max.getZ() && posZ)
            posZ = false;
        else if (cur.getZ() == min.getZ() && !posZ)
            posZ = true;
        else {
            cur = cur.moveCoordinate(posZ ? EnumFacing.SOUTH : EnumFacing.NORTH);
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
            cur = cur.moveCoordinate(posY ? EnumFacing.UP : EnumFacing.DOWN);
            return;
        }
    }
    
    public boolean isOnEdge(Coordinate cor) {
        return getMatches(cor) > 0;
    }
    
    public boolean isInside(Coordinate cor) {
        return cor.getX() >= min.getX() && cor.getX() <= max.getX() && cor.getY() >= min.getY() && cor.getY() <= max.getY()
                && cor.getZ() >= min.getZ() && cor.getZ() <= max.getZ();
    }
    
    public boolean isOnVertex(Coordinate cor) {
        return getMatches(cor) > 1;
    }
    
    public int getMatches(Coordinate cor) {
        int matches = 0;
        if (cor.getX() == min.getX() || cor.getX() == max.getX())
            matches++;
        if (cor.getY() == min.getY() || cor.getY() == max.getY())
            matches++;
        if (cor.getZ() == min.getZ() || cor.getZ() == max.getZ())
            matches++;
        return matches;
    }
    
    public Coordinate current() {
        if (toSearch.isEmpty())
            return cur;
        return toSearch.peek();
    }
    
    public Coordinate next() {
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
    
    public void pushNext(Coordinate cor) {
        toSearch.push(cor);
    }
}
