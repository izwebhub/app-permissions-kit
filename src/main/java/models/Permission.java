package models;

public class Permission {
    private String name;
    private String displayName;
    private String groupName;

    public Permission(String permname, String displayName, String groupName) {
        this.name = permname;
        this.displayName = displayName;
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
