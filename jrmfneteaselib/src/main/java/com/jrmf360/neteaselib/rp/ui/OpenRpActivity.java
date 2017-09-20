package com.jrmf360.neteaselib.rp.ui;


import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.DrawableUtil;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.LogUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.RpInfoModel;
import com.jrmf360.neteaselib.rp.utils.callback.GrabRpCallBack;

/**
 * @创建人 honglin
 * @创建时间 16/12/26 上午10:25
 * @类描述 一句话描述 你的UI
 */
public class OpenRpActivity extends BaseActivity {

	private static GrabRpCallBack mCallback, tempCallBack;

	private ImageView				iv_close, iv_header, iv_open_rp;

	private TextView				tv_name, tv_send_rp, tv_tip, tv_no_rp, tv_look_others;

	private String					envelopeId;

	private RpInfoModel mRpInfoModel;

	private int						key;

	private boolean					isGrabing	= false;

	public static void intent(Activity fromActivity, GrabRpCallBack callBack, RpInfoModel rpInfoModel, String uId, String thirdToken, String envelopeId, int key) {
		tempCallBack = callBack;
		Intent intent = new Intent(fromActivity, OpenRpActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("rpInfoModel", rpInfoModel);
		bundle.putString(ConstantUtil.JRMF_USER_ID, uId);
		bundle.putString(ConstantUtil.JRMF_THIRD_TOKEN, thirdToken);
		bundle.putString("envelopeId", envelopeId);
		bundle.putInt("key", key);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);

	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_open_rp;
	}

	@Override public void initView() {
		iv_close = (ImageView) findViewById(R.id.iv_close);
		iv_header = (ImageView) findViewById(R.id.iv_header);
		iv_open_rp = (ImageView) findViewById(R.id.iv_open_rp);
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_send_rp = (TextView) findViewById(R.id.tv_send_rp);
		tv_tip = (TextView) findViewById(R.id.tv_tip);
		tv_no_rp = (TextView) findViewById(R.id.tv_no_rp);
		tv_look_others = (TextView) findViewById(R.id.tv_look_others);

	}

	@Override public void initListener() {
		iv_close.setOnClickListener(this);
		iv_open_rp.setOnClickListener(this);
		tv_look_others.setOnClickListener(this);
	}

	@Override protected void initData(Bundle bundle) {
		if (bundle != null) {
			thirdToken = bundle.getString("thirdToken");
			envelopeId = bundle.getString("envelopeId");
			key = bundle.getInt("key");
			mRpInfoModel = (RpInfoModel) bundle.getSerializable("rpInfoModel");
			// 设置头像
			if (StringUtil.isNotEmpty(mRpInfoModel.avatar)) {
				ImageLoadUtil.getInstance().loadImage(iv_header, mRpInfoModel.avatar);
			}
			// 设置名字
			tv_name.setText(mRpInfoModel.username);

			if (mRpInfoModel.type == 1) {
				// 拼手气红包
				DrawableUtil.setRightDrawable(this, tv_name, R.drawable.jrmf_rp_ic_pin, true);
			} else {
				DrawableUtil.setRightDrawable(this, tv_name, R.drawable.jrmf_rp_ic_pin, false);
			}

			int rpStatus = mRpInfoModel.envelopeStatus;

			if (rpStatus == 0) {
				// 红包正常未被领取
				showOpenRp();
			} else if (rpStatus == 1) {
				// 红包已被领取

			} else if (rpStatus == 2) {
				// 红包失效
				showRpExpire();
			} else if (rpStatus == 3) {
				// 红包被领完
				showNoRp();
			}
		}
	}

	@Override protected void onStart() {
		super.onStart();
		mCallback = tempCallBack;
		LogUtil.i("onStart");
	}

