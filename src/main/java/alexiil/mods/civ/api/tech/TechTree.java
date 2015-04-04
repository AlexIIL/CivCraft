package alexiil.mods.civ.api.tech;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.MinecraftForge;
import alexiil.mods.civ.CivConfig;
import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.CivLog;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.api.tech.unlock.IUnlockable;
import alexiil.mods.civ.api.tech.unlock.IUnlockableConstructor;
import alexiil.mods.civ.api.tech.unlock.Unlockable;
import alexiil.mods.lib.item.IChangingItemString;

public final class TechTree {
    public enum EState {
        CONSTRUCTING, PRE, ADD_TECHS, SET_REQUIREMENTS, POST, FINALISED, SAVING;
    }

    public class Tech implements ILocalizable {
        public static final String NAME = "name";
        public static final String BEAKERS = "beakers";
        public static final String SCIENCE_PACKS = "packs";
        /** The number of beaker items that are required to unlock this tech */
        private int[] sciencePacks = new int[0];
        /** The science packs, after the config multiplier has been applied */
        private int[] adjustedSciencePacks = new int[0];
        /** The nme of this tech. This is stored to the config and items, so it should be both unlocalized and static
         * (use a good name that might be used by others, for example use "smelting" as a tech that allows for smelting
         * (as that is the action used when you put something into a vanilla furnace). If a simailarly named tech exists
         * in civ5 (or civ5: beyond earth) then use that name (so, if you add a metal, say, platinum, call your tech
         * "platinum_working" to fit the "iron_working" that exists in civ 5. (also, dont use spaces) */
        public final String name;
        /** The parents of this tech: ALL of these techs must be researched in order to even start researching this one */
        private Tech[] parents = new Tech[0];
        /** The children of this tech: after this one has been researched (and 0 to 8 other techs have ALSO been
         * researched), the child tech can be researched. this list is created automatically whenever parents are set */
        private List<WeakReference<Tech>> children = new ArrayList<WeakReference<Tech>>();
        /** The things that are unlocked automatically after researching both this tech, and any other techs that are
         * required for this. */
        private List<WeakReference<IUnlockable>> unlockables = new ArrayList<WeakReference<IUnlockable>>();
        /** A leaf tech is just a graphical way of showing the tech. A tech can only be a leaf tech if it has one parent,
         * and no children */
        private boolean leafTech;

        private Tech(String techName) {
            name = techName;
        }

        private Tech(NBTTagCompound nbt, String name) {
            this.name = name;
            NBTTagList parents = nbt.getTagList("parents", Lib.NBT.STRING);
            for (int idx = 0; idx < parents.tagCount(); idx++) {
                String parentName = parents.getStringTagAt(idx);
                Tech parent = TechTree.this.getTech(parentName);
                addRequirement(parent);
            }
            NBTTagList sciencePacks = nbt.getTagList("sciencePacks", Lib.NBT.INTEGER);
            int[] required = new int[sciencePacks.tagCount()];
            for (int idx = 0; idx < sciencePacks.tagCount(); idx++) {
                required[idx] = ((NBTTagInt) sciencePacks.get(idx)).getInt();
            }
            this.sciencePacks = required;
            leafTech = nbt.getBoolean("leaf");
            if (leafTech)
                setLeafTech();
        }

        private void save(NBTTagCompound nbt) {
            NBTTagList sciencePacksNBT = new NBTTagList();
            for (int index = 0; index < sciencePacks.length; index++)
                sciencePacksNBT.appendTag(new NBTTagInt(sciencePacks[index]));
            nbt.setTag("sciencePacks", sciencePacksNBT);
            NBTTagList parentsNBT = new NBTTagList();
            for (int index = 0; index < parents.length; index++)
                parentsNBT.appendTag(new NBTTagString(parents[index].name));
            nbt.setTag("parents", parentsNBT);
            nbt.setBoolean("leaf", leafTech);
        }

