package alexiil.mods.civ.tech;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import alexiil.mods.civ.utils.CraftUtils;

public class BeakerEarningListener {
    public static final BeakerEarningListener instance = new BeakerEarningListener();

    @SubscribeEvent
    public void playerTick(PlayerTickEvent event) {
        if (event.phase != Phase.END)
            return;

        PlayerResearchHelper.decrementCooldown(event.player);

        // Handle exploration, but only server side
        if (event.player.worldObj.isRemote)
            return;
        boolean exploredNew = CraftUtils.addPlayerToChunk(event.player.worldObj, event.player.getPosition(), event.player);
        if (exploredNew)
            PlayerResearchHelper.progressResearch(event.player, "explore", 1, false);
    }

    /** @return true if the player is a real player (so not a fake one) */
    public static boolean isPlayerFake(EntityPlayer player) {
        if (player == null)
            return true;
        return (player instanceof FakePlayer);
    }

    // Beaker earning events

    @SubscribeEvent
    public void playerBreakBlock(BreakEvent event) {
        if (event.world.isRemote)
            return;
        EntityPlayer player = event.getPlayer();
        if (isPlayerFake(player))
            return;
        int fortune = EnchantmentHelper.getFortuneModifier(player);
        String name = event.state.getBlock().getUnlocalizedName();
        boolean harvest = false;
        List<ItemStack> stacks = event.state.getBlock().getDrops(event.world, event.pos, event.state, fortune);
        if (stacks.size() == 1) {
            ItemStack stack = stacks.get(0);
            Item item = stack.getItem();
            if (item == null)
                ;
            else if (item instanceof ItemBlock) {
                ItemBlock ib = (ItemBlock) item;
                Block b = ib.block;
                harvest = b != event.state.getBlock();
            }
            else
                harvest = true;
        }
        else
            harvest = stacks.size() != 0;
        if (name.startsWith("tile."))
            name = name.substring(5);
        if (!harvest)
            PlayerResearchHelper.progressResearch(player, "block.break." + name, 1, true);
        else
            PlayerResearchHelper.progressResearch(player, "block.harvest." + name, 1, true);
    }

    @SubscribeEvent
    public void playerCrafted(ItemCraftedEvent event) {
        if (event.player.worldObj.isRemote)
            return;
        if (isPlayerFake(event.player))
            return;
        if (event.crafting == null || event.crafting.getItem() == null)
            return;
        String itemName = "craft." + event.crafting.getItem().getUnlocalizedName(event.crafting);
        PlayerResearchHelper.progressResearch(event.player, itemName, 1, true);
    }

    @SubscribeEvent
    public void entityAttack(LivingHurtEvent event) {
        if (event.isCanceled())
            return;
        if (event.entity.worldObj.isRemote)
            return;
        EntityPlayer player;
        boolean arrow = false;
        double distance = 0;
        if (event.source.getSourceOfDamage() instanceof EntityPlayer)
            player = (EntityPlayer) event.source.getSourceOfDamage();
        else if (event.source.getSourceOfDamage() instanceof EntityArrow) {
            if (((EntityArrow) event.source.getSourceOfDamage()).shootingEntity instanceof EntityPlayer) {
                player = (EntityPlayer) ((EntityArrow) event.source.getSourceOfDamage()).shootingEntity;
                arrow = true;
                distance = event.entity.getDistanceSqToEntity(player);
            }
            else
                return;
        }
        else
            return;
        if (isPlayerFake(player))
            return;
        if (event.entityLiving == null)
            return;
        String entName = "entity.attack." + EntityList.getEntityString(event.entityLiving);
        PlayerResearchHelper.progressResearch(player, entName, 1, true);
        if (arrow)
            PlayerResearchHelper.progressResearch(player, "entity.arrowHit", distance, true);
    }

    @SubscribeEvent
    public void entityDeath(LivingDeathEvent event) {
        if (event.isCanceled())
            return;
        if (event.entity.worldObj.isRemote)
            return;
        if (event.source.getSourceOfDamage() == null)
            return;
        EntityPlayer player;
        Entity ent = event.source.getSourceOfDamage();
        boolean arrow = false;
        double distance = 0;
        if (ent instanceof EntityPlayer)
            player = (EntityPlayer) event.source.getSourceOfDamage();
        else if (ent instanceof EntityArrow) {
            if (((EntityArrow) ent).shootingEntity instanceof EntityPlayer) {
                player = (EntityPlayer) ((EntityArrow) ent).shootingEntity;
                arrow = true;
                distance = event.entity.getDistanceSqToEntity(player);
            }
            else
                return;
        }
        else
            return;
        if (isPlayerFake(player))
            return;
        String name = "entity.kill." + EntityList.getEntityString(event.entity);
        PlayerResearchHelper.progressResearch(player, name, 1, true);
        if (arrow)
            PlayerResearchHelper.progressResearch(player, "entity.arrowHit", distance, true);
    }

    // TODO: item related stuffs. So, firing (where an infinity enchantment is less than normal) of bows and
    // breaking of tools
}
