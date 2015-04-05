package alexiil.mods.civ.api.tech.unlock;

import net.minecraft.nbt.NBTTagCompound;

public interface IUnlockableConstructor {
    IUnlockable createUnlockable(NBTTagCompound nbt);
}