        public Tech addRequirement(Tech required) {
            if (state == EState.FINALISED || state == EState.POST) {
                CivLog.warn("Tried to add a requirement to " + name + " too late!");
                return this;
            }
            if (required == null) {
                CivLog.warn("Tried to add a null tech to \"" + name + "\"");
                return this;
            }
            if (Arrays.asList(parents).contains(required))
                return this;
            int start = parents.length;
            String s = parentsToString();
            parents = Arrays.copyOf(parents, 1 + parents.length);
            parents[start] = required;
            CivLog.debugInfo("Added a requirement to \"" + name + "\", the list went from " + s + " to " + parentsToString());
            if (isLeafTech())
                setLeafTech();
            return this;
        }

        public Tech addRequirements(Tech... required) {
            for (Tech t : required)
                addRequirement(t);
            return this;
        }

        public Tech removeRequirement(Tech required) {
            if (state == EState.FINALISED || state == EState.POST) {
                CivLog.warn("Tried to remove a requirement from " + name + " too late!");
                return this;
            }
            if (required == null) {
                CivLog.warn("Tried and failed to remove a requirement from " + name + " because the argument was null!");
                return this;
            }
            String parentsString = parentsToString();
            boolean flag = false;
            for (ILocalizable t : parents) {
                flag = t == required;
                if (flag)
                    break;
            }
            if (!flag) {
                CivLog.warn("Tried and failed to remove \"" + required.name + "\" from " + name + ", as it did not exist in the current list "
                        + parentsString);
                return this;
            }
            Tech[] newParents = new Tech[parents.length - 1];
            int i = 0;
            for (Tech p : parents)
                if (p != required) {
                    newParents[i] = p;
                    i++;
                }
            parents = newParents;
            CivLog.debugInfo("Sucsessfully removed \"" + required.name + "\" from " + name + ", the parents list went from " + parentsString + " to "
                    + parentsToString());
            return this;
        }

        public Tech removeRequirement(Tech... required) {
            for (Tech t : required)
                removeRequirement(t);
            return this;
        }

        public void setRequirements(Tech... required) {
            removeRequirement(parents);
            addRequirements(required);
        }

        public final TechTree getTechTree() {
            return TechTree.this;
        }

        public int[] getSciencePacksNeeded() {
            if (state != EState.ADD_TECHS && state != EState.SET_REQUIREMENTS)
                return adjustedSciencePacks;
            return sciencePacks;
        }

        /** This will not set the science pack array if the argument is null, or has a length of zero (but an array of
         * {0} would be permitted) */
        public Tech setSciencePacksNeeded(int[] sciencePacks) {
            if (state == EState.FINALISED || state == EState.POST || state == EState.SAVING)
                return this;
            if (sciencePacks == null || sciencePacks.length == 0)
                return this;
            this.sciencePacks = sciencePacks;
            return this;
        }

        @Override
        public String getUnlocalizedName() {
            return "civcraft.tech." + name;
        }

        @Override
        public String getLocalizedName() {
            return CivCraft.instance.format(getUnlocalizedName());
        }

        public Tech[] getChildTechs() {
            Tech[] arr = new Tech[children.size()];
            for (int i = 0; i < children.size(); i++)
                arr[i] = children.get(i).get();
            return arr;
        }

        public Tech[] getParentTechs() {
            Tech[] arr = new Tech[parents.length];
            for (int idx = 0; idx < parents.length; idx++)
                arr[idx] = parents[idx];
            return arr;
        }

        private void addUnlockable(IUnlockable req) {
            unlockables.add(new WeakReference<IUnlockable>(req));
        }

        public IUnlockable[] getShownUnlockables() {
            List<IUnlockable> arr = new ArrayList<IUnlockable>();
            for (int i = 0; i < unlockables.size(); i++) {
                if (unlockables.get(i).get() == null)
                    continue;
                else if (unlockables.get(i).get().shouldShow())
                    arr.add(unlockables.get(i).get());
            }
            return arr.toArray(new IUnlockable[0]);
        }

        public Tech setLeafTech() {
            if (state == EState.FINALISED)
                return this;
            if (children.size() == 0 && parents.length == 1)
                leafTech = true;
            else
                leafTech = false;
            return this;
        }

        public boolean isLeafTech() {
            return leafTech;
        }

