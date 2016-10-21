package me.feedplanner.model;

import java.io.Serializable;

/**
 * Created by chienchieu on 13/09/2016.
 */
public class ImageSelectItem implements Serializable {

    private String path;
    private boolean selected;

    public ImageSelectItem(String path, boolean selected) {
        this.path = path;
        this.selected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
