package alexiil.mods.civ.tech;

import ibxm.Player;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import alexiil.mods.civ.CivCraft;
import alexiil.mods.civ.Lib;
import alexiil.mods.civ.compat.ModCompat;
import alexiil.mods.civ.item.ItemTechnology;
import alexiil.mods.civ.utils.TechUtils;
import alexiil.mods.civ.xp.PromotionManager;
import alexiil.mods.lib.item.IChangingItemString;

public final class TechTree {
    public enum EState {
        CONSTRUCTING, PRE, ADD_TECHS, SET_REQUIREMENTS, POST, FINALISED;
    }
    
    public class Tech implements ILocalizable {
        public static final String NAME = "name";
        public static final String BEAKERS = "beakers";
        public static final String SCIENCE_PACKS = "packs";
        /** The number of beaker items that are required to unlock this tech */
        private int[] sciencePacks = new int[0];
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
        private List<WeakReference<Unlockable>> unlockables = new ArrayList<WeakReference<Unlockable>>();
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
            setSciencePacksNeeded(required);
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
        
        public ILocalizable addRequirement(Tech required) {
            if (required == null) {
                log.warn("Tried to add a null tech to \"" + name + "\"");
                return this;
            }
            if (Arrays.asList(parents).contains(required))
                return this;
            int start = parents.length;
            String s = parentsToString();
            parents = Arrays.copyOf(parents, 1 + parents.length);
            parents[start] = required;
            log.info("Added a requirement to \"" + name + "\", the list went from " + s + " to " + parentsToString());
            if (isLeafTech())
                setLeafTech();
            return this;
        }
        
        public ILocalizable addRequirements(Tech... required) {
            for (Tech t : required)
                addRequirement(t);
            return this;
        }
        
