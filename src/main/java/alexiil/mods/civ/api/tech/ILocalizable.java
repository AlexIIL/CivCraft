package alexiil.mods.civ.api.tech;

public interface ILocalizable {

    /** @return The unlocalised version of this object */
    public abstract String getUnlocalizedName();

    /** @return The localised version of this object, such that it can be displayed to the user (this should r called
     *         I18n.format() or similar) */
    public abstract String getLocalizedName();

}
