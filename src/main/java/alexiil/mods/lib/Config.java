package alexiil.mods.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public class Config {
    private List<Property> options = Collections.synchronizedList(new ArrayList<Property>());
    private final File confFile;
    private WeakReference<AlexIILMod> modParent;

    public Config(File file, AlexIILMod parent) {
        confFile = file;
        modParent = new WeakReference<AlexIILMod>(parent);
    }

    public void load() {
        BufferedReader br = null;
        try {
            if (!confFile.exists())
                confFile.createNewFile();
            if (confFile.exists()) {
                br = new BufferedReader(new FileReader(confFile));
                String line = "";
                while ((line = br.readLine()) != null) {
                    try {
                        modParent.get().log.info(line);
                        String[] option = line.split("=");
                        option[0].charAt(0);// null checking, in the lightest way possible (throwing an error if it IS
                                            // null, or not long enough)
                        if (option.length == 1)
                            option = new String[] { option[0], "" };
                        if ("roamingIPLoc".equals(option[0]))
                            option[0] = "roamingIP.location";
                        Property prop = getProp(option[0], option[1]);
                        prop.stored = option[1];
                    }
                    catch (Exception t) {
                        modParent.get().log.warn("Skipping bad option: " + line);
                        modParent.get().log.warn(t);
                    }
                }
            }
        }
        catch (Throwable t) {
            modParent.get().log.warn("Could not read from config file (" + t.getMessage() + ")");
        }
        finally {
            try {
                if (br != null)
                    br.close();
            }
            catch (Throwable t) {
                modParent.get().log.warn("Could not close buffered reader (" + t.getMessage() + ")");
            }
        }
    }

    private void save() {
        Collections.sort(options);
        PrintWriter pr = null;
        try {
            pr = new PrintWriter(new FileWriter(confFile));
            for (Property prop : options)
                pr.println(prop.name + "=" + prop.stored);
        }
        catch (Throwable t) {
            modParent.get().log.warn("Writing config file failed (" + t.getMessage() + ")");
        }
        finally {
            if (pr != null)
                pr.close();
        }
    }

    public Property getProp(String name, String defaultP) {
        for (Property prop : options)
            if (prop.name.equals(name))
                return prop;
        return new Property(name, defaultP);
    }

    public Property getProp(String name) {
        for (Property prop : options)
            if (prop.name.equals(name))
                return prop;
        return null;
    }

    @Deprecated
    public class Property implements Comparable<Property> {
        public final String name, defaultP;
        private String stored;
        /** Placeholder for when forge exists */
        public String comment;

        private Property(String name, String defaultP) {
            this.name = name;
            this.defaultP = defaultP;
            stored = defaultP;
            options.add(this);
            save();
        }

        public String getStored() {
            return stored;
        }

        public boolean getBoolean() {
            return Boolean.parseBoolean(stored);
        }

        public int getInt() {
            return Integer.parseInt(stored);
        }

        public double getDouble() {
            return Double.parseDouble(stored);
        }

        @Override
        public int compareTo(Property o) {
            if (name.contains(o.name))
                return 1;
            if (o.name.contains(name))
                return -1;
            return name.compareTo(o.name);
        }
    }
}