        public ILocalizable removeRequirement(Tech required) {
            if (required == null) {
                log.warn("Tried and failed to remove a requirement from " + name + " because the argument was null!");
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
                log.warn("Tried and failed to remove \"" + required.name + "\" from " + name + ", as it did not exist in the current list "
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
            log.info("Sucsessfully removed \"" + required.name + "\" from " + name + ", the parents list went from " + parentsString + " to "
                    + parentsToString());
            return this;
        }
        
        public ILocalizable removeRequirement(Tech... required) {
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
            return sciencePacks;
        }
        
        /** This will not set the science pack array if the argument is null, or has a length of zero (but an array of
         * {0} would be permitted) */
        public Tech setSciencePacksNeeded(int[] sciencePacks) {
            if (sciencePacks == null || sciencePacks.length == 0)
                return this;
            this.sciencePacks = sciencePacks;
            return this;
        }
        
        @Override public String getUnlocalizedName() {
            return name;
        }
        
        @Override public String getLocalizedName() {
            return CivCraft.instance.format("civcraft.tech." + getUnlocalizedName());
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
        
        private void addUnlockable(Unlockable req) {
            unlockables.add(new WeakReference<Unlockable>(req));
        }
        
        public Unlockable[] getShownUnlockables() {
            Unlockable[] arr = new Unlockable[unlockables.size()];
            for (int i = 0; i < unlockables.size(); i++) {
                if (unlockables.get(i).get() == null)
                    i--;
                else if (unlockables.get(i).get().shouldShow())
                    arr[i] = unlockables.get(i).get();
                else
                    i--;
            }
            return arr;
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
        
        @Override public String toString() {
            return getLocalizedName();
            /* StringBuilder builder = new StringBuilder(); builder.append("Tech [beakers="); builder.append(beakers);
             * builder.append(", beakerTier="); builder.append(beakerTier); builder.append(", name=");
             * builder.append(name); builder.append(", parents="); builder.append(parentsToString());
             * builder.append(", children="); builder.append(techsToString()); builder.append(", requirements=");
             * builder.append(unlockables); builder.append("]"); return builder.toString(); */
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
        @SuppressWarnings("unused") private String techsToString() {
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
        @Override public int hashCode() {
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
        @Override public boolean equals(Object obj) {
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
    
    /** An Unlockable is something that is unlocked whenever all the required tech's have been researched. More
     * specifically, whenever a player unlocks a tech that is required by this unlockable, assuming ALL other tech's
     * required by this are also all unlocked, the {@link #Unlockable.unlock(Player)} method is called. This method will
     * only ever be called once per save-game, per player, assuming that no {@link #TechResearchedEvent} is fired if a
     * player has already unlocked that tech.
     * 
     * @author AlexIIL */
    public static abstract class Unlockable {
        private String name;
        
        public Unlockable(String name) {
            ModCompat compat = TechTree.currentTree.currentCompat;
            this.name = compat == null ? "unknown:" + name : compat.getUnlockableName(name);
        }
        
        public final String getName() {
            return name;
        }
        
        /** @return A list of all the tech's that this 'thing' requires to have been researched, before this itself is
         *         unlocked.
         *         <p>
         *         Note that if this list is empty or null, the {@link #unlock(EntityPlayer)} method will never be
         *         called, and this object can be considered useless.
         *         <p>
         *         Note that this should return the same array contents every time it is called, and it should be stored
         *         in a variable to speed things up.
         *         <p>
         *         Note that if any of these are either invalid tech objects or null, unknown behaviour will occur. */
        public abstract TechTree.Tech[] requiredTechs();
        
        /** @return The unlocalised name for this requirement. This should just be the name of the requirement most of the
         *         time */
        public abstract String getUnlocalisedName();
        
        /** @return The localised name for this requirement.
         *         <p>
         *         Default is just {@link I18n}.format( {@link #getUnlocalisedName()}) on the client side, server side
         *         just returns the unlocalised name */
        public abstract String getLocalisedName();
        
        /** @return The description for this unlockable, which is shown to the player */
        public abstract String getDescription();
        
        /** Called whenever the player unlocks all the tech's required for this 'thing'.
         * <p>
         * Use this if you need to do anything to the world or player, for example change the players NBT values */
        public abstract void unlock(EntityPlayer player);
        
        /** @return true if this should show up on shift-hover for {@link ItemTechnology} usages (for example).
         *         <p>
         *         The general idea of this method, is that if you want the player to be aware of this object, this
         *         should return true */
        public abstract boolean shouldShow();
        
        /** If you need to save any information about this requirement, then this is the NBTTagCompound that you do it in */
        public void save(NBTTagCompound nbt) {
            nbt.setString("name", name);
        }
        
        public boolean isUnlocked(EntityPlayer player) {
            return isUnlocked(TechUtils.getTechs(player));
        }
        
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
    }
    
    private EState state = EState.PRE;
    private Map<String, Tech> techs = new HashMap<String, Tech>();
    private Map<String, Unlockable> unlockables = new HashMap<String, Unlockable>();
    private boolean inMethod = false;
    public static TechTree currentTree = new TechTree();
    public static Logger log = LogManager.getLogger(CivCraft.modMeta.modId + ".techtree");
    private NBTTagCompound treeData;
    /** Used to add a compat-specific prefix to the unlockable name */
    private ModCompat currentCompat = null;
    public final PromotionManager promotions;
    
    public List<String> getItemTooltip(ItemStack stack, EntityPlayer player) {
        List<String> strings = new ArrayList<String>();
        for (Unlockable u : unlockables.values()) {
            if (u instanceof IChangingItemString) {
                String[] toAdd = ((IChangingItemString) u).getString(stack, player);
                for (String s : toAdd)
                    strings.add(s);
            }
        }
        return strings;
    }
    
    public TechTree() {
        state = EState.CONSTRUCTING;
        promotions = new PromotionManager(this);
    }
    
    public EState getState() {
        return state;
    }
    
    /** This initialises the tech tree. Cannot be called from itself (don't want the states being mixed up) and also
     * don't want it being called after it has been finalised (a new tech tree object should be created instead) */
    public void init(NBTTagCompound nbt) {
        if (inMethod) {
            new Exception("Tried to init the tech tree during its initialisation! This is a severe mistake, and needs be fixed!").printStackTrace();
            return;
        }
        if (state != EState.CONSTRUCTING) {
            new Exception("Tried to init the tech tree when it was in the wrong state (was " + state + ", when it needs to be in "
                    + EState.CONSTRUCTING + ")").printStackTrace();
            return;
        }
        // CivCraft.log.info("Writing the NBT Tag Compound out");
        // CivCraft.log.info(NBTUtils.toString(nbt));
        techs.clear();
        unlockables.clear();
        
        log.info("Initializing the tech tree");
        inMethod = true;
        this.treeData = nbt;
        
        state = EState.PRE;
        ModCompat.sendPreEvent(new TechTreeEvent.Pre(this, nbt));
        log.info("Pre-init of the tech tree done");
        
        state = EState.ADD_TECHS;
        addTech("agriculture");
        ModCompat.sendAddTechsEvent(new TechTreeEvent.AddTechs(this, nbt));
        log.info("Added all techs");
        
        state = EState.SET_REQUIREMENTS;
        ModCompat.sendAddUnlockableEvent(new TechTreeEvent.AddUnlockables(this, nbt));
        log.info("Added all unlockables");
        
        state = EState.POST;
        setChildren();
        techs = Collections.unmodifiableMap(techs);
        unlockables = Collections.unmodifiableMap(unlockables);
        ModCompat.sendPostEvent(new TechTreeEvent.Post(this, nbt));
        log.info("Post-init of the tech tree done");
        
        state = EState.FINALISED;
        this.treeData = null;
        inMethod = false;
    }
    
    private void setChildren() {
        for (Tech t : techs.values())
            for (Tech t0 : t.parents)
                t0.children.add(new WeakReference<Tech>(t));
        for (Tech t : techs.values())
            if (t.isLeafTech())// Force the check again
                t.setLeafTech();
    }
    
    public void save(NBTTagCompound nbt) {
        NBTTagCompound techsNBT = new NBTTagCompound();
        for (String s : techs.keySet()) {
            NBTTagCompound techNBT = new NBTTagCompound();
            techs.get(s).save(techNBT);
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
        
        nbt.setTag("promotions", promotions.saveToNBT());
    }
    
    /** @param unlock
     *            The requirement to add
     * @return The unlockable, if it was added
     * @throws Exception
     *             if this was not in the right state */
    public Unlockable addUnlockable(Unlockable unlock) {
        if (state != EState.SET_REQUIREMENTS) {
            new Exception("Tried to add an unlockable when in the wrong state! (was " + state + ", when it needs to be in " + EState.SET_REQUIREMENTS
                    + ")").printStackTrace();
            return null;
        }
        TechTreeEvent.RegisterUnlockable e = new TechTreeEvent.RegisterUnlockable(this, unlock, treeData);
        ModCompat.sendRegisterUnlockableEvent(e);
        for (Tech t : unlock.requiredTechs())
            t.addUnlockable(unlock);
        unlockables.put(unlock.name, unlock);
        log.info("Added the unlockable \"" + unlock.name + "\"");
        return unlock;
    }
    
    /** @param name
     *            The name of this tech
     * @param tier
     *            The beaker tier to init the tech to
     * @param required
     *            The techs that are required. only add the techs you add yourself, in the same method
     * @return The tech that was created
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
        else
            t = new Tech(name);
        t.addRequirements(required);
        t.setSciencePacksNeeded(packs);
        TechTreeEvent.RegisterTech e = new TechTreeEvent.RegisterTech(this, t, treeData);
        ModCompat.sendRegisterTechEvent(e);
        techs.put(name, t);
        log.info("Added the tech \"" + t.name + "\"");
        return t;
    }
    
    public Tech addTech(String name, Tech... required) {
        return addTech(name, new int[0], required);
    }
    
    public Tech getBaseTech() {
        return getTech("agriculture");
    }
    
    public Tech getTech(String name) {
        if (techs.containsKey(name))
            return techs.get(name);
        if (state == EState.FINALISED)// Don't create create techs if this tree has been finalised
            return null;
        Tech t = new Tech(name);
        techs.put(name, t);
        return t;
    }
    
    public Map<String, Tech> getTechs() {
        return Collections.unmodifiableMap(techs);
    }
    
    public Unlockable getUnlockable(String name) {
        return unlockables.getOrDefault(name, null);
    }
    
    public Map<String, Unlockable> getUnlockables() {
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
            @SuppressWarnings("unchecked") ArrayList<Tech> input2 = (ArrayList<Tech>) input.clone();
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
    
    public void setModCompat(ModCompat compat) {
        currentCompat = compat;
    }
}