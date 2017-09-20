package com.jrmf360.neteaselib.rp.fragment;//package com.jrmf360.rplib.fragment;
//
//import android.animation.ObjectAnimator;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.jrmf360.rplib.R;
//import com.jrmf360.rplib.http.WalletHttpManager;
//import com.jrmf360.rplib.http.model.RpInfoModel;
//import com.jrmf360.rplib.ui.RpDetailActivity;
//import com.jrmf360.rplib.utils.callback.GrabRpCallBack;
//import com.jrmf360.tools.http.OkHttpModelCallBack;
//import com.jrmf360.tools.utils.DrawableUtil;
//import com.jrmf360.tools.utils.ImageLoadUtil;
//import com.jrmf360.tools.utils.StringUtil;
//import com.jrmf360.tools.utils.ToastUtil;
//
///**
// * @创建人 honglin
// * @创建时间 16/11/15 下午4:25
// * @类描述 打开红包的弹框
// */
//public class OpenRpFragment extends OpenRpBaseDialogFragment {
//
//	private static GrabRpCallBack	mCallback;
//
//	private ImageView		iv_close, iv_header, iv_open_rp;
//
//	private TextView		tv_name, tv_send_rp, tv_tip, tv_no_rp, tv_look_others;
//
//	private String			userId, thirdToken, envelopeId;
//
//	private RpInfoModel		mRpInfoModel;
//
//	private int				key;
//
//	private boolean			isGrabing	= false;
//
//	public OpenRpFragment() {}
//
//	public static OpenRpFragment getInstance(GrabRpCallBack callBack){
//		mCallback = callBack;
//		return new OpenRpFragment();
//	}
//
//	@Override public int getLayoutId() {
//		return R.layout.jrmf_rp_dialog_open_rp;
//	}
//
//	@Override public void initView() {
//		iv_close = (ImageView) rootView.findViewById(R.id.iv_close);
//		iv_header = (ImageView) rootView.findViewById(R.id.iv_header);
//		iv_open_rp = (ImageView) rootView.findViewById(R.id.iv_open_rp);
//		tv_name = (TextView) rootView.findViewById(R.id.tv_name);
//		tv_send_rp = (TextView) rootView.findViewById(R.id.tv_send_rp);
//		tv_tip = (TextView) rootView.findViewById(R.id.tv_tip);
//		tv_no_rp = (TextView) rootView.findViewById(R.id.tv_no_rp);
//		tv_look_others = (TextView) rootView.findViewById(R.id.tv_look_others);
//
//	}
//
//	@Override public void initListener() {
//		iv_close.setOnClickListener(this);
//		iv_open_rp.setOnClickListener(this);
//		tv_look_others.setOnClickListener(this);
//	}
//
//	@Override protected void initData(Bundle bundle) {
//		if (bundle != null) {
//			userId = bundle.getString("userId");
//			thirdToken = bundle.getString("thirdToken");
//			envelopeId = bundle.getString("envelopeId");
//			key = bundle.getInt("key");
////			mCallback = (GrabRpCallBack) bundle.getSerializable("callback");
//			mRpInfoModel = (RpInfoModel) bundle.getSerializable("rpInfoModel");
//			// 设置头像
//			if (StringUtil.isNotEmpty(mRpInfoModel.avatar)) {
//				ImageLoadUtil.getInstance().loadImage(iv_header, mRpInfoModel.avatar);
//			}
//			// 设置名字
//			tv_name.setText(mRpInfoModel.username);
//
//			if (mRpInfoModel.type == 1) {
//				// 拼手气红包
//				DrawableUtil.setRightDrawable(fromActivity, tv_name, R.drawable.jrmf_rp_ic_pin, true);
//			} else {
//				DrawableUtil.setRightDrawable(fromActivity, tv_name, R.drawable.jrmf_rp_ic_pin, false);
//			}
//
//			int rpStatus = mRpInfoModel.envelopeStatus;
//
//			if (rpStatus == 0) {
//				// 红包正常未被领取
//				showOpenRp();
//			} else if (rpStatus == 1) {
//				// 红包已被领取
//
//			} else if (rpStatus == 2) {
//				// 红包失效
//				showRpExpire();
//			} else if (rpStatus == 3) {
//				// 红包被领完
//				showNoRp();
//			}
//		}
//	}
//
//	/**
//	 * 红包已被领完
//	 *
//	 */
//	private void showNoRp() {
//		tv_no_rp.setVisibility(View.VISIBLE);
//		tv_no_rp.setText(getActivity().getString(R.string.jrmf_rp_no_rp));
//	}
//
//	/**
//	 * 显示红包失效页面
//	 *
//	 */
//	private void showRpExpire() {
//		tv_look_others.setVisibility(View.GONE);
//		tv_no_rp.setVisibility(View.VISIBLE);
//		tv_no_rp.setText(getActivity().getString(R.string.jrmf_rp_rp_expire));
//
//	}
//
//	/**
//	 * 显示拆红包
//	 *
//	 */
//	private void showOpenRp() {
//		iv_open_rp.setVisibility(View.VISIBLE);
//		tv_send_rp.setVisibility(View.VISIBLE);
//		tv_tip.setVisibility(View.VISIBLE);
//		tv_tip.setText(mRpInfoModel.content);
//	}
//
//	@Override public void onClick(View v) {
//		int id = v.getId();
//		if (id == R.id.iv_close) {
//			dismissAllowingStateLoss();
//		} else if (id == R.id.iv_open_rp) {
//			// 点击拆红包
//			if (!isGrabing) {
//				grabRp();
//				isGrabing = true;
//			}
//		} else if (id == R.id.tv_look_others) {
//			// 看看其他人的手气-跳转到红包详情页面
//			RpDetailActivity.intent(fromActivity, 1, userId, thirdToken, envelopeId, mRpInfoModel.username, mRpInfoModel.avatar);
//			dismissAllowingStateLoss();
//		}
//	}
//
//	/**
//	 * 请求网络,抢红包
//	 */
//	private void grabRp() {
//		// 给拆红包添加动画
//		// 0 正常未被领取状态 1红包已经被领取，2红包失效不能领取 3红包未失效单已经被领完4:普通红包并且用户点击自己红包
//		final ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(iv_open_rp, "RotationY", 0, 180);
//		objectAnimator.setDuration(500);
//		objectAnimator.setRepeatCount(-1);
//		objectAnimator.start();
//		WalletHttpManager.grabGroupRp(fromActivity,userId, thirdToken, key, envelopeId, mRpInfoModel.username, mRpInfoModel.avatar, new OkHttpModelCallBack<RpInfoModel>() {
//
//			@Override public void onSuccess(final RpInfoModel rpInfoModel) {
//				objectAnimator.cancel();
//				isGrabing = false;
//				if (rpInfoModel == null) {
//					ToastUtil.showToast(fromActivity, "亲,您的网络不给力噢~");
//					return;
//				}
//
//				if (rpInfoModel.isSuccess()) {
//					mRpInfoModel = rpInfoModel;
//					if (rpInfoModel.envelopeStatus == 3) {
//						// 红包已经领完-显示手慢了页面
//						iv_open_rp.setVisibility(View.INVISIBLE);
//						tv_send_rp.setVisibility(View.INVISIBLE);
//						tv_tip.setVisibility(View.INVISIBLE);
//						showNoRp();
//					} else {
//						// 抢红包成功-跳到红包详情页面
//						RpDetailActivity.intent(fromActivity, 0, rpInfoModel, userId, thirdToken, envelopeId);
//						// 通知用户
//						if(rpInfoModel.envelopeStatus == 0){
//							if (mCallback != null) {
//								// 0 抢到最后一个红包,一个红包
//								if (rpInfoModel.hasLeft == 0 && rpInfoModel.total > 1) {
//									//红包个数大于一个的时候才会有抢到最后一个红包
//									mCallback.grabRpResult(0);
//								} else {
//									mCallback.grabRpResult(1);
//								}
//							}
//						}else if(rpInfoModel.envelopeStatus == 1){
//							//已领取红包
//							ToastUtil.showToast(fromActivity,"您已领取过该红包");
//						}
//						dismiss();
//					}
//				}
//			}
//
//			@Override public void onFail(String result) {
//				objectAnimator.cancel();
//				isGrabing = false;
//				ToastUtil.showToast(fromActivity, "亲,您的网络不给力噢~");
//			}
//		});
//	}
//
//}
