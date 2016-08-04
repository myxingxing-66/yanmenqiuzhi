package com.example.ysulib.config;

import android.os.Environment;

public class BmobConstants {

	//��ŷ���ͼƬ��Ŀ¼
	public static String BMOB_PICTURE_PATH = Environment.getExternalStorageDirectory()	+ "/bmobimdemo/image/";
	
	//�ҵ�ͷ�񱣴�Ŀ¼
	public static String MyAvatarDir = "/sdcard/bmobimdemo/avatar/";
	 
	//���ջص�
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;//�����޸�ͷ��
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;//��������޸�ͷ��
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;//ϵͳ�ü�ͷ��
	
	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;//����
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;//����ͼƬ
	public static final String EXTRA_STRING = "extra_string";
	
	public static final String ACTION_REGISTER_SUCCESS_FINISH ="register.success.finish";//ע��ɹ�֮���½ҳ���˳�
}
