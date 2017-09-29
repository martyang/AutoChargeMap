package com.gionee.autochargemap.utils;

import android.app.Application;


/*
 *  @文件名:   ContextUtil
 *  @创建者:   gionee
 *  @创建时间:  2017/2/24 11:39
 *  @描述：获得上线文
 */


public class ContextUtil
        extends Application {
    private static ContextUtil instance;

    public static ContextUtil getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

}
