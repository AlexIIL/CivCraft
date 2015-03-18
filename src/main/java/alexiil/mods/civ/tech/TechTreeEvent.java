package alexiil.mods.civ.tech;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/** Fired at intervals throughout the tech tree creation process. All of the events have (at least) two fields: the tree
 * that is being created, and the NBTTagCompound that is saved to the world folder that describes the tree.
 * 
 * All of these events are fired on the main forge event bus
 * 
 * @author AlexIIL */
public abstract class TechTreeEvent extends Event {
    /** Fired before you are allowed to add to the tech tree, so before any techs have been registered. Use this to
     * register Unlockable types */
    public static class Pre extends TechTreeEvent {
        public Pre(TechTree tree, NBTTagCompound nbt) {
            super(tree, nbt);
        }
    }

    /** Fired when you should add techs to the tree */
    public static class AddTechs extends TechTreeEvent {
        public AddTechs(TechTree tree, NBTTagCompound nbt) {
            super(tree, nbt);
        }
    }

    /** Fired whenever a tech is registered with the tech tree. most of the time you wont need to use this, unless you
     * want to make some adjustments to a tech while it is being made. */
    @Cancelable()
    public static class RegisterTech extends TechTreeEvent {
        public final TechTree.Tech tech;

        public RegisterTech(TechTree tree, TechTree.Tech tech, NBTTagCompound nbt) {
            super(tree, nbt);
            this.tech = tech;
        }
    }

    @Cancelable()
    public static class RegisterUnlockable extends TechTreeEvent {
        public final Unlockable unlockable;

        public RegisterUnlockable(TechTree tree, Unlockable req, NBTTagCompound nbt) {
            super(tree, nbt);
            unlockable = req;
        }
    }

    /** Fired when you should add tech dependencies and requirements to the tech tree */
    public static class AddUnlockables extends TechTreeEvent {
        public AddUnlockables(TechTree tree, NBTTagCompound nbt) {
            super(tree, nbt);
        }
    }

    /** Fired after the tech tree has been initialised and frozen */
    public static class Post extends TechTreeEvent {
        public Post(TechTree tree, NBTTagCompound nbt) {
            super(tree, nbt);
        }
    }

    public final TechTree tree;
    /** The NBT tag compound that is written to the config file. This includes both the tech configs (under "techs") and
     * the unlockable configs (under "unlockables")) */
    public final NBTTagCompound treeNBTCompound;

    public TechTreeEvent(TechTree tree, NBTTagCompound nbt) {
        this.tree = tree;
        treeNBTCompound = nbt;
    }
}
