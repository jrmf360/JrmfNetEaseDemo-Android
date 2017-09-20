package com.jrmf360.neteaselib.base.interfaces;

import android.view.View;

/**
 * @创建人 honglin
 * @创建时间 16/8/27 上午11:57
 * @类描述 activity 和 fragment需要实现的方法
 */
public interface UIInterface extends View.OnClickListener {

	/**
	 * 获得布局ID 由子类来实现
	 * 
	 * @return
	 */
	int getLayoutId();

	/**
	 * 初始化view 在子类中实现
	 */
	void initView();

	/**
	 * 初始化数据
	 */

	/**
	 * 初始化监听器，子类可以不实现，因为并不是所有的页面都需要
	 */
	void initListener();
}
