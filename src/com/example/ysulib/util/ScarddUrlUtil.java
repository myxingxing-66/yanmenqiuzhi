package com.example.ysulib.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import android.os.Environment;

public class ScarddUrlUtil {
	/**
     * ���� "system/etc/vold.fstab�� �ļ�����ȡȫ����Android�Ĺ��ص���Ϣ
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
     * ��ȡ��չSD���洢Ŀ¼
     * 
     * �������ӵ�SD���������ѹ��أ��򷵻��������SD��Ŀ¼
     * ���򣺷�������SD��Ŀ¼
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
