package com.victor.sexytalk.sexytalk;

/**
 * Created by Victor on 14/03/2015.
 */
public class NavigationDrawerItems {

    private int icon;
    private String title;

    private boolean isGroupHeader = false;

    public NavigationDrawerItems(int icon, String title) {
        super();
        this.icon = icon;
        this.title = title;
        isGroupHeader=true;
    }
    public NavigationDrawerItems(String title ) {
        super();
        this.title = title;
    }

    public boolean isGroupHeader(){
        return isGroupHeader;
    }

    public int getIcon(){
        return icon;
    }
    public String getTitle(){
        return title;
    }

}
