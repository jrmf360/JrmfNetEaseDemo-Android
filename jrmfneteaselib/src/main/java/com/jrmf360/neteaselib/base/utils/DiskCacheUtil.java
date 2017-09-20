package com.jrmf360.neteaselib.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

/**
 * @创建人 honglin
 * @创建时间 16/9/22 下午1:43
 * @类描述 图片磁盘缓存的工具类
 */
public class DiskCacheUtil {

	private static final String	CACHE_PATH	= Environment.getExternalStorageDirectory().getAbsolutePath() + "/jrmf_image";

	public DiskCacheUtil(){
//		File file = new File(CACHE_PATH);
//		if (file.exists() && file.isDirectory()){
//			File[] files = file.listFiles();
//			for (File f :files){
//				f.delete();
//			}
//			LogUtil.e("deleteFile","jrmf_b_success");
//		}
	}

	/**
	 * 把图片缓存到磁盘中
	 *
	 * @param imageUrl
	 * @param bitmap
	 */
	public void putImageToDisk(String imageUrl, Bitmap bitmap) {
		// 先检查外部存储卡是否可用
		if (checkExternalStorageState()) {
			// 把图片的url地址作为文件名
			String filename = DesUtil.encrypt(imageUrl);
			File file = new File(CACHE_PATH, filename);

			// 通过得到文件的父文件,判断父文件是否存在
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}

			try {
				// 把图片保存至本地
				if (bitmap!=null){
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

    /**
     * 根据图片地址从磁盘中获得图片
     * @param imageUrl
     * @return
     */
	public Bitmap getBitmapFromDisk(String imageUrl) {
		// 先判断外部存储器是否存在
		if (checkExternalStorageState()) {
			String filename = DesUtil.encrypt(imageUrl);
			File file = new File(CACHE_PATH, filename);
			if (file.exists()) {
				try {
					Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                    return bitmap;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
        return null;
	}

	private boolean checkExternalStorageState() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// 外部存储可用
			return true;
		} else {
			// 外部存储不可用
			return false;
		}
	}
}
