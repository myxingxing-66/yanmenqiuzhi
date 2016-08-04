package com.example.ysulib.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.os.Environment;

public class ScarddUrlUtil {
	/**
     * 遍历 "system/etc/vold.fstab” 文件，获取全部的Android的挂载点信息
     * 
     * @return
	 * @throws IOException 
     */
    private static ArrayList<String> getDevMountList() throws IOException {
    	File file1=new File("/etc/vold.fstab");
        String[] toSearch = FileUtils.readFileToString(file1).split(" ");
        ArrayList<String> out = new ArrayList<String>();
        for (int i = 0; i < toSearch.length; i++) {
            if (toSearch[i].contains("dev_mount")) {
                if (new File(toSearch[i + 2]).exists()) {
                    out.add(toSearch[i + 2]);
                }
            }
        }
        return out;
    }
	
	
    /**
     * 获取扩展SD卡存储目录
     * 
     * 如果有外接的SD卡，并且已挂载，则返回这个外置SD卡目录
     * 否则：返回内置SD卡目录
     * 
     * @return
     * @throws IOException 
     */
    public static String getExternalSdCardPath() throws IOException {
 
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sdCardFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
            return sdCardFile.getAbsolutePath();
        }
 
        String path = null;
 
        File sdCardFile = null;
 
        ArrayList<String> devMountList = getDevMountList();
 
        for (String devMount : devMountList) {
            File file = new File(devMount);
 
            if (file.isDirectory() && file.canWrite()) {
                path = file.getAbsolutePath();
            }
        }
 
        if (path != null) {
            sdCardFile = new File(path);
            return sdCardFile.getAbsolutePath();
        }
 
        return null;
    }
}
