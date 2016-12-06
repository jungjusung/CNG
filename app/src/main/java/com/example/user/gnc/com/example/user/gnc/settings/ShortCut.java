package com.example.user.gnc.com.example.user.gnc.settings;

/**
 * Created by user on 2016-12-02.
 */

public class ShortCut {
    private int short_cut;  //제스쳐 번호
    private String path;    //전화번호 또는 패키지 uri
    private String name;    //연락처 이름 또는 앱 이름
    private int method;     //전화걸기 또는 앱 실행

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMethod() {
        return method;
    }

    public void setMethod(int method) {
        this.method = method;
    }
}
