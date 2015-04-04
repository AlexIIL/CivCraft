package alexiil.mods.civ.api.tech.unlock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.api.tech.TechTree;
import alexiil.mods.civ.api.tech.TechTree.Tech;
import alexiil.mods.civ.utils.TechUtils;

/** Holds most of a base implementation of IUnlockable, it is recommended that you extend this insted of IUnlockable for
 * simplicity */
public abstract class Unlockable implements IUnlockable {
    private String name;
    /** If this is false, then it wont be saved in the config. Useful if you have settings that cannot be deduced from a
     * config, and need to be specially coded */
    protected boolean isLoadable = true;
    private final TechTree.Tech[] requiredTechs;
    private final boolean shouldShow;

    public static IUnlockable loadUnlockable(NBTTagCompound nbt) {
        String type = nbt.getString("type");
        boolean isLoadable = nbt.getBoolean("isLoadable");
        if (!isLoadable)
            return null;
        Map<String, IUnlockableConstructor> map = TechTree.currentTree.getUnlockableTypes();
        IUnlockableConstructor cons = map.get(type);
        if (cons == null) {
            CivLog.warn("Tried to load an unlockable of type \"" + type + "\", but it did not exist in the map!");
            return null;
        }
        return cons.createUnlockable(nbt);
    }

    public Unlockable(String name, TechTree.Tech[] required, boolean show) {
        this.name = TechTree.currentTree.getUnlockablePrefix() + ":" + name;
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

    protected Unlockable(NBTTagCompound nbt) {
        name = nbt.getString("name");
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

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#getName() */
    @Override
    public final String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#getUnlocalisedName() */
    @Override
    public abstract String getUnlocalisedName();

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#getLocalisedName() */
    @Override
    public String getLocalisedName() {
        return CivCraft.instance.format(getUnlocalisedName());
    }

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#getDescription() */
    @Override
    public List<String> getDescription() {
        return Collections.singletonList(CivCraft.instance.format(getUnlocalisedName() + ".desc"));
    }

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#unlock(net.minecraft.entity.player.EntityPlayer) */
    @Override
    public abstract void unlock(EntityPlayer player);

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#shouldShow() */
    @Override
    public boolean shouldShow() {
        return shouldShow;
    }

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#isUnlocked(net.minecraft.entity.player.EntityPlayer) */
    @Override
    public boolean isUnlocked(EntityPlayer player) {
        return isUnlocked(TechUtils.getTechs(player));
    }

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#isUnlocked(java.util.List) */
    @Override
    public boolean isUnlocked(List<Tech> techs) {
        for (Tech t : requiredTechs()) {
            boolean hasFound = false;
            for (Tech t0 : techs) {
                if (t == t0)
                    hasFound = true;
            }
            if (!hasFound)
                return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#getType() */
    @Override
    public abstract String getType();

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#save(net.minecraft.nbt.NBTTagCompound) */
    @Override
    public void save(NBTTagCompound nbt) {
        nbt.setString("type", getType());
        nbt.setString("name", name);
        nbt.setBoolean("isLoadable", isLoadable);
        nbt.setBoolean("shouldShow", shouldShow);
        NBTTagList list = new NBTTagList();
        for (Tech t : requiredTechs)
            list.appendTag(new NBTTagString(t.name));
        nbt.setTag("requiredTechs", list);
    }

    /* (non-Javadoc)
     * @see alexiil.mods.civ.api.tech.unlock.IUnlockable#requiredTechs() */
    @Override
    public TechTree.Tech[] requiredTechs() {
        if (requiredTechs == null)
            return new TechTree.Tech[0];
        return Arrays.copyOf(requiredTechs, requiredTechs.length);
    }
}
