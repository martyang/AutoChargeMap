package com.gionee.autochargemap.utils;

/*
 *  @项目名：  AutoChargeMap 
 *  @包名：    com.gionee.autochargemap.utils
 *  @文件名:   FileUtils
 *  @创建者:   pbl
 *  @创建时间:  2017/3/29 11:17
 *  @描述：    文件
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtils {
    public static void writeObjectToFile(Object obj,String name)
    {
        File             file =new File(name);
        createDir(file.getParent());
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut =new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(e.getMessage());
        }
    }

    public static Object readObjectFromFile(String name)
    {
        Object          temp=null;
        File            file =new File(name);
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn=new ObjectInputStream(in);
            temp=objIn.readObject();
            objIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            Log.i(e.getMessage());
        }
        return temp;
    }
    /**
     * 创建目录
     * @param destDirName 路径
     * @return 是否成功
     */
   public static boolean createDir(String destDirName) {
        File dir = new File(destDirName);
        if (dir.exists()) {
            Log.i("创建目录" + destDirName + "失败，目标目录已经存在");
            return false;
        }
        if (!destDirName.endsWith(File.separator)) {
            destDirName = destDirName + File.separator;
        }
        //创建目录
        if (dir.mkdirs()) {
            Log.i("创建目录" + destDirName + "成功！");
            return true;
        } else {
            Log.i("创建目录" + destDirName + "失败！");
            return false;
        }
    }
}
