package com.sai.user.gnc;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by wbhlkc0 on 2016-11-30.
 */

public class HeroIcon extends ImageView {
    int winX;
    int winY;
    int x;
    int y;
    int width;
    int heigth;
    ViewGroup.LayoutParams img_params;
    public HeroIcon(Context context, int winX, int winY, int width, int height) {
        super(context);
        x = winX-width/2;
        y = winY-height/2;
        this.width = width;
        this.heigth = height;

        img_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(img_params);

    }




}
