package alexiil.mods.civ.event;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/** This event is fired whenever a crafting-type inventory tries to either craft, or show a crafting possibility given a
 * set of items going in and out. If this event is cancelled, then the craft will not be shown as a possibility.
 * 
 * @author AlexIIL */
@Cancelable
public class FindMatchingRecipeEvent extends Event {
    /** This shows the type of crafting that is ocouring. It is recommended that if you have a category that doesn't fit
     * into these that you use {@link #OTHER}. */
    /* OR maybe that this is just a String with constants expressed here? that would allow for many more possibilities,
     * but I don't know if thats actually helpful, unless an ore-dictionary type naming convention was here, and even
     * then, what would you do with that? */
    public static enum EType {
        CRAFT, SMELT, OTHER;
    }

    public final World world;
    public final ItemStack[] input, output;
    public final EType type;

    /** Either of the item stack arrays can be null: they will be initialised to empty arrays if they are null. Type will
     * be initialised to EType.OTHER */
    public FindMatchingRecipeEvent(World world, ItemStack[] in, ItemStack[] out, EType type) {
        input = in == null ? new ItemStack[0] : in;
        output = out == null ? new ItemStack[0] : out;
        this.world = world;
        this.type = type == null ? EType.OTHER : type;
        // Is this really wanted? or just throw an NPE early?
    }

    /** This is fired whenever a block tries to craft with items. More specifically, this is when a block, or multiple
     * blocks but this as the centre, try to craft something. */
    public static class Block extends FindMatchingRecipeEvent {
        public final BlockPos pos;

        public Block(World world, BlockPos pos, ItemStack[] in, ItemStack[] out, EType type) {
            super(world, in, out, type);
            this.pos = pos;
        }
    }

    /** This is fired whenever a player tires to craft items. More specifically, this is whenever a player, and only one
     * player tries to craft something. */
    public static class Player extends FindMatchingRecipeEvent {
        public final EntityPlayer player;

        public Player(World world, EntityPlayer player, ItemStack[] in, ItemStack[] out, EType type) {
            super(world, in, out, type);
            this.player = player;
        }
    }
}
