package com.jrmf360.neteaselib.rp.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.manager.CusActivityManager;
import com.jrmf360.neteaselib.base.utils.LogUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.rp.bean.H5ResultBean;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.UrlModel;
import com.jrmf360.neteaselib.rp.widget.ActionBarView;

import java.net.URLDecoder;

/**
 * @创建人 honglin
 * @创建时间 2017/5/4 下午2:41
 * @类描述 乌行涉及密码的H5页面
 */
public class PwdH5Activity extends BaseActivity {

	private WebView	webView;

	private int		index;

	/**
	 * 仅从拆红包需要设置密码的时候调用该方法更新thirdToken，和userid
	 *
	 * @param fromActivity
	 * @param index
	 *            0 设置密码 1 修改密码 2 忘记密码 3 设置免密 4 取消免密 5 发送红包 6 用户协议
	 */
	public static void intentOpenRp(Activity fromActivity, int index,String userId,String thirdToken) {
		Intent intent = new Intent(fromActivity, PwdH5Activity.class);
		Bundle bundle = new Bundle();
		bundle.putString(ConstantUtil.JRMF_USER_ID, userId);
		bundle.putString(ConstantUtil.JRMF_THIRD_TOKEN, thirdToken);
		bundle.putInt("index", index);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	/**
	 * 根据不同的key来跳转到对应的页面
	 * 
	 * @param fromActivity
	 * @param index
	 *            0 设置密码 1 修改密码 2 忘记密码 3 设置免密 4 取消免密 5 发送红包 6 用户协议
	 */
	public static void intent(Activity fromActivity, int index) {
		Intent intent = new Intent(fromActivity, PwdH5Activity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("index", index);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	/**
	 * 根据不同的key来跳转到对应的页面
	 *
	 * @param fromActivity
	 * @param index
	 *            0 设置密码 1 修改密码 2 忘记密码 3 设置免密 4 取消免密 5 发送红包 6 用户协议
	 */
	public static void intentSendRp(Activity fromActivity, int index,String url) {
		Intent intent = new Intent(fromActivity, PwdH5Activity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("index", index);
		bundle.putString("url", url);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	/**
	 * 从添加银行卡第二步跳转过来
	 *
	 * @param fromActivity
	 * @param index
	 *            0 设置密码 1 修改密码 2 忘记密码 3 设置免密 4 取消免密 5 发送红包
	 */
	public static void intentFromAddCardSec(Activity fromActivity, int index,String url,int fromKey) {
		Intent intent = new Intent(fromActivity, PwdH5Activity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("index", index);
		bundle.putInt("fromKey", fromKey);
		bundle.putString("url", url);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_pwdh5;
	}

	@Override public void initView() {
		actionBarView = (ActionBarView) findViewById(R.id.actionbar);
		webView = (WebView) findViewById(R.id.webView);
		setWebView();
	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
	}

	@Override protected void initData(Bundle bundle) {
		if (bundle != null) {
			index = bundle.getInt("index");
			int fromKey = bundle.getInt("fromKey");

			if (index == 6){//用户协议
				DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading));
				webView.loadUrl(ConstantUtil.H5_PROTOCOL_URL);
				return;
			}

			if (fromKey == ConstantUtil.FROM_ADD_CARD_SEC && index == 0){//从添加银行卡第二步跳转过来-设置密码
				DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading));
				String url = bundle.getString("url");
				webView.loadUrl(url);
				return;
			}

			if (index == 5) {//发红包
//				actionBarView.setVisibility(View.GONE);
				DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading));
				String url = bundle.getString("url");
				webView.loadUrl(url);
			} else {
				DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading));
				RpHttpManager.getUrl(context,userid,thirdToken,index, new OkHttpModelCallBack<UrlModel>() {

					@Override public void onSuccess(UrlModel urlModel) {
						if (urlModel != null && urlModel.isSuccess()) {
							webView.loadUrl(urlModel.url);
						}else{
							DialogDisplay.getInstance().dialogCloseLoading(context);
							ToastUtil.showToast(context,urlModel.respmsg);
						}
					}

					@Override public void onFail(String result) {
						ToastUtil.showToast(context, result);
						DialogDisplay.getInstance().dialogCloseLoading(context);
						finish();
					}
				});
			}

		}
	}

	/**
	 * 获得发送红包的url
	 * 
	 * @param rpId
	 */
	private void getBalancePayRedPacketUrl(String rpId) {
		RpHttpManager.getBalancePayRedPacketUrl(context, userid,thirdToken,rpId, new OkHttpModelCallBack<UrlModel>() {

			@Override public void onSuccess(UrlModel urlModel) {
				if (urlModel != null && urlModel.isSuccess()) {
					webView.loadUrl(urlModel.url);
				}else{
					DialogDisplay.getInstance().dialogCloseLoading(context);
					ToastUtil.showToast(context,urlModel.respmsg);
				}
			}

			@Override public void onFail(String result) {
				ToastUtil.showToast(context, result);
				DialogDisplay.getInstance().dialogCloseLoading(context);
				finish();
			}
		});
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_back) {
			onBackPressed();
		}
	}

	/**
	 * 初始化webview
	 */
	private void setWebView() {
		/**
		 * 设置焦点
		 */
		webView.requestFocus();
		WebSettings mWebSettings = webView.getSettings();
		// 支持js
		mWebSettings.setJavaScriptEnabled(true);

		// 设置自适应屏幕，两者合用
		mWebSettings.setUseWideViewPort(true); // 将图片调整到适合webview的大小
		mWebSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			mWebSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
		}

		// 开启 DOM storage API 功能
		mWebSettings.setDomStorageEnabled(true);
		// 开启 database storage API 功能
		mWebSettings.setDatabaseEnabled(true);
		mWebSettings.setAppCacheMaxSize(1024 * 1024 * 8);
		mWebSettings.setAppCacheEnabled(true);
		String appCachePath = getCacheDir().getAbsolutePath();
		mWebSettings.setAppCachePath(appCachePath);

		// 支持缩放，默认为true。是下面那个的前提
		mWebSettings.setSupportZoom(true);
		// 设置内置的缩放控件.若上面是false，则该WebView不可缩放，这个不管设置什么都不能缩放
		mWebSettings.setBuiltInZoomControls(true);

		mWebSettings.setAllowFileAccess(true);
		mWebSettings.setSavePassword(false);
		mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		mWebSettings.setLoadsImagesAutomatically(true);
		mWebSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

		setWebViewClient();
		setWebChromeClient();
	}

	private void setWebViewClient() {
		// WebViewClient主要帮助WebView处理各种通知、请求事件的
		webView.setWebViewClient(new WebViewClient() {

			@Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
			}

			@Override public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				actionBarView.setTitle(view.getTitle());
			}

			@Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// http://192.168.30.120:8081/mfkj-api/uccb/result.shtml?transCode=***&resultCode=*****&respMsg=****
				LogUtil.e("httpOverrideUrl",url);
				if (url.contains("resultNotify.shtml")) {
					// 乌行的url，不加载
					DialogDisplay.getInstance().dialogLoading(context,getString(R.string.jrmf_rp_loading));
				}
				if (url.contains("result.shtml")) {
					DialogDisplay.getInstance().dialogCloseLoading(context);
					// 结果返回的url
					H5ResultBean h5ResultBean = makeResultBean(url);
					if ("SUCCESS".equals(h5ResultBean.resultCode)){//成功
						if ("U00003".equals(h5ResultBean.transCode)) {
							// 设置密码-成功
							ToastUtil.showToast(context, getString(R.string.jrmf_rp_set_pwd_suc));
							finish();
						} else if ("U00004".equals(h5ResultBean.transCode)) {
							// 修改密码-成功
							ToastUtil.showToast(context, getString(R.string.jrmf_rp_update_pwd_suc));
							finish();
						} else if ("U00005".equals(h5ResultBean.transCode)) {
							// 重置密码-成功
							ToastUtil.showToast(context, getString(R.string.jrmf_rp_reset_pwd_suc));
							finish();
						} else if ("U00009".equals(h5ResultBean.transCode)) {
							// 免密协议签订
							ToastUtil.showToast(context, getString(R.string.jrmf_rp_no_pwd_suc));
							finish();
						} else if ("T00004".equals(h5ResultBean.transCode)) {
							// 发红包
							PActivity pActivity = CusActivityManager.getInstance().findActivity(PActivity.class);
							if (pActivity != null){
								pActivity.finishWithResult();
							}
							finish();
						}
					}else{ //失败
						if ("T00004".equals(h5ResultBean.transCode)){//发红包
							ToastUtil.showToast(context, URLDecoder.decode(h5ResultBean.respMsg));
							finish();
						}else{
							ToastUtil.showToast(context,URLDecoder.decode(h5ResultBean.respMsg));
						}
					}
				} else {
					view.loadUrl(url);
				}
				return true;
			}
		});
	}

	/**
	 * 把返回的结果url组装成一个对象
	 * 
	 * @param url
	 * @return
	 */
	private H5ResultBean makeResultBean(String url) {
		H5ResultBean h5ResultBean = null;
		String[] splits = url.split("result.shtml/?");
		if (splits != null && splits.length >= 2) {
			String[] results = splits[1].split("&");
			if (results != null && results.length >= 3) {
				h5ResultBean = new H5ResultBean();
				h5ResultBean.transCode = results[0].substring(results[0].indexOf("=") + 1);
				h5ResultBean.resultCode = results[1].substring(results[1].indexOf("=") + 1);
				h5ResultBean.respMsg = results[2].substring(results[2].indexOf("=") + 1);
			}
		}
		return h5ResultBean;
	}

	private void setWebChromeClient() {
		webView.setWebChromeClient(new WebChromeClient() {

			@Override public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
				actionBarView.setTitle(title);
			}

			@Override public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress >= 95) {
					try {
						DialogDisplay.getInstance().dialogCloseLoading(context);
					} catch (Exception e) {
						LogUtil.e("CommonProgressDialog", e);
					}
				}
			}
		});
	}

	@Override public void onBackPressed() {
		if (webView != null) {
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				super.onBackPressed();
			}
		}
	}

	@Override protected void onDestroy() {
		if (webView != null && webView.getParent() != null) {
			((ViewGroup) webView.getParent()).removeView(webView);
			webView.destroy();
			webView = null;
		}
		super.onDestroy();
	}
}
