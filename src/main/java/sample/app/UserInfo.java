package sample.app;

import java.util.HashSet;

public class UserInfo {
    private String name;
    private HashSet<String> roles = new HashSet<>();

    UserInfo(String name) {
        this.name = name;
    }

    public HashSet<String> getRoles() {
        return roles;
    }

    public void addRole(String role) {
        roles.add(role);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}