package alexiil.mods.civ.tech;

import net.minecraft.nbt.NBTTagCompound;

public interface IUnlockableConstructor {
    public Unlockable createUnlockable(NBTTagCompound nbt);
}
