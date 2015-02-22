package alexiil.mods.civ.tech;

public interface ILocalizable {
    
    /** @return The unlocalized version of this object */
    public abstract String getUnlocalizedName();
    
    /** @return The localized version of this object, such that it can be displayed to the user (this should have called
     *         I18n.format() or simailar) */
    public abstract String getLocalizedName();
    
}