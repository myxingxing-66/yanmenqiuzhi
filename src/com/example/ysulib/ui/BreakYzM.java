package com.example.ysulib.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;

public class BreakYzM {
	private static final String TAG = "MainActivity ...";
	private static final String TESSBASE_PATH = "/mnt/sdcard/tesseract/";
	private static final String DEFAULT_LANGUAGE = "eng";
	private static final String IMAGE_PATH = "/mnt/sdcard/abc.jpg";
	private static final String EXPECTED_FILE = TESSBASE_PATH + "tessdata/"
			+ DEFAULT_LANGUAGE + ".traineddata";
	
	public static String ocr(Bitmap bitmap){

		//Bitmap bitmap = BitmapFactory.decodeFile(img_path);
		Log.d(TAG, "---in ocr()  before try--");
		try {
			Log.v(TAG, "not in the exception");
			ExifInterface exif = new ExifInterface(IMAGE_PATH);
			int exifOrientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			Log.v(TAG, "Orient: " + exifOrientation);

			int rotate = 0;
			switch (exifOrientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			}
			Log.v(TAG, "Rotation: " + rotate);
			int w = bitmap.getWidth();
			int h = bitmap.getHeight();
			Matrix mtx = new Matrix();
			mtx.preRotate(rotate);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
			bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
		} catch (IOException e) {
			Log.e(TAG, "Rotate or coversion failed: " + e.toString());
			Log.v(TAG, "in the exception");
		}
		TessBaseAPI baseApi = new TessBaseAPI();
		baseApi.setDebug(true);
		baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
		baseApi.setImage(bitmap);
		String recognizedText = baseApi.getUTF8Text();
		baseApi.end();
		Log.v(TAG, "OCR Result: " + recognizedText);
		if (DEFAULT_LANGUAGE.equalsIgnoreCase("eng")) {
			recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
		}
		if (recognizedText.length() != 0) {
			return recognizedText;
		}else{
			return null;
		}
	}

	public static void createFile(InputStream ins) {
		String filePath = "./mnt/sdcard/tesseract/tessdata" + "/"
				+ "eng.traineddata";// 文件路径
		try {
			File dir = new File("./mnt/sdcard/tesseract/tessdata");// 目录路径
			if (!dir.exists()) {
				if (dir.mkdirs()) {
				} else {
				}
			}
			// 目录存在，则将apk中raw中的需要的文档复制到该目录下
			File file = new File(filePath);
			if (!file.exists()) {// 文件不存在
				System.out.println("要打开的文件不存在");
				//InputStream ins = getResources().openRawResource(R.raw.eng);// 通过raw得到数据资源
				System.out.println("开始读入");
				FileOutputStream fos = new FileOutputStream(file);
				System.out.println("开始写出");
				byte[] buffer = new byte[8192];
				int count = 0;// 循环写出
				while ((count = ins.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				System.out.println("已经创建该文件");
				fos.close();// 关闭流
				ins.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
