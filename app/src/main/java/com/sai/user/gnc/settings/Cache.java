package com.sai.user.gnc.settings;

/**
 * Created by Jusung on 2016. 12. 20..
 */

import android.support.v4.util.LruCache;

public class Cache {

    private static Cache instance;
    private LruCache<Object, Object> lru;
    private LruCache<String,String> title;
    private LruCache<String,String> content;
    private Cache() {

        lru = new LruCache<Object, Object>(128);
        title=new LruCache<String,String>(128);
        content=new LruCache<String,String>(128);
    }

    public static Cache getInstance() {

        if (instance == null) {

            instance = new Cache();
        }

        return instance;

    }

    public LruCache<Object, Object> getLru() {
        return lru;
    }
    public LruCache<String, String> getTitle() {
        return title;
    }
    public LruCache<String, String> getContent() {
        return content;
    }
}
