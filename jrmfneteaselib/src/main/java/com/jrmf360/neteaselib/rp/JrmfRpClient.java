package com.jrmf360.neteaselib.rp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LARDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.manager.CusActivityManager;
import com.jrmf360.neteaselib.base.model.BaseModel;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.rp.bean.EnvelopeBean;
import com.jrmf360.neteaselib.rp.bean.TransAccountBean;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.RpInfoModel;
import com.jrmf360.neteaselib.rp.ui.AuthenActivity;
import com.jrmf360.neteaselib.rp.ui.OpenRpActivity;
import com.jrmf360.neteaselib.rp.ui.PActivity;
import com.jrmf360.neteaselib.rp.ui.PwdH5Activity;
import com.jrmf360.neteaselib.rp.ui.RpDetailActivity;
import com.jrmf360.neteaselib.rp.ui.SgRpActivity;
import com.jrmf360.neteaselib.rp.ui.SsRpActivity;
import com.jrmf360.neteaselib.rp.utils.callback.GrabRpCallBack;

public class JrmfRpClient {

	private static String				BASE_BAR_COLOR	= "#E54141";	// 红包标题栏颜色

	private static LARDialogFragment bindCardTipDialog;				// 绑卡弹框

	private static final int			DIALOG_SET_PWD	= 10;			// 设置密码弹框提示

	private static final int			DIALOG_AUTH		= 11;			// 用户认证弹框提示

	/**
	 * 设置全局标题栏颜色
	 *
//	 * @param color
	 */
//	private static void setCommonTitleBarColor(String color) {
//		BASE_BAR_COLOR = color;
//	}

//	public static String getCommonTitleBarColor() {
//		return BASE_BAR_COLOR;
//	}

	/**
	 * 设置红包标题栏颜色 格式如"#dd0000"
	 *
	 * @param color
	 */
//	public static void setJrmfRpTitleColor(String color) {
//		JrmfRpClient.setCommonTitleBarColor(color);
//	}

	/**
	 * 打开查看交易记录
	 *
	 * @param fromActivity
	 *            [必传] 上下文
	 * @param userId
	 *            [必传] 用户id
	 * @param thirdToken
	 *            【必传】三方签名令牌（服务端计算后给到app， 服务端算法为md5（custUid+appsecret）
	 */
//	public static void openTradeHistory(Activity fromActivity, String userId, String thirdToken) {
//		TradeHistoryActivity.intent(fromActivity, userId, thirdToken);
//	}

	/**
	 * 打开红包详情
	 *
	 * @param fromActivity
	 *            [必传] 上下文
	 * @param userId
	 *            [必传] 用户id
	 * @param thirdToken
	 *            【必传】三方签名令牌（服务端计算后给到app， 服务端算法为md5（custUid+appsecret）
	 * @param rpId
	 *            [必传] 红包id
	 * @param username
	 *            [可为空] 用户昵称
	 * @param usericon
	 *            [可为空] 用户头像
	 */
	public static void openRpDetail(Activity fromActivity, String userId, String thirdToken, String rpId, String username, String usericon) {
		RpDetailActivity.intentNoRpInfo(fromActivity, 2, userId, thirdToken, rpId, username, usericon);
	}

	/**
	 * 打开发送单聊红包界面
	 *
	 * @param context
	 *            【必传】上下文对象
	 * @param targetId
	 *            【必传】目标ID(单聊会话ID)
	 * @param userid
	 *            【必传】用户ID|当前用户ID
	 * @param thirdToken
	 *            【必传】三方签名令牌（服务端计算后给到app， 服务端算法为md5（custUid+appsecret）
	 * @param username
	 *            【可为NULL或""】用户昵称
	 * @param usericon
	 *            【可为NULL或""】用户头像URL
	 * @param requestCode
	 *            【必传】
	 */
	public static void sendSingleEnvelopeForResult(Activity context, String targetId, String userid, String thirdToken, String username, String usericon, int requestCode) {
		Intent intent = new Intent(context, SsRpActivity.class);
		exeStartSendForResult(context, intent, targetId, userid, thirdToken, 0, username, usericon, requestCode);
	}

