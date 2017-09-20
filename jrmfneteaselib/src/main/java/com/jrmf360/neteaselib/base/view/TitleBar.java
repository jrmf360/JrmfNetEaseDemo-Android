package com.jrmf360.neteaselib.base.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;

/**
 * 自定义的标题栏
 * 
 * @author honglin
 * 
 */
public class TitleBar extends FrameLayout {

	private ImageView iv_back;
	private TextView tv_title;
	private RelativeLayout rootStatusbar;
	private int backGroudColor;
	private int textColor;
	private int textsize;


	public TitleBar(Context context) {
		this(context,null);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		// 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
		this(context, attrs, 0);
	}

	public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.Jrmf_b_TitleBar,defStyleAttr,0);
		backGroudColor = array.getColor(R.styleable.Jrmf_b_TitleBar_backgroud,context.getResources().getColor(R.color.jrmf_b_title_bar_color));
		textColor = array.getColor(R.styleable.Jrmf_b_TitleBar_title_color,context.getResources().getColor(R.color.jrmf_b_white));
		textsize = array.getDimensionPixelSize(R.styleable.Jrmf_b_TitleBar_title_size,context.getResources().getDimensionPixelSize(R.dimen.title_bar_text_size));
		init();
	}

	private void init() {
		View statusView = View.inflate(getContext(), R.layout.jrmf_b_title_bar, this);
	    rootStatusbar = (RelativeLayout) statusView.findViewById(R.id.rootStatusbar);
		rootStatusbar.setBackgroundColor(backGroudColor);
		iv_back = (ImageView) statusView.findViewById(R.id.iv_back);

		tv_title = (TextView) statusView.findViewById(R.id.tv_title);
		tv_title.setTextSize(TypedValue.COMPLEX_UNIT_PX,textsize);
		tv_title.setTextColor(textColor);

	}
	
	/**
	 * 得到返回按钮
	 * @return
	 */
	public ImageView getIvBack(){
		return iv_back;
	}
	
	/**
	 * 设置标题栏背景色
	 * @param color
	 */
	public void setBackGround(String color){
		rootStatusbar.setBackgroundColor(Color.parseColor(color));
	}
	/**
	 * 设置标题栏背景色
	 * @param color
	 */
	public void setBackGround(int color){
		rootStatusbar.setBackgroundColor(color);
	}

	/**
	 * 设置标题栏的title
	 * @param title
	 */
	public void setTitle(String title) {
		tv_title.setText(title);
	}
}
