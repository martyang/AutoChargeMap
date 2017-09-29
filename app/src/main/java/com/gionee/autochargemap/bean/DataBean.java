package com.gionee.autochargemap.bean;

/*
 *  @项目名：  AutoChargeMap 
 *  @包名：    com.gionee.autochargemap.bean
 *  @文件名:   DateBean
 *  @创建者:   gionee
 *  @创建时间:  2017/3/29 9:44
 *  @描述：    数据bean
 */


public class DataBean{

    /**
     * 时间
     */
    private String time;
    /**
     * 电压
     */
    private String voltage;
    /**
     * 电流
     */
    private String current;
    /**
     * 温度
     */
    private String temp;

    /**
     * 当前的电量
     */
    private String newEnergy;

    public String getNewEnergy() {
        return newEnergy;
    }

    public void setNewEnergy(String newEnergy) {
        this.newEnergy = newEnergy;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getVoltage() {
        return voltage;
    }

    public void setVoltage(String voltage) {
        this.voltage = voltage;
    }

    public String getCurrent() {
        return current;
    }

    public void setCurrent(String current) {
        this.current = current;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }


    @Override
    public String toString() {
        return time+","+ voltage +"," + current + "," + temp+ ","+newEnergy;
    }
}