	/**
	 * 打开发送群组红包界面
	 *
	 * @param context
	 *            【必传】上下文对象
	 * @param targetId
	 *            【必传】目标ID(会话ID)
	 * @param userid
	 *            【必传】用户ID|当前用户ID
	 * @param thirdToken
	 *            【【必传】三方签名令牌（服务端计算后给到app， 服务端算法为md5（custUid+appsecret）
	 * @param groupNum
	 *            【必传】群组人数
	 * @param username
	 *            【可为NULL或""】用户昵称
	 * @param usericon
	 *            【可为NULL或""】用户头像URL
	 * @param requestCode
	 *            【必传】
	 */
	public static void sendGroupEnvelopeForResult(Activity context, String targetId, String userid, String thirdToken, int groupNum, String username, String usericon, int requestCode) {
		Intent intent = new Intent(context, SgRpActivity.class);
		exeStartSendForResult(context, intent, targetId, userid, thirdToken, groupNum, username, usericon, requestCode);
	}

	/**
	 * 抢群红包
	 *
	 * @param activity
	 *            [必传] 上下文
	 * @param userid
	 *            [必传] 用户id
	 * @param thirdToken
	 *            【必传】三方签名令牌（服务端计算后给到app， 服务端算法为md5（custUid+appsecret）
	 * @param username
	 *            [可为空] 用户姓名
	 * @param usericon
	 *            [可为空] 用户头像
	 * @param envelopeId
	 *            [必传] 红包id
	 * @param callBack
	 *            [必传] 抢红包回调接口
	 */
	public static void openGroupRp(final Activity activity, final String userid, final String thirdToken, final String username, final String usericon, final String envelopeId,
			GrabRpCallBack callBack) {
		openRedPacket(activity, 0, userid, thirdToken, username, usericon, envelopeId, callBack);

	}

	/**
	 * 打开单聊红包
	 *
	 * @param activity
	 *            [必传] 上下文
	 * @param userid
	 *            [必传] 用户id
	 * @param thirdToken
	 *            【必传】三方签名令牌（服务端计算后给到app， 服务端算法为md5（custUid+appsecret）
	 * @param username
	 *            [可为空] 用户姓名
	 * @param usericon
	 *            [可为空] 用户头像
	 * @param envelopeId
	 *            [必传] 红包id
	 * @param callBack
	 *            [必传] 抢红包回调接口
	 */
	public static void openSingleRp(final Activity activity, final String userid, final String thirdToken, final String username, final String usericon, final String envelopeId,
			GrabRpCallBack callBack) {
		openRedPacket(activity, 1, userid, thirdToken, username, usericon, envelopeId, callBack);
	}

	/**
	 * 打开转账页面
	 *
	 * @param context
	 *            【必传】上下文对象
	 * @param targetId
	 *            【必传】目标ID(用户ID)
	 * @param userid
	 *            【必传】用户ID|当前用户ID
	 * @param thirdToken
	 *            【必传】三方签名令牌（服务端计算后给到app， 服务端算法为md5（custUid+appsecret）
	 * @param username
	 *            【可为NULL或""】当前用户昵称
	 * @param usericon
	 *            【可为NULL或""】当前用户头像URL
	 * @param recUserName
	 *            【可为NULL或""】目标用户昵称
	 * @param recUserIcon
	 *            【可为NULL或""】目标用户头像URL
	 * @param requestCode
	 *            【必传】
	 *
	 */
	//	public static void transAccountForResult(Activity context, String targetId, String userid, String thirdToken, String username, String usericon, String recUserName, String recUserIcon,
	//			int requestCode) {
	//		Intent intent = new Intent(context, TransAccountActivity.class);
	//		Bundle bundle = new Bundle();
	//		bundle.putString(ConstantUtil.R_TargetId, targetId);
	//		bundle.putString(ConstantUtil.JRMF_USER_ID, userid);
	//		bundle.putString(ConstantUtil.JRMF_THIRD_TOKEN,thirdToken);
	//
	//		bundle.putString(ConstantUtil.JRMF_USER_NAME, username);
	//		bundle.putString(ConstantUtil.JRMF_USER_ICON, usericon);
	//		bundle.putString(ConstantUtil.JRMF_REC_USER_NAME, recUserName);
	//		bundle.putString(ConstantUtil.JRMF_REC_USER_ICON, recUserIcon);
	//
	//		intent.putExtras(bundle);
	//		context.startActivityForResult(intent, requestCode);
	//	}

