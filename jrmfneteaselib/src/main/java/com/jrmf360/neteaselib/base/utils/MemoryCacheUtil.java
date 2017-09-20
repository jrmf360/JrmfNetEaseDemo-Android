package com.jrmf360.neteaselib.base.utils;


import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * @创建人 honglin
 * @创建时间 16/9/22 下午1:42
 * @类描述 内存缓存的工具类
 */
public class MemoryCacheUtil {

	private LruCache<String, Bitmap>	mMemoryCache;

	public MemoryCacheUtil() {
		// 得到手机最大允许内存的1/8,即超过指定内存,则开始回收
		long maxMemory = Runtime.getRuntime().maxMemory() / 8;
		// 需要传入允许的内存最大值,虚拟机默认内存16M,真机不一定相同
		mMemoryCache = new LruCache<String, Bitmap>((int) maxMemory) {

			// 用于计算每个条目的大小
			@Override protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};

	}

	/**
	 * 把图片缓存到内存中
	 *
	 * @param imageUrl
	 * @param bitmap
	 */
	public void putImageToMemory(String imageUrl, Bitmap bitmap) {
		if (bitmap != null) {
			mMemoryCache.put(imageUrl, bitmap);
		}
	}

	/**
	 * 从内存中读图片
	 *
	 * @param url
	 */
	public Bitmap getBitmapFromMemory(String url) {
		Bitmap bitmap = mMemoryCache.get(url);
		return bitmap;
	}
}
