package com.jrmf360.neteaselib.base.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Looper;

public class ThreadUtil {

	private static ThreadUtil instance = null;

	private ExecutorService fixedThreadPool; // 固定大小的线程池

	private ExecutorService singleThreadPool;

	private ThreadUtil() {
		fixedThreadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1);
		singleThreadPool = Executors.newSingleThreadExecutor();
	}

	public static ThreadUtil getInstance() {
		synchronized (ThreadUtil.class) {
			if (instance == null) {
				instance = new ThreadUtil();
			}
		}
		return instance;
	}

	/**
	 * 得到单个线程池
	 * @return
     */
	public ExecutorService getSingleThreadPool(){
		return singleThreadPool;
	}


	public void singleExecute(Runnable runnable){
		if (runnable != null){
			singleThreadPool.execute(runnable);
		}
	}


	/**
	 * 切换到主线程
	 * 
	 * @param runnable
	 */
	public void runMainThread(Runnable runnable) {
		new Handler(Looper.getMainLooper()).post(runnable);
	}

	public void execute(Runnable runnable) {
		fixedThreadPool.execute(runnable);
	}



	/**
	 * 关闭线程池，拒绝新提交的任务，但是已经提交的任务可以继续执行
	 */
	public void shutdown() {
		fixedThreadPool.shutdown();
	}

	public void shutdownNow() {
		fixedThreadPool.shutdownNow();
	}

}
