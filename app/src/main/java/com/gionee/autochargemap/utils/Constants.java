package com.gionee.autochargemap.utils;

/*
 *  @项目名：  AutoChargeMap 
 *  @包名：    com.gionee.autochargemap.utils
 *  @文件名:   Constants
 *  @创建者:   gionee
 *  @创建时间:  2017/3/28 10:28
 *  @描述：    常量
 */


import android.os.Environment;

import java.io.File;

public interface Constants {
    /**
     * 表名
     */
    String SHEET_NAME = "测试数据";
    /**
     * 图名
     */
    String[]    TABLENAME =new String[]{"电流-mA","电压-mV","温度-℃","当前电量"};

    /**
     * sd卡路径
     */
    String SDCARD_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    /**
     * 应用文件路径
     */
    String DATA_PATH = SDCARD_PATH+ File.separator+"AutoChargeMap";

    /**
     * 数据
     */
    String LOG_PATH = DATA_PATH+File.separator+"ChargeData";

    /**
     * 图片存放路径
     */
   String IMAGE_PATH = DATA_PATH+File.separator+"ChargeImage";
    /**
     * 开始时间
     */
    String START_TIME = "start_time";
    /**
     * 结束的时间
     */
    String STOP_TIME = "stop_time";
    /**
     * 满电的时间
     */
    String FILL_TIME = "fill_time";
    /**
     * 开始时的电量
     */
    String START_LEVEL = "start_Level";
    /**
     * 当前电量
     */
    String NOW_LEVEL = "now_level";
}