	/**
	 * 打开转账详情
	 * 
	 * @param activity
	 *            上下文
	 * @param userid
	 * @param thirdToken
	 *            【必传】三方签名令牌（服务端计算后给到app， 服务端算法为md5（custUid+appsecret）
	 * @param transferOrder
	 *            【必传】转账单号
	 * @param transCallBack
	 *            【必传】转账回调接口
	 */
//	public static void openTransDetail(Activity activity, String userid, String thirdToken, String transferOrder, TransAccountCallBack transCallBack) {
//		TransDetailActivity.intent(activity, userid, thirdToken, transferOrder, transCallBack);
//	}

	/**
	 * 原生拆红包页面
	 *
	 * @param activity
	 * @param key
	 *            0 群红包,1单聊红包
	 * @param userid
	 * @param thirdToken
	 * @param username
	 * @param usericon
	 * @param envelopeId
	 */
	private static void openRedPacket(final Activity activity, final int key, final String userid, final String thirdToken, final String username, final String usericon, final String envelopeId,
			final GrabRpCallBack callBack) {
		DialogDisplay.getInstance().dialogLoading(activity, activity.getString(R.string.jrmf_rp_loading));
		RpHttpManager.groupRpInfo(activity, key, userid, thirdToken, envelopeId, username, usericon, new OkHttpModelCallBack<RpInfoModel>() {

			@Override public void onSuccess(RpInfoModel rpInfoModel) {
				DialogDisplay.getInstance().dialogCloseLoading(activity);
				if (activity.isFinishing()) {
					return;
				}
				if (rpInfoModel.isSuccess()) {
					// 获得红包信息成功
					if (rpInfoModel.envelopeStatus == 4 || rpInfoModel.envelopeStatus == 1) {
						// 普通红包 用户自己点击自己,或者已领取红包 - 直接跳转到红包详情页
						RpDetailActivity.intent(activity, 0, rpInfoModel, userid, thirdToken, envelopeId);
					} else {
						OpenRpActivity.intent(activity, callBack, rpInfoModel, userid, thirdToken, envelopeId, key);
					}
				} else if ("U0044".equals(rpInfoModel.respstat)) {// 未完成实名认证
					showBindCardTipDialog(activity, userid, thirdToken,DIALOG_AUTH);
				} else if ("U0046".equals(rpInfoModel.respstat)) {// 用户没有设置密码
					showBindCardTipDialog(activity,userid,thirdToken,DIALOG_SET_PWD);
				}else{
					ToastUtil.showToast(activity, rpInfoModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				ToastUtil.showToast(activity, activity.getString(R.string.jrmf_rp_network_error));
				DialogDisplay.getInstance().dialogCloseLoading(activity);
			}
		});
	}

	/**
	 * 显示绑卡提示弹框
	 *
	 */
	private static void showBindCardTipDialog(final Activity activity, final String userId, final String thirdToken, final int dialogType) {
		String title = dialogType == DIALOG_AUTH ? activity.getString(R.string.jrmf_rp_auth_tip):activity.getString(R.string.jrmf_rp_set_pwd_tip);
		bindCardTipDialog = DialogDisplay.getInstance().dialogLeftAndRight(LARDialogFragment.FROM_DEFAULT,title, activity.getString(R.string.jrmf_rp_quit),
				activity.getString(R.string.jrmf_rp_confirm), new LARDialogFragment.InputPwdErrorListener() {

					@Override public void onLeft() {
						if (bindCardTipDialog != null) {
							bindCardTipDialog.dismissAllowingStateLoss();
							bindCardTipDialog = null;
						}
					}

					@Override public void onRight() {
						if (dialogType == DIALOG_AUTH){
							AuthenActivity.openRpIntent(activity, userId, thirdToken);
						}else{
							PwdH5Activity.intentOpenRp(activity,0,userId,thirdToken);
						}
						if (bindCardTipDialog != null) {
							bindCardTipDialog.dismissAllowingStateLoss();
							bindCardTipDialog = null;
						}
					}
				});
		bindCardTipDialog.setCancelable(false);
		bindCardTipDialog.showAllowingStateLoss(activity.getFragmentManager(), activity.getClass().getSimpleName());
	}

	/**
	 * 更新用戶信息接口 此接口需要开发者自己回调处理
	 *
	 * @param custUid
	 *            【必传】用户ID|当前用户ID
	 * @param thirdToken
	 *            【必传】三方签名令牌（服务端计算后给到app， 服务端算法为md5（custUid+appsecret）
	 * @param nickName
	 *            【必传】将要更新的用户昵称
	 * @param avatar
	 *            【必传】将要更新的用户头像URL
	 */
	public static void updateUserInfo(final String custUid, String thirdToken, final String nickName, final String avatar, OkHttpModelCallBack<BaseModel> callBack) {
		RpHttpManager.updateUserInfo(custUid, thirdToken, nickName, avatar, callBack);
	}



	/**
	 * 跳转到发红包页面 不对外调用
	 *
	 * @param context
	 * @param intent
	 * @param targetId
	 * @param userid
	 * @param username
	 * @param usericon
	 * @param requestCode
	 */
	private static void exeStartSendForResult(Activity context, Intent intent, String targetId, String userid, String thirdToken, int groupNum, String username, String usericon, int requestCode) {
		Bundle bundle = new Bundle();
		bundle.putString(ConstantUtil.R_TargetId, targetId);
		bundle.putString(ConstantUtil.JRMF_USER_ID, userid);
		bundle.putString(ConstantUtil.JRMF_THIRD_TOKEN, thirdToken);
		bundle.putInt(ConstantUtil.GNUM, groupNum);

		bundle.putString(ConstantUtil.JRMF_USER_NAME, username);
		bundle.putString(ConstantUtil.JRMF_USER_ICON, usericon);

		intent.putExtras(bundle);
		context.startActivityForResult(intent, requestCode);
	}

	/**
	 * 微信支付-成功回调后需要关闭支付和发送红包页面 会通过ActivityResult回调发送结果
	 */
	public static void closePayAndSendRpPageWithResult() {
		PActivity pActivity = CusActivityManager.getInstance().findActivity(PActivity.class);
		if (pActivity != null) {
			pActivity.finishWithResult();
		}
	}

	/**
	 * 根据intent携带的数据,封装发送的红包对象
	 *
	 * @param intent
	 * @return
	 */
	public static EnvelopeBean getEnvelopeInfo(Intent intent) {
		if (intent == null) {
			return null;
		}
		EnvelopeBean bean = new EnvelopeBean();
		bean.setEnvelopesID(intent.getStringExtra("envelopesID"));
		bean.setEnvelopeMessage(intent.getStringExtra("envelopeMessage"));
		bean.setEnvelopeName(intent.getStringExtra("envelopeName"));
		return bean;
	}

	/**
	 * 根据intent携带的数据,封装发送的红包对象
	 *
	 * @param intent
	 * @return
	 */
	public static TransAccountBean getTransAccountBean(Intent intent) {
		if (intent == null) {
			return null;
		}
		TransAccountBean bean = new TransAccountBean();
		bean.setTransferOrder(intent.getStringExtra("transferOrder"));
		bean.setTransferAmount(intent.getStringExtra("transferAmount"));
		bean.setTransferDesc(intent.getStringExtra("transferDesc"));
		bean.setTransferStatus(intent.getStringExtra("transferStatus"));
		return bean;
	}
}
