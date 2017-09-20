package com.jrmf360.neteaselib.base.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

/**
 * @创建人 honglin
 * @创建时间 16/11/17 下午7:31
 * @类描述 能实现多种布局的listView的adapter
 */
public abstract class MulTypeListViewAdapter<T> extends BaseAdapter {

	protected Context			mContext;

	protected List<T>			mDatas;

	protected LayoutInflater	mInflater;

	public MulTypeListViewAdapter(Context context, List<T> datas) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		this.mDatas = datas;
	}

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
