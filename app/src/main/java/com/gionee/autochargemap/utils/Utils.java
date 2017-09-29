package com.gionee.autochargemap.utils;

/*
 *  @项目名：  AutoChargeMap 
 *  @包名：    com.gionee.autochargemap.utils
 *  @文件名:   Utils
 *  @创建者:   gionee
 *  @创建时间:  2017/3/29 10:18
 *  @描述：    工具
 */


import com.gionee.autochargemap.bean.DataBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Utils {
    /**
     * 获得毫秒值
     */
    public static long getTimeForMillisecond(){
       return new Date().getTime();
    }

    /**
     * 获得日期时间
     */
    public static String getTime(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }
    /**
     * 获得时间
     */
    public static String getTimeHms(){
        SimpleDateFormat df = new SimpleDateFormat("MM:dd-HH:mm:ss");//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }
    /**
     * 获得时间
     */
    public static long getTimeHms(String data)
            throws ParseException
    {
        SimpleDateFormat format =  new SimpleDateFormat("MM:dd-HH:mm:ss");
        Date date = format.parse(data);
        return date.getTime();

    }
    /**
     * 毫秒值转时间
     */
    public static String getData(long ms){
        long s =ms/1000;
        StringBuilder builder = new StringBuilder();
        if (s/(3600)>0){
            builder.append(s / (3600))
                   .append("时");
            s =s%(3600);
        }if (s/(60)>0){
            builder.append(s / (60))
                   .append("分");
            s =s%(60);
        }
        builder.append(s)
               .append("秒");
        return builder.toString();
    }

    /**
     * 时间差
     */
    public static String getLeadTime(){
        long startTime;
        long stopTime;
        try {
            startTime = Utils.getTimeHms(Preference.getString(ContextUtil.getInstance(), Constants.START_TIME));
            stopTime = Utils.getTimeHms(Preference.getString(ContextUtil.getInstance(), Constants.STOP_TIME));

        } catch (ParseException e) {
            e.printStackTrace();
            Log.i(e.getMessage());
            return "时长:error";
        }
        return "测试时长:"+ Utils.getData(stopTime - startTime);
    }

    /**
     * log解析成bean
     */
    public static ArrayList<DataBean> log2List(String path){
        ArrayList<DataBean> list = new ArrayList<>();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(new File(path)));
            String line;
            while ((line=bufferedReader.readLine())!=null){
                DataBean bean = new DataBean();
                String[] strs = line.split(",");
                // time+","+ voltage +"," + current + "," + temp+ ","+newEnergy;
                bean.setTime(strs[0]);
                bean.setVoltage(strs[1]);
                bean.setCurrent(strs[2]);
                bean.setTemp(strs[3]);
                bean.setNewEnergy(strs[4]);
                list.add(bean);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return list;
        }
        return list;
    }

}
