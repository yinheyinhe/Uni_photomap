package com.HBuilder.UniPlugin;

import android.view.View;

public class OnClickUtil {
    public static void setOnclick(View.OnClickListener implOnclick, View... param){
        for (View v : param){
            v.setOnClickListener(implOnclick);
        }
    }
}
