package alexiil.mods.civ;

import alexiil.mods.civ.item.CivItems;
import alexiil.mods.lib.item.ItemBase;

public enum EProducedType {
    HAMMER(CivItems.hammer), SCIENCE(CivItems.sciencePacks[0]), GOLD(CivItems.gold), FOOD(CivItems.food), CULTURE(CivItems.culture), FAITH(
            CivItems.faith);
    
    public final ItemBase produced;
    
    EProducedType(ItemBase produced) {
        this.produced = produced;
        // CivCraft.types.add(produced);
    }
}
