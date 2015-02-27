package alexiil.mods.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GitContributorRequestor {
    public static class Contributor {
        public final String name;
        public final int commits;
        public final String avatarUrl;
        
        public Contributor(String name, String avatarUrl, int commits) {
            this.name = name;
            this.avatarUrl = avatarUrl;
            this.commits = commits;
        }
    }
    
    private static final int LOGIN_LENGTH = "\"login\":\"".length();
    private static final int AVATAR_LENGTH = "\"avatar_url\":\"".length();
    private static final int COMMITS_LENGTH = "\"contributions\":".length();
    
    public static List<Contributor> getContributors(String user, String repo) {
        String response = getResponse("https://api.github.com/repos/" + user + "/" + repo + "/contributors");
        if (response == null)
            return Collections.emptyList();
        return parseContributors(response);
    }
    
    private static List<Contributor> parseContributors(String s) {
        String[] strings = s.split("\\},\\{");
        List<Contributor> lst = new ArrayList<Contributor>();
        for (String string : strings) {
            if (string.startsWith("[{"))
                string = string.substring(2);
            if (string.endsWith("}]"))
                string = string.substring(0, string.length() - 2);
            
            String name = "";
            String avatarUrl = "";
            int contributions = 0;
            
            for (String tag : string.split(",")) {
                if (tag.startsWith("\"login"))
                    name = tag.substring(LOGIN_LENGTH, tag.length() - 1);
                if (tag.startsWith("\"avatar_url"))
                    avatarUrl = tag.substring(AVATAR_LENGTH, tag.length() - 1);
                if (tag.startsWith("\"contributions"))
                    contributions = Integer.parseInt(tag.substring(COMMITS_LENGTH, tag.length()));
            }
            
            lst.add(new Contributor(name, avatarUrl, contributions));
        }
        return lst;
    }
    
    private static String getResponse(String site) {
        try {
            URLConnection url = new URL(site).openConnection();
            InputStream response = url.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(response, Charset.forName("UTF-8")));
            return br.readLine();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
