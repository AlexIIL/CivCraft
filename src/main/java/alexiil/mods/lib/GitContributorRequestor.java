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
import java.util.Comparator;
import java.util.List;

public class GitContributorRequestor {
    public static class GitHubUser {
        public final String name;
        public final int commits;
        public final String avatarUrl;
        public final String githubUrl;
        
        public GitHubUser(String name, String avatarUrl, String githubUrl, int commits) {
            this.name = name;
            this.avatarUrl = avatarUrl;
            this.githubUrl = githubUrl;
            this.commits = commits;
        }
    }
    
    private static final String LOGIN = "\"login\":\"";
    private static final String AVATAR = "\"avatar_url\":\"";
    private static final String COMMITS = "\"contributions\":";
    private static final String URL = "\"url\":\"";
    
    public static List<GitHubUser> getContributors(String user, String repo) {
        String response = getResponse("https://api.github.com/repos/" + user + "/" + repo + "/contributors");
        if (response == null)
            return Collections.emptyList();
        List<GitHubUser> users = parseContributors(response);
        Collections.sort(users, new Comparator<GitHubUser>() {
            @Override public int compare(GitHubUser o1, GitHubUser o2) {
                return o2.commits - o1.commits;
            }
        });
        return users;
    }
    
    private static List<GitHubUser> parseContributors(String s) {
        String[] strings = s.split("\\},\\{");
        List<GitHubUser> lst = new ArrayList<GitHubUser>();
        for (String string : strings) {
            if (string.startsWith("[{"))
                string = string.substring(2);
            if (string.endsWith("}]"))
                string = string.substring(0, string.length() - 2);
            
            String name = "";
            String avatarUrl = "";
            String url = "";
            int contributions = 0;
            
            for (String tag : string.split(",")) {
                if (tag.startsWith(LOGIN))
                    name = tag.substring(LOGIN.length(), tag.length() - 1);
                if (tag.startsWith(AVATAR))
                    avatarUrl = tag.substring(AVATAR.length(), tag.length() - 1);
                if (tag.startsWith(COMMITS))
                    contributions = Integer.parseInt(tag.substring(COMMITS.length(), tag.length()));
                if (tag.startsWith(URL))
                    url = tag.substring(URL.length(), tag.length() - 1);
            }
            
            lst.add(new GitHubUser(name, avatarUrl, url, contributions));
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
