package com.jrmf360.neteaselib.rp.widget;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;

/**
 * Created by Administrator on 2016/2/25.
 */
public class ActionBarView extends LinearLayout {

	private Context		context;

	private RelativeLayout	layout;

	private TextView	tv_content;

	private ImageView	iv_back;

	private boolean		isBackFinish;

	public ActionBarView(Context context) {
		this(context, null);
	}

	public ActionBarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi") public ActionBarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		initView(attrs, defStyleAttr);
	}

	private void initView(AttributeSet attrs, int defStyleAttr) {
		View contentView = LayoutInflater.from(context).inflate(R.layout.jrmf_rp_layout_actionbar, this);
		layout = (RelativeLayout) contentView.findViewById(R.id.layout);
		tv_content = (TextView) contentView.findViewById(R.id.tv_content);
		iv_back = (ImageView) contentView.findViewById(R.id.iv_back);

		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Jrmf_Rp_ActionBarView, defStyleAttr, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			if (attr == R.styleable.Jrmf_Rp_ActionBarView_content) {
				tv_content.setText(a.getString(attr));
			} else if (attr == R.styleable.Jrmf_Rp_ActionBarView_bankground) {
				int textColor = a.getColor(attr,getResources().getColor(R.color.jrmf_rp_title_bar));
				layout.setBackgroundColor(textColor);
			} else if (attr == R.styleable.Jrmf_Rp_ActionBarView_isBackFinish) {
				isBackFinish = a.getBoolean(attr, true);
			} else if (attr == R.styleable.Jrmf_Rp_ActionBarView_leftIcon) {
				Drawable drawable = a.getDrawable(attr);
				iv_back.setBackgroundDrawable(drawable);
			}
		}
		a.recycle();
	}

	/**
	 * 根据"＃909090"这样的格式来设置标题栏颜色
	 * 
	 * @param color
	 */
	public void setBarColor(String color) {
		if (color != null && color.length() > 0) {
			if (!color.startsWith("#")) {
				color = "#" + color;
			}
			layout.setBackgroundColor(Color.parseColor(color));
		}
	}

	public ImageView getIvBack() {
		return iv_back;
	}

	public void setTitle(String str) {
		tv_content.setText(str);
	}

}
