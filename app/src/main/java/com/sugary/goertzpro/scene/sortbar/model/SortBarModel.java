package com.sugary.goertzpro.scene.sortbar.model;

import com.sugary.goertzpro.widget.sortbar.ItemSortable;

/**
 * Created by Ethan on 2017/10/27.
 */

public class SortBarModel implements ItemSortable {

    private String title;
    private String id;
    private boolean hasSortView;

    public SortBarModel(String title, String id, boolean hasSortView) {
        this.title = title;
        this.id = id;
        this.hasSortView = hasSortView;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setHasSortView(boolean hasSortView) {
        this.hasSortView = hasSortView;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public boolean hasSortView() {
        return hasSortView;
    }
}
