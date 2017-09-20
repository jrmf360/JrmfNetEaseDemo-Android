package com.jrmf360.neteaselib.base.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * @创建人 honglin
 * @创建时间 16/9/22 下午1:39
 * @类描述 图片加载的工具类，利用三级缓存来加载图片
 */
public class ImageLoadUtil {
    private static ImageLoadUtil instance = null;

    private MemoryCacheUtil memoryCacheUtil;
    private DiskCacheUtil dishCacheUtil;
    private NetworkCacheUtil networkCacheUtil;

    private ImageLoadUtil(){
        memoryCacheUtil = new MemoryCacheUtil();
        dishCacheUtil = new DiskCacheUtil();
        networkCacheUtil = new NetworkCacheUtil(memoryCacheUtil,dishCacheUtil);
    }

    public static ImageLoadUtil getInstance() {
        synchronized (ImageLoadUtil.class) {
            if (instance == null) {
                instance = new ImageLoadUtil();
            }
        }

        return instance;
    }


    /**
     * 加载显示图片
     * @param imageView
     * @param imageUrl
     */
    public void loadImage(ImageView imageView, String imageUrl) {
        Bitmap bitmap;
        //内存缓存
        bitmap=memoryCacheUtil.getBitmapFromMemory(imageUrl);
        if (bitmap!=null){
            imageView.setImageBitmap(bitmap);
            return;
        }

        //本地缓存
        bitmap = dishCacheUtil.getBitmapFromDisk(imageUrl);
        if(bitmap !=null){
            imageView.setImageBitmap(bitmap);
            //从本地获取图片后,保存至内存中
            memoryCacheUtil.putImageToMemory(imageUrl,bitmap);
            return;
        }
        //网络缓存
        networkCacheUtil.getBitmapFromNet(imageView,imageUrl);
    }
}
