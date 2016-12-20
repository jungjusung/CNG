package com.example.user.gnc.com.example.user.gnc.settings;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;

/**
 * Created by Jusung on 2016. 12. 20..
 */

public class MemoryCache extends android.support.v4.util.LruCache {

    private LruCache<String, Bitmap> mMemoryCache;

    public MemoryCache(int maxSize) {
        super(maxSize);

    }
    protected void onCreate( Bundle savedInstanceState){
        // 캐쉬의 기본 단위는 킬로바이트 입니다.
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 8;

        // 캐쉬를 생성합니다. cacheSize 는 이 메모리 캐쉬의 사이즈입니다.(단위 킬로바이트)
        // sizeOf 함수는 구현해야 합니다. 캐쉬하려는 객체의 사이즈를 리턴하는 함수. 캐쉬 남은용량과 캐쉬하려는 객체의 용량을 비교하기 위해서 사용되는것 같습니다.
        mMemoryCache = new LruCache<String, Bitmap>( cacheSize){
            @Override
            protected int sizeOf( String key, Bitmap bitmap){
                return bitmap.getByteCount() / 1024;
            }
        };
    }


    // 이미지 캐쉬 하기
    public void addBitmapToMemoryCache(String key, Bitmap bitmap){
        if(getBitmapFromMemCache(key) == null){
            mMemoryCache.put( key, bitmap);
        }
    }

    // 캐쉬된 이미지 가져오기
    public Bitmap getBitmapFromMemCache(String key){
        return mMemoryCache.get(key);
    }
}
