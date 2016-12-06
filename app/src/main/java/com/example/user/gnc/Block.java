package com.example.user.gnc;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by wbhlkc0 on 2016-11-30.
 */

public class Block extends LinearLayout {
    int x;
    int y;
    int width;
    int height;

    public Block(Context context, int winX, int winY, int width, int height) {

        super(context);
        x = winX-width/2;
        y = winY-height/2;
        this.width = width;
        this.height = height;

        LayoutParams btn_liParameters1 = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setBackgroundColor(Color.argb(0,23,20,23));
        setLayoutParams(btn_liParameters1);
    }
}
