package alexiil.mods.lib.block;

public class BlockSpace {/* protected int[][][] colours = new int[16][16][16]; private static boolean[][][] rendered =
                          * new boolean[16][16][16]; private BlockSpace(int[][][] colours) { this.colours = colours; }
                          * public BlockSpace() { setColour(0, 0, 0, 0x000000); setColour(0, 0, 15, 0x0000FF);
                          * setColour(0, 15, 0, 0x00FF00); setColour(0, 15, 15, 0x00FFFF); setColour(15, 0, 0,
                          * 0xFF0000); setColour(15, 0, 15, 0xFF00FF); setColour(15, 15, 0, 0xFFFF00); setColour(15, 15,
                          * 15, 0xFFFFFF); } public void setColour(int x, int y, int z, int colour) { if
                          * (isValidCoord(x, y, z)) colours[x][y][z] = colour; } public int getColour(int x, int y, int
                          * z) { if (isValidCoord(x, y, z)) return colours[x][y][z]; return 0; } public static
                          * BlockSpace loadFromNBT(NBTTagCompound nbt) { int[] integers = nbt.getIntArray("Colours"); if
                          * (integers == null) return null; if (integers.length != 16 * 16 * 16) return null; int[][][]
                          * space = new int[16][16][16]; int i = 0; for (int x = 0; x < 16; x++) for (int y = 0; y < 16;
                          * y++) for (int z = 0; z < 16; z++) { space[x][y][z] = integers[i]; i++; } return new
                          * BlockSpace(space); } public NBTTagCompound writeToNBT(NBTTagCompound nbt) { int[] integers =
                          * new int[16 * 16 * 16]; int i = 0; for (int x = 0; x < 16; x++) for (int y = 0; y < 16; y++)
                          * for (int z = 0; z < 16; z++) { integers[i] = colours[x][y][z]; i++; }
                          * nbt.setIntArray("Colours", integers); return nbt; }
                          * @SideOnly(Side.CLIENT) public void addDrawList() { for (int x = 0; x < 16; x++) for (int y =
                          * 0; y < 16; y++) for (int z = 0; z < 16; z++) rendered[x][y][z] = colours[x][y][z] != 0; for
                          * (int y = 0; y < 16; y++) for (int x = 15; x >= 0; x--) for (int z = 15; z >= 0; z--) if
                          * (rendered[x][y][z]) renderInsideBlock(x, y, z); }
                          * @SideOnly(Side.CLIENT) private boolean isRenderedAt(int x, int y, int z) { if
                          * (!isValidCoord(x, y, z)) return false; return rendered[x][y][z]; } private boolean
                          * isValidCoord(int x, int y, int z) { return (x >= 0 && x < 16) && (y >= 0 && y < 16) && (z >=
                          * 0 && z < 16); }
                          * @SideOnly(Side.CLIENT) private void renderInsideBlock(int x, int y, int z) { Tessellator
                          * tess = Tessellator.instance; tess.addTranslation(x, y, z); IIcon icon =
                          * Blocks.iron_block.getIcon(0, 0); tess.setTextureUV(icon.getInterpolatedU(2),
                          * icon.getInterpolatedV(6)); tess.setColorRGBA_I(colours[x][y][z], 0xFF); if (!isRenderedAt(x,
                          * y + 1, z))// FINE { tess.addVertex(0, 1, 1); tess.addVertex(1, 1, 1); tess.addVertex(1, 1,
                          * 0); tess.addVertex(0, 1, 0); } if (!isRenderedAt(x - 1, y, z))// FINE { tess.addVertex(0, 0,
                          * 1); tess.addVertex(0, 1, 1); tess.addVertex(0, 1, 0); tess.addVertex(0, 0, 0); } if
                          * (!isRenderedAt(x, y, z + 1))// FINE { tess.addVertex(1, 0, 1); tess.addVertex(1, 1, 1);
                          * tess.addVertex(0, 1, 1); tess.addVertex(0, 0, 1); } if (!isRenderedAt(x + 1, y, z))// FINE {
                          * tess.addVertex(1, 0, 0); tess.addVertex(1, 1, 0); tess.addVertex(1, 1, 1); tess.addVertex(1,
                          * 0, 1); } if (!isRenderedAt(x, y, z - 1))// FINE { tess.addVertex(0, 0, 0); tess.addVertex(0,
                          * 1, 0); tess.addVertex(1, 1, 0); tess.addVertex(1, 0, 0); } if (!isRenderedAt(x, y - 1, z))//
                          * FINE { tess.addVertex(1, 0, 1); tess.addVertex(0, 0, 1); tess.addVertex(0, 0, 0);
                          * tess.addVertex(1, 0, 0); } tess.addTranslation(-x, -y, -z); } */
}
