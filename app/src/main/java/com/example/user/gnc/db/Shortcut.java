package com.example.user.gnc.db;

/**
 * Created by user on 2016-12-02.
 */

public class Shortcut {
    private int short_cut;
    private String path;
    private int method;

    public int getShort_cut() {
        return short_cut;
    }

    public void setShort_cut(int short_cut) {
        this.short_cut = short_cut;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }
}