        @Override
        public String toString() {
            return getLocalizedName();
        }

        private String parentsToString() {
            String s = "[";
            int l = parents.length;
            for (Tech t : parents) {
                if (t == null)
                    s += "null";
                else
                    s += t.name;
                if (l != 1)
                    s += ", ";
                l--;
            }
            return s + "]";
        }

        // method used for debugging
        @SuppressWarnings("unused")
        private String techsToString() {
            String s = "[";
            int l = children.size();
            for (WeakReference<Tech> t : children) {
                Tech t0 = t.get();
                if (t0 == null)
                    s += "null";
                else
                    s += t0.name;
                if (l != 1)
                    s += ", ";
                l--;
            }
            return s + "]";
        }

        // Eclipse Generated
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((children == null) ? 0 : children.hashCode());
            result = prime * result + (leafTech ? 1231 : 1237);
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            result = prime * result + Arrays.hashCode(parents);
            result = prime * result + Arrays.hashCode(sciencePacks);
            result = prime * result + ((unlockables == null) ? 0 : unlockables.hashCode());
            return result;
        }

        // Eclipse Generated
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Tech other = (Tech) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (children == null) {
                if (other.children != null)
                    return false;
            }
            else if (!children.equals(other.children))
                return false;
            if (leafTech != other.leafTech)
                return false;
            if (name == null) {
                if (other.name != null)
                    return false;
            }
            else if (!name.equals(other.name))
                return false;
            if (!Arrays.equals(parents, other.parents))
                return false;
            if (!Arrays.equals(sciencePacks, other.sciencePacks))
                return false;
            if (unlockables == null) {
                if (other.unlockables != null)
                    return false;
            }
            else if (!unlockables.equals(other.unlockables))
                return false;
            return true;
        }

        private TechTree getOuterType() {
            return TechTree.this;
        }
    }

    private EState state = EState.CONSTRUCTING;
    private Map<String, Tech> techs = new HashMap<String, Tech>();
    private Map<String, Tech> disabledTechs = new HashMap<String, Tech>();
    private Map<String, IUnlockable> unlockables = new HashMap<String, IUnlockable>();
    private Map<String, IUnlockableConstructor> unlockableTypes = new HashMap<String, IUnlockableConstructor>();
    /** WARNING: This will be null if a save has not been loaded yet, or it will be the old tech tree if a save has been
     * unloaded */
    public static TechTree currentTree = null;
    private final NBTTagCompound treeData;
    /** Used to add a compat-specific prefix to the unlockable name */
    private String currentPrefix = null;

    public List<String> getItemTooltip(ItemStack stack, EntityPlayer player) {
        List<String> strings = new ArrayList<String>();
        for (IUnlockable u : unlockables.values()) {
            if (u instanceof IChangingItemString) {
                String[] toAdd = ((IChangingItemString) u).getString(stack, player);
                for (String s : toAdd)
                    strings.add(s);
            }
        }
        return strings;
    }

    public TechTree(NBTTagCompound nbt) {
        TechTree oldTree = TechTree.currentTree;
        TechTree.currentTree = this;
        state = EState.CONSTRUCTING;

        CivLog.info("Initializing the tech tree");
        treeData = nbt;

        state = EState.PRE;
        MinecraftForge.EVENT_BUS.post(new TechTreeEvent.Pre(this, nbt));
        unlockableTypes = Collections.unmodifiableMap(unlockableTypes);
        CivLog.info("Pre-init of the tech tree done");

        state = EState.ADD_TECHS;
        addTech("agriculture");
        MinecraftForge.EVENT_BUS.post(new TechTreeEvent.AddTechs(this, nbt));
        CivLog.info("Added all techs");

        state = EState.SET_REQUIREMENTS;
        MinecraftForge.EVENT_BUS.post(new TechTreeEvent.AddUnlockables(this, nbt));
        CivLog.info("Added all unlockables");

        double multiplier = CivConfig.sciencePacksRequired.getDouble();

        for (Tech t : techs.values()) {
            int[] req = Arrays.copyOf(t.sciencePacks, t.sciencePacks.length);
            for (int idx = 1; idx < req.length; idx++) {
                req[idx] *= multiplier;
            }
            t.adjustedSciencePacks = req;
        }

        state = EState.POST;
        setChildren();
        techs = Collections.unmodifiableMap(techs);
        unlockables = Collections.unmodifiableMap(unlockables);
        MinecraftForge.EVENT_BUS.post(new TechTreeEvent.Post(this, nbt));
        CivLog.info("Post-init of the tech tree done");

        state = EState.FINALISED;

        TechTree.currentTree = oldTree;
    }

    public EState getState() {
        return state;
    }

    private void setChildren() {
        for (Tech t : techs.values())
            for (Tech t0 : t.parents)
                t0.children.add(new WeakReference<Tech>(t));
        for (Tech t : techs.values())
            if (t.isLeafTech())// Force the check again
                t.setLeafTech();
    }

    public NBTTagCompound save(NBTTagCompound nbt) {
        EState oldState = state;
        // This is set, so that the ACTUAL value for how many science packs is used, not the adjusted one
        state = EState.SAVING;

        NBTTagCompound techsNBT = new NBTTagCompound();
        for (String s : techs.keySet()) {
            NBTTagCompound techNBT = new NBTTagCompound();
            techs.get(s).save(techNBT);
            techsNBT.setTag(s, techNBT);
        }
        for (String s : disabledTechs.keySet()) {
            NBTTagCompound techNBT = new NBTTagCompound();
            disabledTechs.get(s).save(techNBT);
            techNBT.setBoolean("disabled", true);
            techsNBT.setTag(s, techNBT);
        }
        nbt.setTag("techs", techsNBT);
        NBTTagCompound reqsNBT = new NBTTagCompound();
        for (String s : unlockables.keySet()) {
            NBTTagCompound reqNBT = new NBTTagCompound();
            unlockables.get(s).save(reqNBT);
            reqsNBT.setTag(s, reqNBT);
        }
        nbt.setTag("unlockables", reqsNBT);

        state = oldState;

        return nbt;
    }

    /** @param unlock
     *            The requirement to add
     * @return The unlockable, if it was added
     * @throws Exception
     *             if this was not in the right state */
    public IUnlockable addUnlockable(IUnlockable unlock) {
        if (state != EState.SET_REQUIREMENTS) {
            new Exception("Tried to add an unlockable when in the wrong state! (was " + state + ", when it needs to be in " + EState.SET_REQUIREMENTS
                    + ")").printStackTrace();
            return null;
        }
        if (!unlockableTypes.containsKey(unlock.getType()))
            CivLog.warn("Adding an unlockable (type == " + unlock.getType()
                    + ") that is not contained in the type map! This means that this unlockable will NOT persist through saves or in the config");
        TechTreeEvent.RegisterUnlockable e = new TechTreeEvent.RegisterUnlockable(this, unlock, treeData);
        MinecraftForge.EVENT_BUS.post(e);
        if (e.isCanceled())
            return unlock;
        for (Tech t : unlock.requiredTechs()) {
            if (techs.containsValue(t))
                t.addUnlockable(unlock);
        }
        unlockables.put(unlock.getName(), unlock);
        CivLog.debugInfo("Added the unlockable \"" + unlock.getName() + "\"");
        return unlock;
    }

    /** @param name
     *            The name of this tech
     * @param tier
     *            The beaker tier to init the tech to
     * @param required
     *            The techs that are required. only add the techs you add yourself, in the same method
     * @return The tech that was created. If the addition of this tech was cancelled by some-one, the returned tech
     *         object is useless
     * @throws Exception
     *             If something went wrong during this */
    public Tech addTech(String name, int[] packs, Tech... required) {
        if (state != EState.ADD_TECHS) {
            new Exception("Tried to add a tech when in the wrong state! (was " + state + ", when it needs to be in " + EState.ADD_TECHS + ")")
                    .printStackTrace();
            return null;
        }
        Tech t;
        if (techs.containsKey(name))
            t = techs.get(name);
        else if (disabledTechs.containsKey(name))
            t = disabledTechs.get(name);
        else
            t = new Tech(name);
        t.addRequirements(required);
        t.setSciencePacksNeeded(packs);
        TechTreeEvent.RegisterTech e = new TechTreeEvent.RegisterTech(this, t, treeData);
        MinecraftForge.EVENT_BUS.post(e);
        if (e.isCanceled()) {
            CivLog.debugInfo("Canceled the addition of the tech \"" + t.name + "\"");
            disabledTechs.put(name, t);
            return t;
        }
        else if (disabledTechs.containsValue(t)) {
            disabledTechs.remove(name);
        }
        techs.put(name, t);
        CivLog.debugInfo("Added the tech \"" + t.name + "\"");
        return t;
    }

    public Tech addTech(String name, Tech... required) {
        return addTech(name, new int[0], required);
    }

    public Tech getBaseTech() {
        return getTech("agriculture");
    }

    public boolean hasTech(String name) {
        return techs.containsKey(name);
    }

    public Tech getTech(String name) {
        if (techs.containsKey(name))
            return techs.get(name);
        if (state != EState.ADD_TECHS)// Don't create create techs if this tree has been finalised
            return null;
        return addTech(name);
    }

    public Map<String, Tech> getTechs() {
        return Collections.unmodifiableMap(techs);
    }

    public IUnlockable getUnlockable(String name) {
        if (unlockables.containsKey(name))
            return unlockables.get(name);
        return null;
    }

    public Map<String, IUnlockable> getUnlockables() {
        return Collections.unmodifiableMap(unlockables);
    }

    public Tech getResult(ArrayList<Tech> input, int firstSlot) {
        ArrayList<Tech> allUses = new ArrayList<Tech>();
        for (Tech t : input)
            for (WeakReference<Tech> t1 : t.children)
                if (!allUses.contains(t1.get()))
                    allUses.add(t1.get());
        ArrayList<Tech> uses = new ArrayList<Tech>();
        for (Tech t : allUses) {
            if (input.size() != t.parents.length)
                continue;
            @SuppressWarnings("unchecked")
            ArrayList<Tech> input2 = (ArrayList<Tech>) input.clone();
            for (ILocalizable t1 : t.parents) {
                if (!input2.remove(t1))
                    break;
            }
            if (input2.size() == 0)
                uses.add(t);
        }
        if (firstSlot >= uses.size())
            firstSlot = uses.size() - 1;
        if (firstSlot >= 0)
            return uses.get(firstSlot);
        return null;
    }

    /** You MUST call this before registering any unlockable's (with {@link #addUnlockable(Unlockable)}) */
    public void setUnlockablePrefix(String modId) {
        currentPrefix = modId;
    }

    public String getUnlockablePrefix() {
        return currentPrefix;
    }

    /** Register unlockable types here. When java 8 is used in forge, calls to this will look a whole lot nicer. */
    public void registerUnlockable(String type, IUnlockableConstructor unlockable) {
        if (state != EState.PRE) {
            CivLog.warn("Tried to register an unlockable type outside of PRE-INIT. This is not meant to happen, change this code please!");
            return;
        }
        if (unlockableTypes.containsKey(type))
            CivLog.warn("\"" + type + "\" is already registered! Replacing it with the new one...");
        unlockableTypes.put(type, unlockable);
        CivLog.debugInfo("Registered \"" + type + "\" as an unlockable");
    }

    /** Register an unlockable type, specifying a class to load from. Unlike the other method, this class MUST have a
     * constructor with a single NBTTagCompound argument. Use the other register method if you want more control over
     * which class is returned. */
    public void registerUnlockable(String type, final Class<? extends Unlockable> unlockableClass) {
        registerUnlockable(type, new IUnlockableConstructor() {
            @Override
            public Unlockable createUnlockable(NBTTagCompound nbt) {
                try {
                    return unlockableClass.getConstructor(NBTTagCompound.class).newInstance(nbt);
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
                return null;
            }
        });
    }

    public Map<String, IUnlockableConstructor> getUnlockableTypes() {
        return unlockableTypes;
    }
}
