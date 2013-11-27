package com.cumulus.collection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private String name;
    private String homepage;
    private String id;
    private List<Experience> experiences;
    private Map<String, String> infos;


    public User(String name, String homepage) {
        this.name = name;
        setHomepage(homepage);
        experiences = new ArrayList<Experience>();
        infos = new HashMap<String, String>();
    }

    public User() {
        name = null;
        homepage = null;
        experiences = new ArrayList<Experience>();
        infos = new HashMap<String, String>();
    }

    public void addExperience(String name, String homepage, ArrayList<String> details) {
        experiences.add(new Experience(name, homepage));
        experiences.get(experiences.size()-1).details = details;
    }

    public void addInfo(String key, String val) {
        infos.put(key, val);
    }

    public void setHomepage(String homepage) {
        int idx = homepage.lastIndexOf('?');  // index of question mark which follows POST data
        if (idx!=-1) {
            String _homepage = homepage.substring(0, idx);
            if (_homepage.endsWith("profile.php")) {
                int _idx = homepage.indexOf('&', idx);
                if (_idx!=-1) {
                    homepage = homepage.substring(0, _idx);
                }
            } else {
                homepage = _homepage;
            }
        }
        this.homepage = homepage;

        // set id as well (only for printing usage)
        this.id = homepage.lastIndexOf('?')!=-1?
                homepage.substring(homepage.indexOf('=', idx)+1) : homepage.substring(homepage.lastIndexOf('/')+1);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getHomepage() {
        return homepage;
    }

    // tab could be "friends", "about" or so
    public String getTab(String tab) {
        String ret = getHomepage();

        if (homepage.indexOf('?')!=-1) {
            ret += "&sk=";
        } else {
            ret += '/';
        }
        ret += tab.toLowerCase();
        return ret;
    }

    public int hashCode() {
        return homepage.hashCode();
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", homepage='" + homepage + '\'' +
                ", id='" + id + '\'' +
                ", experiences=" + experiences +
                ", infos=" + infos +
                '}';
    }

    private class Experience {
        private String name;
        private String homepage;
        private List<String> details;

        private Experience(String name, String homepage) {
            this.name = name;
            this.homepage = homepage;
            details = new ArrayList<String>();
        }

        @Override
        public String toString() {
            return "Experience{" +
                    "name='" + name + '\'' +
                    ", homepage='" + homepage + '\'' +
                    ", details=" + details +
                    '}';
        }
    }
}
