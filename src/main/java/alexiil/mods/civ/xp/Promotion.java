package alexiil.mods.civ.xp;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

public abstract class Promotion {
    public final boolean requiresAll;
    public final int xpLevel;

    public Promotion(boolean requiresAll, int level) {
        this.requiresAll = requiresAll;
        xpLevel = level;
    }

    public abstract List<Promotion> getRequiredPromotions();

    public boolean isUnlockable(EntityPlayer player) {
        int playerLevel = player.experienceLevel;
        if (playerLevel < xpLevel)
            return false;
        List<Promotion> promotions = PromotionUtils.getPromotions(player);
        return isUnlockable(promotions);
    }

    public boolean isUnlockable(List<Promotion> unlockedPromotions) {
        if (requiresAll) {
            for (Promotion promo : getRequiredPromotions())
                if (!unlockedPromotions.contains(promo))
                    return false;
            return true;
        }
        else {
            for (Promotion promo : getRequiredPromotions())
                if (unlockedPromotions.contains(promo))
                    return true;
            return false;
        }
    }

    /** Called whenever this promotion is activated, or added to the player, or whenever the player has enough XP to use
     * it */
    public abstract void onActivate(EntityPlayer player);

    /** Called whenever this promotion is deactivated, or is removed from the player, or the player no longer has enough
     * XP to use it */
    public abstract void onDeactivate(EntityPlayer player);
}
