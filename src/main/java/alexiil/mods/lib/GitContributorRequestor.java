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

import alexiil.mods.civ.CivLog;

public class GitContributorRequestor {
    public static class GitHubUser {
        public final String name;
        public final int commits;// May use this, not sure
        public final String avatarUrl;// Might use this in a new Gui
        public final String githubUrl;// Useful if anyone wants to get more info on a person
        
        public GitHubUser(String name, String avatarUrl, String githubUrl, int commits) {
            this.name = name;
            this.avatarUrl = avatarUrl;
            this.githubUrl = githubUrl;
            this.commits = commits;
        }
    }
    
    public static class Commit {
        public final String message;
        public final String id;
        public final String url;
        public final int additions, deletions;
        
        public Commit(String url, String message, String id, int additions, int deletions) {
            this.message = message;
            this.id = id;
            this.url = url;
            this.additions = additions;
            this.deletions = deletions;
        }
    }
    
    private static final String LOGIN = "\"login\":\"";// "login":"
    private static final String AVATAR = "\"avatar_url\":\"";// "avatar_url":"
    private static final String COMMITS = "\"contributions\":";// "contributions":
    private static final String URL = "\"url\":\"";// "url":"
    
    public static List<GitHubUser> getContributors(String user, String repo) {
        String response = getResponse("repos/" + user + "/" + repo + "/contributors");
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
    
    /** Get a GitHub API response, without using an access token */
    public static String getResponse(String site) {
        return getResponse(site, null);
    }
    
    /** This appends the site to "https://api.github.com" so you don't need to (also, so you cannot use this method for
     * non-GitHub sites)<br>
     * The accessToken parameter is for if you have an access token, and you don't have any parameters in the site (so,
     * if your site is <code>"repo/AlexIIL/CivCraft/issues"</code> then you can use an access token, but if your site is
     * <code>"repo/AlexIIL/CivCraft/issues?label:enhancement"</code> you cannot. If any error occurs, then the returned
     * string is <code>null</code>, and an error is printed out to console */
    public static String getResponse(String site, String accessToken) {
        try {
            if (accessToken != null)
                site = site + "?access_token=" + accessToken;
            URLConnection url = new URL("https://api.github.com/" + site).openConnection();
            InputStream response = url.getInputStream();
            CivLog.info(url.getHeaderField("X-RateLimit-Remaining") + " requests left from GitHub in this hour");
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
