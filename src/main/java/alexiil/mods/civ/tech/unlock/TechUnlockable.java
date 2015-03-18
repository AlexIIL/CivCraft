package alexiil.mods.civ.tech.unlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.tech.TechTree;
import alexiil.mods.civ.tech.TechTree.Tech;
import alexiil.mods.civ.tech.Unlockable;

public abstract class TechUnlockable extends Unlockable {
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
            else if (!TechTree.currentTree.hasTech(t.name)) {
                TechTree.Tech[] nrequired = new TechTree.Tech[required.length - 1];
                for (int i = 0; i < required.length; i++) {
                    int changedIndex = ((i < index) ? i : (i - 1));
                    if (i == index)
                        continue;
                    nrequired[changedIndex] = required[i];
                }
                required = nrequired;
                index--;

                CivLog.info("The tech \"" + t.name
                        + "\" was not in the tech tree. It is most likely that this tech did not exist in the config, so ignoring this one");
            }
        }
        requiredTechs = required;
    }

    public TechUnlockable(NBTTagCompound nbt) {
        super(nbt);
        shouldShow = nbt.getBoolean("shouldShow");
        NBTTagList list = nbt.getTagList("requiredTechs", Lib.NBT.STRING);
        List<TechTree.Tech> techs = new ArrayList<TechTree.Tech>();
        for (int i = 0; i < list.tagCount(); i++) {
            TechTree.Tech t = TechTree.currentTree.getTech(list.getStringTagAt(i));
            if (t != null)
                techs.add(t);
        }
        requiredTechs = techs.toArray(new TechTree.Tech[0]);
    }

    @Override
    public void save(NBTTagCompound nbt) {
        super.save(nbt);
        nbt.setBoolean("shouldShow", shouldShow);
        NBTTagList list = new NBTTagList();
        for (Tech t : requiredTechs)
            list.appendTag(new NBTTagString(t.name));
        nbt.setTag("requiredTechs", list);
    }

    @Override
    public TechTree.Tech[] requiredTechs() {
        if (requiredTechs == null)
            return new TechTree.Tech[0];
        return Arrays.copyOf(requiredTechs, requiredTechs.length);
    }

    @Override
    public String getLocalisedName() {
        return CivCraft.instance.format(getUnlocalisedName());
    }

    @Override
    public List<String> getDescription() {
        return Collections.singletonList(CivCraft.instance.format(getUnlocalisedName() + ".desc"));
    }

    @Override
    public boolean shouldShow() {
        return shouldShow;
    }
}
