package com.jrmf360.neteaselib.base.http;

/**
 * @创建人 honglin
 * @创建时间 17/1/19 上午10:52
 * @类描述 一句话描述 你的UI
 */
public abstract class DownLoadCallBack<T> implements HttpCallBack<T> {

	@Override public void parseNetworkResponse(String response) {}

	public void onProgress(long byteReadOrWrite, long contentLength, boolean done) {};

}
