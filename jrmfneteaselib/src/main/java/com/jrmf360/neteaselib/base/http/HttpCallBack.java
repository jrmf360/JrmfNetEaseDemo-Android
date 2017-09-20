package com.jrmf360.neteaselib.base.http;

/**
 * @创建人 honglin
 * @创建时间 16/9/14 上午11:50
 * @类描述 网络请求的返回
 */
public interface HttpCallBack<T> {

	/**
	 * 请求成功 返回对象
	 * 
	 * @param object
	 */
	void onSuccess(T object);

	/**
	 * 请求失败，返回错误信息
	 * 
	 * @param result
	 */
	void onFail(String result);

	void parseNetworkResponse(String response);

}
