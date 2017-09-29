package com.gionee.autochargemap.utils;

/*
 *  @项目名：  AutoChargeMap 
 *  @包名：    com.gionee.autochargemap.utils
 *  @文件名:   ChargeUtils
 *  @创建者:   gionee
 *  @创建时间:  2017/3/25 15:09
 *  @描述：    获取充电的电流和电压
 */


import android.os.SystemProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChargeUtils {
    public static final int ChargeUtils_TYPE_CURRENT = 0x00000000;
    public static final int ChargeUtils_TYPE_VOLTAGE = 0x00000001;
    public static final int ChargeUtils_TYPE_TEMP    = 0x00000002;
    //高通
    private static final String QCOM_CURRENT_NOW = "/sys/class/power_supply/battery/current_now";//电流
    private static final String QCOM_VOLTAGE_NOW = "/sys/class/power_supply/battery/voltage_now";//电压
    private static final String QCOM_TEMP        = "/sys/class/power_supply/battery/temp";//温度

    //MTK
    private static final String MTK_CURRENT_NOW = "/sys/class/power_supply/battery/BatteryAverageCurrent";//电流
    private static final String MTK_VOLTAGE_NOW = "/sys/class/power_supply/battery/batt_vol";//电压
    private static final String MTK_TEMP        = "/sys/class/power_supply/battery/batt_temp";//温度


    //获得充电电压 充电电压 温度
   public static String getCharge(int type) {
        String mFileName = null;
//        Log.i("cup:" + getCpu());
        if (getCpu().equals("QCOM")) {
            switch (type) {
                case ChargeUtils_TYPE_CURRENT:
                    mFileName = QCOM_CURRENT_NOW;
            		break;
                case ChargeUtils_TYPE_VOLTAGE:
                    mFileName = QCOM_VOLTAGE_NOW;
                    break;
                case ChargeUtils_TYPE_TEMP:
                    mFileName = QCOM_TEMP;
                    break;
            	}
        } else {
            switch (type) {
                case ChargeUtils_TYPE_CURRENT:
                    mFileName = MTK_CURRENT_NOW;
                    break;
                case ChargeUtils_TYPE_VOLTAGE:
                    mFileName = MTK_VOLTAGE_NOW;
                    break;
                case ChargeUtils_TYPE_TEMP:
                    mFileName = MTK_TEMP;
                    break;
            }
        }
        String            changeVoltage     = null;
        FileInputStream   fileInputStream   = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader    br                = null;
        try {
            File voltageFilePath = new File(mFileName);
            if (voltageFilePath.exists()) {
                fileInputStream = new FileInputStream(voltageFilePath);
                inputStreamReader = new InputStreamReader(fileInputStream);
                br = new BufferedReader(inputStreamReader);
                String data;
                while ((data = br.readLine()) != null) {
                    changeVoltage = data;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (getCpu().equals("QCOM")) {
            switch (type) {
                case ChargeUtils_TYPE_CURRENT:
                   changeVoltage = Math.abs(Integer.parseInt(changeVoltage)/1000)+"";
                    break;
                case ChargeUtils_TYPE_VOLTAGE:
                    changeVoltage = Math.abs(Integer.parseInt(changeVoltage)/1000)+"";
                    break;
                case ChargeUtils_TYPE_TEMP:
                    changeVoltage = Math.abs(Integer.parseInt(changeVoltage)/10)+"";
                    break;
            }
        } else {
            switch (type) {
                case ChargeUtils_TYPE_TEMP:
                    changeVoltage = Math.abs(Integer.parseInt(changeVoltage)/10)+"";
                    break;
            }
        }
        return changeVoltage;
    }

    //获得充电电流
    public static String getChargeCurrent() {
        String mFileName;
//        Log.i("cup:" + getCpu());
        if (getCpu().equals("QCOM")) {
            mFileName = "/sys/class/power_supply/battery/current_now";
        } else {
            mFileName = "/sys/class/power_supply/battery/BatteryAverageCurrent";
        }
        String            chargeCurrent     = null;
        FileInputStream   fileInputStream   = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader    br                = null;
        try {
            File currentFilePath = new File(mFileName);
            if (currentFilePath.exists()) {
                fileInputStream = new FileInputStream(currentFilePath);
                inputStreamReader = new InputStreamReader(fileInputStream);
                br = new BufferedReader(inputStreamReader);
                String data;
                while ((data = br.readLine()) != null) {
                    chargeCurrent = data;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return chargeCurrent;
    }


    /**
     * @return cup型号
     */
    private static String getCpu() {
        return SystemProperties.get("ro.gn.platform.support");
    }

}