	@Override protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LogUtil.i("onNewIntent");
	}

	/**
	 * 红包已被领完
	 *
	 */
	private void showNoRp() {
		tv_no_rp.setVisibility(View.VISIBLE);
		if (mRpInfoModel.type == 1) {
			tv_no_rp.setText(getString(R.string.jrmf_rp_no_rp));
		} else {
			// 已经领完的群普通红包，页面展示样式
			tv_no_rp.setVisibility(View.VISIBLE);
			tv_no_rp.setText(getString(R.string.jrmf_rp_no_rp_normal));
			tv_look_others.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 显示红包失效页面
	 *
	 */
	private void showRpExpire() {
		tv_look_others.setVisibility(View.GONE);
		tv_no_rp.setVisibility(View.VISIBLE);
		tv_no_rp.setText(getString(R.string.jrmf_rp_rp_expire));

	}

	/**
	 * 显示拆红包
	 *
	 */
	private void showOpenRp() {
		if (mRpInfoModel.isSelf == 1 && mRpInfoModel.type == 1) {
			// 群发拼手气自己查看
			tv_look_others.setVisibility(View.VISIBLE);
		} else {
			tv_look_others.setVisibility(View.INVISIBLE);
		}
		iv_open_rp.setVisibility(View.VISIBLE);
		tv_send_rp.setVisibility(View.VISIBLE);
		tv_tip.setVisibility(View.VISIBLE);
		tv_tip.setText(mRpInfoModel.content);
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_close) {
			finishAcvity();
		} else if (id == R.id.iv_open_rp) {
			// 点击拆红包
			if (!isGrabing) {
				grabRp();
				isGrabing = true;
			}
		} else if (id == R.id.tv_look_others) {
			// 看看其他人的手气-跳转到红包详情页面
			RpDetailActivity.intentNoRpInfo(this, 1, userid, thirdToken, envelopeId, "", "");
			finishAcvity();
		}
	}

	/**
	 * 请求网络,抢红包
	 */
	private void grabRp() {
		// 给拆红包添加动画
		// 0 正常未被领取状态 1红包已经被领取，2红包失效不能领取 3红包未失效单已经被领完4:普通红包并且用户点击自己红包
		final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(iv_open_rp, "RotationY", 0, 180);
		objectAnimator.setDuration(400);
		objectAnimator.setRepeatCount(-1);
		objectAnimator.start();
		RpHttpManager.grabGroupRp(context, userid, thirdToken, key, envelopeId, mRpInfoModel.username, mRpInfoModel.avatar, new OkHttpModelCallBack<RpInfoModel>() {

			@Override public void onSuccess(final RpInfoModel rpInfoModel) {
				objectAnimator.cancel();
				isGrabing = false;
				if (rpInfoModel.isSuccess()) {
					mRpInfoModel = rpInfoModel;
					if (rpInfoModel.envelopeStatus == 3) {
						// 红包已经领完-显示手慢了页面
						iv_open_rp.setVisibility(View.INVISIBLE);
						tv_send_rp.setVisibility(View.INVISIBLE);
						tv_tip.setVisibility(View.INVISIBLE);
						showNoRp();
					} else {
						// 抢红包成功-跳到红包详情页面
						RpDetailActivity.intent(context, 0, rpInfoModel, userid, thirdToken, envelopeId);
						// 通知用户
						if (rpInfoModel.envelopeStatus == 0) {
							if (mCallback != null) {
								// 0 抢到最后一个红包,1 抢到一个红包
								if (rpInfoModel.hasLeft == 0 && rpInfoModel.total > 1) {
									// 红包个数大于一个的时候才会有抢到最后一个红包
									mCallback.grabRpResult(0);
								} else {
									mCallback.grabRpResult(1);
								}
							}
						} else if (rpInfoModel.envelopeStatus == 1) {
							// 已领取红包
							ToastUtil.showToast(context, getString(R.string.jrmf_rp_had_rp));
						}
						finishAcvity();
					}
				}
			}

			@Override public void onFail(String result) {
				objectAnimator.cancel();
				isGrabing = false;
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_net_error_l));
			}
		});
	}

	@Override public void onBackPressed() {
		finishAcvity();
	}

	private void finishAcvity() {
		finish();
		overridePendingTransition(0, R.anim.jrmf_rp_fade_out);
	}

}
