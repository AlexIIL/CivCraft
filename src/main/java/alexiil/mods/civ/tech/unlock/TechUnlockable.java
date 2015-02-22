package alexiil.mods.civ.tech.unlock;

import java.util.Arrays;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;

public abstract class TechUnlockable extends TechTree.Unlockable {
    private final TechTree.Tech[] requiredTechs;
    private final boolean shouldShow;
    
    /** @param required
     *            The technologies that are required for this object
     * @param show
     *            If this should be shown to the player when they look for the uses of tech's */
    public TechUnlockable(String name, TechTree.Tech[] required, boolean show) {
        super(name);
        shouldShow = show;
        if (required == null) {
            requiredTechs = null;
            new Exception(new NullPointerException("This must require at least one tech")).printStackTrace();
            return;
        }
        if (required.length <= 0) {
            requiredTechs = null;
            new UnsupportedOperationException("This must require at least 1 Tech").printStackTrace();
            return;
        }
        for (int index = 0; index < required.length; index++) {
            TechTree.Tech t = required[index];
            if (t == null) {
                requiredTechs = null;
                new NullPointerException("The tech found at position " + index
                        + " was null. This is most likely a bug, and would have caused issues later down the line.").printStackTrace();
                return;
            }
        }
        requiredTechs = required;
    }
    
    public TechUnlockable(TechTree tree, String name, String[] required, boolean show) {
        super(name);
        shouldShow = show;
        if (required == null) {
            requiredTechs = null;
            new NullPointerException("This must require at least one tech (was null)").printStackTrace();
            return;
        }
        if (required.length <= 0) {
            requiredTechs = null;
            new UnsupportedOperationException("This must require at least one Tech (was an empty list)").printStackTrace();
            return;
        }
        TechTree.Tech[] techs = new TechTree.Tech[required.length];
        for (int index = 0; index < required.length; index++) {
            String t = required[index];
            if (t == null) {
                requiredTechs = null;
                new NullPointerException("The string found at position " + index
                        + " was null. This is most likely a bug, and would have caused issues later down the line if it was allowed.")
                        .printStackTrace();
                return;
            }
            if (tree.getTech(t) != null) {// If this exists, then use that
                techs[index] = tree.getTech(t);
            }
            else {
                requiredTechs = null;
                new NullPointerException(
                        "The string found at position "
                                + index
                                + "("
                                + required[index]
                                + ") did not have a corresponding tech. This is most likely a bug, and would have caused issues later down the line if it was allowed.")
                        .printStackTrace();
                return;
            }
        }
        requiredTechs = techs;
    }
    
    @Override public TechTree.Tech[] requiredTechs() {
        if (requiredTechs == null)
            return new TechTree.Tech[0];
        return Arrays.copyOf(requiredTechs, requiredTechs.length);
    }
    
    @Override public String getLocalisedName() {
        return CivCraft.instance.format(getUnlocalisedName());
    }
    
    @Override public String getDescription() {
        return CivCraft.instance.format(getUnlocalisedName() + ".desc");
    }
    
    @Override public boolean shouldShow() {
        return shouldShow;
    }
    
    @Override public void save(NBTTagCompound nbt) {
        super.save(nbt);
        for (int i = 0; i < requiredTechs.length; i++)
            nbt.setString("techRequired" + i, requiredTechs[i].name);
        nbt.setBoolean("shouldShow", shouldShow);
        NBTTagList list = new NBTTagList();
        for (Tech t : requiredTechs)
            list.appendTag(new NBTTagString(t.name));
        nbt.setTag("techRequired", list);
    }
}
