package alexiil.mods.lib.nbt;

import java.util.Map.Entry;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.config.Property.Type;
import alexiil.mods.lib.AlexIILLib;

public class NBTUtils {
    public static String toString(NBTTagCompound nbt) {
        return toString(nbt, 0);
    }
    
    public static String toString(NBTTagCompound nbt, int indent) {
        String s = "\n{";
        int idx = nbt.getKeySet().size();
        for (Object o : nbt.getKeySet()) {
            String key = (String) o;
            s += key + ":";
            NBTBase base = nbt.getTag(key);
            s += toString(base, indent);
            if (idx != 1)
                s += ",";
            s += getNextLine(indent + 1);
            idx--;
        }
        return s + getNextLine(indent) + "}";
    }
    
    private static String toString(NBTBase base, int indent) {
        String s = "";
        switch (base.getId()) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8: {// Normal tag
                s += base.toString();
                break;
            }
            case 9: {// List
                s += "<";
                for (int i = 0; i < ((NBTTagList) base).tagCount(); i++) {
                    NBTBase base1 = ((NBTTagList) base).get(i);
                    s += getNextLine(indent + 1) + toString(base1, indent + 1);
                    if (i != ((NBTTagList) base).tagCount() - 1)
                        s += ",";
                }
                s += getNextLine(indent) + ">";
                break;
            }
            case 10: {// Compound
                s += getNextLine(indent + 1);
                s += toString((NBTTagCompound) base, indent + 1);
                s += getNextLine(indent);
            }
        }
        return s;
    }
    
    private static String getNextLine(int indent) {
        String s = "\n";
        while (indent > 0) {
            s += "\t";
            indent--;
        }
        return s;
    }
    
    public static NBTTagCompound convertToNBT(ConfigCategory cat) {
        NBTTagCompound nbt = new NBTTagCompound();
        for (ConfigCategory child : cat.getChildren())
            nbt.setTag(child.getName(), convertToNBT(child));
        for (Entry<String, Property> pair : cat.entrySet()) {
            Property prop = pair.getValue();
            switch (prop.getType()) {
                case BOOLEAN: {
                    if (prop.isList()) {
                        String[] strings = prop.getStringList();
                        NBTTagList list = new NBTTagList();
                        for (String s : strings)
                            list.appendTag(new NBTTagString(s));
                        nbt.setTag(pair.getKey(), list);
                    }
                    else
                        nbt.setBoolean(pair.getKey(), prop.getBoolean());
                    break;
                }
                case DOUBLE: {
                    if (prop.isList()) {
                        double[] strings = prop.getDoubleList();
                        NBTTagList list = new NBTTagList();
                        for (double s : strings)
                            list.appendTag(new NBTTagDouble(s));
                        nbt.setTag(pair.getKey(), list);
                    }
                    else
                        nbt.setDouble(pair.getKey(), prop.getDouble());
                    break;
                }
                case INTEGER: {
                    if (prop.isList()) {
                        int[] strings = prop.getIntList();
                        NBTTagList list = new NBTTagList();
                        for (int s : strings)
                            list.appendTag(new NBTTagInt(s));
                        nbt.setTag(pair.getKey(), list);
                    }
                    else
                        nbt.setInteger(pair.getKey(), prop.getInt());
                    break;
                }
                case STRING: {
                    if (prop.isList()) {
                        String[] strings = prop.getStringList();
                        NBTTagList list = new NBTTagList();
                        for (String s : strings)
                            list.appendTag(new NBTTagString(s));
                        nbt.setTag(pair.getKey(), list);
                    }
                    else
                        nbt.setString(pair.getKey(), prop.getString());
                    break;
                }
                default: {
                    AlexIILLib.instance.log.warn("Was an unsupported type! (" + prop.getType().name().toLowerCase() + ")");
                    break;
                }
            }
        }
        return nbt;
    }
    
    public static void convertToConfigCategory(ConfigCategory cat, NBTTagCompound nbt) {
        for (Object key : nbt.getKeySet()) {
            String name = (String) key;
            NBTBase tag = nbt.getTag(name);
            switch (tag.getId()) {
                case 1: { // Byte, assume that this is a boolean :P
                    byte b = ((NBTBase.NBTPrimitive) tag).getByte();
                    if (b <= 1) {
                        cat.put(name, new Property(name, b == 0 ? "false" : "true", Type.BOOLEAN));
                        break;
                    }
                }
                case 2:// Short
                case 3:// Int
                case 4: {// Long
                    int l = ((NBTBase.NBTPrimitive) tag).getInt();
                    cat.put(name, new Property(name, Integer.toString(l), Type.INTEGER));
                    break;
                }
                case 5:// Float
                case 6: {// Double
                    double d = ((NBTBase.NBTPrimitive) tag).getDouble();
                    cat.put(name, new Property(name, Double.toString(d), Type.DOUBLE));
                    break;
                }
                case 7:// Byte Array
                    break;
                case 8: {// String
                    String s = ((NBTTagString) tag).getString();
                    cat.put(name, new Property(name, s, Type.STRING));
                    break;
                }
                case 9: {// Tag List
                    NBTTagList list = (NBTTagList) tag;
                    String[] strings = new String[list.tagCount()];
                    final Type type;
                    switch (list.getTagType()) {
                    // TODO: this!
                        case 1:
                        case 2:
                        case 3:
                        case 4: {
                            type = Type.INTEGER;
                            break;
                        }
                        case 5:
                        case 6: {
                            type = Type.DOUBLE;
                            break;
                        }
                        case 7:// String
                        case 8:// ByteArray
                        case 11: {// Int Array
                            type = Type.STRING;
                            break;
                        }
                        case 9:
                        case 10: {
                            AlexIILLib.instance.log
                                    .warn("A tag list was found embedded in a tag list! this cannot be directly transfered to a config category, skipping");
                        }
                        
                        default:
                            type = null;
                    }
                    for (int i = 0; i < list.tagCount(); i++) {
                        NBTBase n = list.get(i);
                        if (n instanceof NBTTagString)
                            strings[i] = ((NBTTagString) n).getString();
                        else
                            strings[i] = n.toString();
                    }
                    if (type != null)
                        cat.put(name, new Property(name, strings, type));
                    break;
                }
                case 10: {// NBTTagCompound
                    ConfigCategory cat2 = new ConfigCategory(name, cat);
                    convertToConfigCategory(cat2, (NBTTagCompound) tag);
                }
                case 11: // Int Array
                    break;
            }
        }
    }
}
