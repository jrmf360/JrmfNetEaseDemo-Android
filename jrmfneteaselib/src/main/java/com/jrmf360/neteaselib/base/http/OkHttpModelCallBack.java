package com.jrmf360.neteaselib.base.http;

import com.jrmf360.neteaselib.base.manager.GsonManager;

import java.lang.reflect.ParameterizedType;


/**
 * @创建人 honglin
 * @创建时间 17/1/1 下午8:16
 * @类描述 一句话描述 你的UI
 */
public abstract class OkHttpModelCallBack<T> implements HttpCallBack<T> {

	@Override
	public void parseNetworkResponse(String response) {
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		if (entityClass == String.class){
			onSuccess((T) response);
			return;
		}

		T t = GsonManager.getInstance().fromJson(response,entityClass);
		onSuccess(t);
	}
}
