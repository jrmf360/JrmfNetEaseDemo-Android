package com.jrmf360.neteaselib.base.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.jrmf360.neteaselib.base.http.DownLoadCallBack;
import com.jrmf360.neteaselib.base.http.OkHttpWork;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * @创建人 honglin
 * @创建时间 16/9/22 下午1:43
 * @类描述 图片网络缓存的工具类
 */
public class NetworkCacheUtil {

	private final DiskCacheUtil		diskCacheUtil;

	private final MemoryCacheUtil	memoryCacheUtil;

	/**
	 * 构造方法
	 *
	 * @param memoryCacheUtil
	 * @param dishCacheUtil
	 */
	public NetworkCacheUtil(MemoryCacheUtil memoryCacheUtil, DiskCacheUtil dishCacheUtil) {
		this.memoryCacheUtil = memoryCacheUtil;
		this.diskCacheUtil = dishCacheUtil;
	}

	/**
	 *
	 * @param imageView
	 * @param url
	 */
	public void getBitmapFromNet(ImageView imageView, String url) {
		downloadBitmapFromNet(url, imageView);
	}

	/**
	 * 从网络加载图片
	 * 
	 * @param imageUrl
	 * @return
	 */
	private void downloadBitmapFromNet(final String imageUrl, final ImageView imageView) {
		OkHttpWork.getInstance().downLoadFile(imageUrl, new DownLoadCallBack() {

			@Override public void onSuccess(Object object) {
				if (object != null && object instanceof byte[]) {
					byte [] bys = (byte[]) object;
					Bitmap bitmap = BitmapFactory.decodeByteArray(bys, 0, bys.length);
					// 图片加载成功 － 先显示
					if (imageView.getTag() != null && bitmap != null) {
						if (imageView.getTag().equals(imageUrl)) {
							imageView.setImageBitmap(bitmap);
							// 缓存到本地缓存
							diskCacheUtil.putImageToDisk(imageUrl, bitmap);
							// 缓存到内存中
							memoryCacheUtil.putImageToMemory(imageUrl, bitmap);
						}
					} else {
						if (bitmap != null) {
							imageView.setImageBitmap(bitmap);
							// 缓存到本地缓存
							diskCacheUtil.putImageToDisk(imageUrl, bitmap);
							// 缓存到内存中
							memoryCacheUtil.putImageToMemory(imageUrl, bitmap);
						}
					}
				}
			}

			@Override public void onFail(String result) {
				// 下载失败
			}
		});
	}

	/**
	 * 验证主机名
	 */
	static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {

		// 信任的主机
		@Override public boolean verify(String hostname, SSLSession session) {
			LogUtil.i("verifyhost:" + hostname);
			// 示例
			if ("api.jrmf360.com".equals(hostname) || "api-test.jrmf360.com".equals(hostname) || "yun-test.jrmf360.com".equals(hostname) || "api-collection.jrmf360.com".equals(hostname)) {
				return true;
			} else {
				HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
				boolean verify = hv.verify(hostname, session);
				return verify;
			}
		}
	};
}
