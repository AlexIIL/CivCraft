package alexiil.mods.civ.compat.vanilla;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import alexiil.mods.civ.xp.Promotion;

public class PromotionMoveSpeed extends Promotion {
    public static final UUID modifierID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A2765AB6");
    
    public final int level;
    
    public PromotionMoveSpeed(int level) {
        super(true, level * 2 + 1);
        this.level = level;
    }
    
    public PromotionMoveSpeed(NBTTagCompound nbt) {
        this(nbt.getInteger("level"));
    }
    
    @Override public List<Promotion> getRequiredPromotions() {
        List<Promotion> promotions = new ArrayList<Promotion>();
        if (level <= 0)
            return promotions;
        if (level >= VanillaCompat.moveSpeeds.length)
            return promotions;
        promotions.add(VanillaCompat.moveSpeeds[level - 1]);
        return promotions;
    }
    
    @Override public void onActivate(EntityPlayer player) {
        double wantedLevel = 0.25 * (level + 1);
        IAttributeInstance iai = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        AttributeModifier oldModifier = iai.getModifier(modifierID);
        if (oldModifier == null || oldModifier.getAmount() < wantedLevel)
            return;
        AttributeModifier modifier = new AttributeModifier(modifierID, "Player Move Speed", wantedLevel, 2);
        iai.removeModifier(oldModifier);
        iai.applyModifier(modifier);
    }
    
    @Override public void onDeactivate(EntityPlayer player) {
        double wantedLevel = 0.25 * (level + 1);
        IAttributeInstance iai = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        AttributeModifier oldModifier = iai.getModifier(modifierID);
        if (oldModifier == null || oldModifier.getAmount() != wantedLevel)
            return;
        iai.removeModifier(oldModifier);
    }
}
