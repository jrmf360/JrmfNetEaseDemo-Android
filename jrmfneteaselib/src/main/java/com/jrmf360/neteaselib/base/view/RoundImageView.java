package com.jrmf360.neteaselib.base.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.jrmf360.neteaselib.R;

/**
 * @创建人 honglin
 * @创建时间 17/3/3 下午2:33
 * @类描述 自定义圆形圆角图片
 */
public class RoundImageView extends ImageView {

	/**
	 * 图片的类型，圆形or圆角
	 */
	private int					type;

	private static final int	TYPE_CIRCLE				= 0;

	private static final int	TYPE_ROUND				= 1;

	private int					BODER_RADIUS_DEFAULT	= 10;

	private float				mBorderRadius;					// 圆的半径

	/**
	 * 绘图的Paint
	 */
	private Paint mBitmapPaint;

	/**
	 * 圆角的半径
	 */
	private int					mRadius;

	/**
	 * 渲染图像，使用图像为绘制图形着色
	 */
	private BitmapShader mBitmapShader;

	/**
	 * 3x3 矩阵，主要用于缩小放大
	 */
	private Matrix mMatrix;

	/**
	 * view的宽度
	 */
	private int					mWidth;

	private RectF mRoundRect;

	public RoundImageView(Context context) {
		super(context);
	}

	public RoundImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mMatrix = new Matrix();
		mBitmapPaint = new Paint();
		mBitmapPaint.setAntiAlias(true);

		// 获取自定义属性
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Jrmf_RoundImageView);
		mBorderRadius = typedArray.getDimensionPixelSize(R.styleable.Jrmf_RoundImageView_jrmf_borderRadius,
				(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BODER_RADIUS_DEFAULT, getResources().getDisplayMetrics()));// 默认为10dp
		type = typedArray.getInt(R.styleable.Jrmf_RoundImageView_jrmf_type, TYPE_CIRCLE);
		typedArray.recycle();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (type == TYPE_CIRCLE) {
			// 如果是圆形让宽高一致
			mWidth = Math.min(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
			mRadius = mWidth / 2;
			setMeasuredDimension(mWidth, mWidth);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 圆角图片的范围
		if (type == TYPE_ROUND) {
			mRoundRect = new RectF(0, 0, getWidth(), getHeight());
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
		Drawable drawable = getDrawable();
		if (drawable == null) {
			return;
		}
		setUpShader();

		// 开始绘制
		if (type == TYPE_ROUND) {
			canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius, mBitmapPaint);
		} else {
			canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
		}
	}

	private void setUpShader() {
		Drawable drawable = getDrawable();
		Bitmap bitmap = drawableToBitmap(drawable);

		// 着色器
		mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
		float scale = 1.0f;
		if (type == TYPE_CIRCLE) {
			// 圆形
			scale = mWidth * 1.0f / Math.min(bitmap.getWidth(), bitmap.getHeight());
		} else {
			// 圆角
			scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(), getHeight() * 1.0f / bitmap.getHeight());
		}

		// 创建shader的变换矩阵，主要用来缩放
		mMatrix.setScale(scale, scale);
		// 给shader设置矩阵
		mBitmapShader.setLocalMatrix(mMatrix);
		// 给paint设置shader
		mBitmapPaint.setShader(mBitmapShader);
	}

	/**
	 * drawable 转化成Bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	private Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			BitmapDrawable bd = (BitmapDrawable) drawable;
			return bd.getBitmap();
		}
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		drawable.draw(canvas);
		return bitmap;
	}
}
