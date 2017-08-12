
package com.yinhe.model;

public class ListViewItem {
    private String name;
    private boolean isChecked = false;

    public ListViewItem() {
        super();
    }

    public ListViewItem(String string) {
        super();
        this.name = string;
    }

    public ListViewItem(String name, boolean isChecked) {
        super();
        this.name = name;
        this.isChecked = isChecked;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
