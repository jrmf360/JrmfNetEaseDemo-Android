package com.jrmf360.neteaselib.rp.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.adapter.MulTypeListViewAdapter;
import com.jrmf360.neteaselib.base.adapter.ViewHolder;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.DrawableUtil;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.SPManager;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.rp.bean.ButtomBean;
import com.jrmf360.neteaselib.rp.constants.ConstantUtil;
import com.jrmf360.neteaselib.rp.http.RpHttpManager;
import com.jrmf360.neteaselib.rp.http.model.RpInfoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @创建人 honglin
 * @创建时间 16/11/17 上午10:50
 * @类描述 红包详情页面
 */
public class RpDetailActivity extends BaseActivity implements AbsListView.OnScrollListener {

	private ListView		listView;

	private ImageView		iv_avatar;

	private TextView		tv_look_rp_history, tv_username, tv_bless, tv_rec_amount, tv_rp_num;

	private RpDetailAdapter	rpDetailAdapter;

	private int				mListViewHeight, page = 2, pageCount;

	private boolean			isScroll		= false;

	private ButtomBean buttomBean;

	private List<Object>	rpItemModelList;

	private String			rpId;

	private boolean			canLookRpHis	= true;												// 是否能够查看我的红包记录

	private int				fromKey			= -1;

	private int				hasLeft			= -1;

	private String username;
	private String usericon;

	public static void intent(Activity fromActivity, int fromKey, RpInfoModel rpInfoModel, String userId, String thirdToken, String rpId) {
		Intent intent = new Intent(fromActivity, RpDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("fromKey", fromKey);
		bundle.putString(ConstantUtil.JRMF_USER_ID, userId);
		bundle.putString(ConstantUtil.JRMF_THIRD_TOKEN, thirdToken);
		bundle.putString("rpId", rpId);
		bundle.putSerializable("rpInfoModel", rpInfoModel);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	/**
	 * 跳转到红包详情页面 没有携带红包信息 1 2
	 * 1 查看大家手气
	 * 2 查看红包详情
	 *
	 * @param fromActivity
	 * @param fromKey
	 * @param userId
	 * @param thirdToken
	 * @param rpId
	 * @param userName
	 * @param userIcon
	 */
	public static void intentNoRpInfo(Activity fromActivity, int fromKey, String userId, String thirdToken, String rpId, String userName, String userIcon) {
		Intent intent = new Intent(fromActivity, RpDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("fromKey", fromKey);
		bundle.putString("rpId", rpId);
		bundle.putString(ConstantUtil.JRMF_USER_ID, userId);
		bundle.putString(ConstantUtil.JRMF_THIRD_TOKEN, thirdToken);
		bundle.putString(ConstantUtil.JRMF_USER_NAME, userName);
		bundle.putString(ConstantUtil.JRMF_USER_ICON, userIcon);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_rp_activity_rp_detail;
	}

	@Override public void initView() {
		listView = (ListView) findViewById(R.id.listView);
		tv_look_rp_history = (TextView) findViewById(R.id.tv_look_rp_history);
		View headView = View.inflate(this, R.layout.jrmf_rp_header_rp_detail, null);
		iv_avatar = (ImageView) headView.findViewById(R.id.iv_avatar);
		tv_username = (TextView) headView.findViewById(R.id.tv_username);
		tv_bless = (TextView) headView.findViewById(R.id.tv_bless);
		tv_rec_amount = (TextView) headView.findViewById(R.id.tv_rec_amount);
		tv_rp_num = (TextView) headView.findViewById(R.id.tv_rp_num);
		listView.addHeaderView(headView);
	}

	@Override public void initListener() {
		actionBarView.getIvBack().setOnClickListener(this);
		tv_look_rp_history.setOnClickListener(this);
		listView.setOnScrollListener(this);
		listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

			@SuppressLint("NewApi") @Override public void onGlobalLayout() {
				mListViewHeight = listView.getHeight();

				if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
					listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				} else {
					listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		});
	}

	@Override protected void initData(Bundle bundle) {
		actionBarView.setTitle(SPManager.getInstance().getString(this, "partner_name", ""));
		rpItemModelList = new ArrayList<>();
		rpDetailAdapter = new RpDetailAdapter(this, rpItemModelList);
		if (bundle != null) {
			fromKey = bundle.getInt("fromKey");
			thirdToken = bundle.getString("thirdToken");
			rpId = bundle.getString("rpId");
			if (fromKey == 0) {
				// 有数据
				showData((RpInfoModel) bundle.getSerializable("rpInfoModel"), false);
			} else {
				if (fromKey == 2) {
					int random = new Random().nextInt(10);
					tv_look_rp_history.setText(random % 2 == 0 ? getString(R.string.jrmf_rp_receive_grab_tip1) : getString(R.string.jrmf_rp_receive_grab_tip2));
					tv_look_rp_history.setTextColor(getResources().getColor(R.color.jrmf_rp_gray));
					tv_look_rp_history.setClickable(false);
				}
				username = bundle.getString(ConstantUtil.JRMF_USER_NAME);
				usericon = bundle.getString(ConstantUtil.JRMF_USER_ICON);
				loadRpDetail(userid, thirdToken, rpId, username, usericon);
			}
			listView.setAdapter(rpDetailAdapter = new RpDetailAdapter(this, rpItemModelList));
		}
	}

	private void showData(RpInfoModel rpInfoModel, boolean nextMore) {
		hasLeft = rpInfoModel.hasLeft;
		// 先显示从上个页面传递过来的数据
		// 名字
		if (StringUtil.isNotEmpty(rpInfoModel.username)) {
			tv_username.setText(String.format(getString(R.string.jrmf_rp_who_rp), rpInfoModel.username));
		}
		// 头像
		if (StringUtil.isNotEmpty(rpInfoModel.avatar)) {
			ImageLoadUtil.getInstance().loadImage(iv_avatar, rpInfoModel.avatar);
		}
		// 祝福语
		tv_bless.setText(rpInfoModel.content);
		// 是否是拼手气红包
		if (rpInfoModel.type == 1) {
			// 拼手气
			DrawableUtil.setRightDrawable(context, tv_username, R.drawable.jrmf_rp_ic_pin, true);
		} else {
			DrawableUtil.setRightDrawable(context, tv_username, R.drawable.jrmf_rp_ic_pin, false);
		}
		// 红包金额
		if (StringUtil.isNotEmptyAndNull(rpInfoModel.grabMoney) && StringUtil.formatMoneyDouble(rpInfoModel.grabMoney) > 0) {
			// 抢到了钱
			SpannableString spannableString = new SpannableString(String.format(getString(R.string.jrmf_rp_money_yuan),rpInfoModel.grabMoney));
			spannableString.setSpan(new AbsoluteSizeSpan(100), 0, spannableString.length() - 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
			tv_rec_amount.setText(spannableString);
		} else {
			// 没有抢到钱
			tv_rec_amount.setVisibility(View.GONE);
		}

		// 红包提示语
		// 单人红包
		if (rpInfoModel.isGroup == 0) {
			if (rpInfoModel.isSelf == 1) {
				// 自己
				if (rpInfoModel.hasLeft == 0) {
					// 已领完
					tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_single_rp_done), rpInfoModel.total, rpInfoModel.totalMoney));
				} else {
					// 未领完
					tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_single_rp), rpInfoModel.totalMoney));
					showReturnMonty();
				}
			} else {
				// 别人
				tv_rp_num.setText("");
				return;
			}
		} else {
			// 群普通红包
			if (rpInfoModel.type == 0) {
				// 普通群红包
				if (rpInfoModel.total == 1) {
					// 单个普通群红包
					if (rpInfoModel.isSelf == 1) {
						// 自己
						if (rpInfoModel.hasLeft == 0) {
							// 已领完
							tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_single_rp_done), rpInfoModel.total, rpInfoModel.totalMoney));
						} else {
							// 未领完
							tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_single_rp), rpInfoModel.totalMoney));
							showReturnMonty();
						}
					} else {
						// 别人
						tv_rp_num.setText("");
						return;
					}
				} else {
					// 多个普通群红包
					if (rpInfoModel.isSelf == 1) {
						// 自己
						if (rpInfoModel.receTotal == 0) {
							// 未领取
							tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_single_rp), rpInfoModel.totalMoney));
							showReturnMonty();
						} else if (rpInfoModel.hasLeft == 0) {
							// 已领完
							tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_single_rp_done), rpInfoModel.total, rpInfoModel.totalMoney));
						} else {
							// 已领取未领完
							tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_self_rp_has_left), rpInfoModel.receTotal, rpInfoModel.total, rpInfoModel.recTotalMoney, rpInfoModel.totalMoney));
							showReturnMonty();
						}
					} else {
						// 别人
						tv_rp_num.setText("");
						return;
					}
				}
			} else {
				// 拼手气群红包
				if (rpInfoModel.isSelf == 1) {
					// 自己
					if (rpInfoModel.hasLeft == 0) {
						// 已领完
						tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_pin_rp_done), rpInfoModel.total, rpInfoModel.recTotalMoney, rpInfoModel.grabTimes));
					} else {
						// 未领完
						tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_self_rp_has_left), rpInfoModel.receTotal, rpInfoModel.total, rpInfoModel.recTotalMoney, rpInfoModel.totalMoney));
						showReturnMonty();
					}
				} else {
					// 别人
					if (rpInfoModel.hasLeft == 0) {
						// 已领完
						tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_pin_rp_other_done), rpInfoModel.total, rpInfoModel.grabTimes));
					} else {
						// 未领完
						tv_rp_num.setText(String.format(getString(R.string.jrmf_rp_pin_rp_other), rpInfoModel.receTotal, rpInfoModel.total));
					}
				}
			}
		}

		if (!nextMore) {
			if (rpInfoModel.receiveHistory != null && rpInfoModel.receiveHistory.size() > 0) {
				rpItemModelList.addAll(rpInfoModel.receiveHistory);
				buttomBean = new ButtomBean();
				if (rpInfoModel.receiveHistory.size() > 9) {
					buttomBean.isMore = true;
					pageCount = 2;
				} else {
					buttomBean.isMore = false;
				}
				rpItemModelList.add(buttomBean);
			}
			rpDetailAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 发送者查看－红包未领完
	 */
	private void showReturnMonty() {
		tv_look_rp_history.setVisibility(View.VISIBLE);
		if (fromKey == 2) {
			tv_look_rp_history.setText(getString(R.string.jrmf_rp_no_grab_tip2));
		} else {
			tv_look_rp_history.setText(getString(R.string.jrmf_rp_no_grab_tip));
		}
		tv_look_rp_history.setTextColor(getResources().getColor(R.color.jrmf_rp_gray));
		canLookRpHis = false;
	}

	/**
	 * 从网络获得红包详情
	 *
	 * @param userId
	 * @param thirdToken
	 * @param rpId
	 * @param userName
	 * @param userIcon
	 */
	private void loadRpDetail(String userId, String thirdToken, String rpId, String userName, String userIcon) {
		DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_rp_loading));
		RpHttpManager.rpDetail(context, userId, thirdToken, rpId, userName, userIcon, new OkHttpModelCallBack<RpInfoModel>() {

			@Override public void onSuccess(final RpInfoModel rpInfoModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (rpInfoModel == null) {
					ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
					return;
				}
				if (rpInfoModel.isSuccess()) {
					// 请求网络获得数据成功
					showData(rpInfoModel, false);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}

	/**
	 * 从网络加载下一页数据
	 */
	private void loadNextPage() {

		RpHttpManager.loadNextPage(context, userid, thirdToken, rpId, username, usericon, page, new OkHttpModelCallBack<RpInfoModel>() {

			@Override public void onSuccess(RpInfoModel rpNextModel) {
				if (rpNextModel.isSuccess()) {
					pageCount = rpNextModel.pageCount;
					page++;
					if (rpNextModel.receiveHistory != null && rpNextModel.receiveHistory.size() > 0) {
						buttomBean.isMore = rpNextModel.receiveHistory.size() >= 10;
						rpItemModelList.addAll(rpItemModelList.size() - 1, rpNextModel.receiveHistory);
						rpDetailAdapter.notifyDataSetChanged();
						showData(rpNextModel, true);
					} else {
						if (buttomBean != null) {
							buttomBean.isMore = false;
							rpDetailAdapter.notifyDataSetChanged();
							showData(rpNextModel, true);
						}
					}
				}
			}

			@Override public void onFail(String result) {
				ToastUtil.showToast(context, getString(R.string.jrmf_rp_network_error));
			}
		});
	}

	@Override public void onClick(int id) {
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.tv_look_rp_history) {
			// 查看我的红包记录-跳转到我的红包页面
			if (canLookRpHis) {
				MyRpActivity.intent(this, userid, thirdToken);
			}
		}
	}

	class RpDetailAdapter extends MulTypeListViewAdapter<Object> {

		@Override public int getItemViewType(int position) {
			if (rpItemModelList.get(position) instanceof ButtomBean) {
				return 1;
			} else {
				return 0;
			}
		}

		@Override public int getViewTypeCount() {
			return 2;
		}

		public RpDetailAdapter(Context context, List<Object> datas) {
			super(context, datas);
		}

		@Override public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0) {
				// 红包item
				RpInfoModel.RpItemModel rpItemModel = (RpInfoModel.RpItemModel) rpItemModelList.get(position);
				ViewHolder itemHolder = ViewHolder.get(context, convertView, parent, R.layout.jrmf_rp_item_rp_detail, position);
				TextView tv_name = itemHolder.getView(R.id.tv_name);
				TextView tv_time = itemHolder.getView(R.id.tv_time);
				TextView tv_amount = itemHolder.getView(R.id.tv_amount);
				TextView tv_best = itemHolder.getView(R.id.tv_best);

				tv_name.setText(rpItemModel.nickname);
				tv_time.setText(rpItemModel.activateTime);
				tv_amount.setText(String.format(getString(R.string.jrmf_rp_money_yuan),rpItemModel.moneyYuan));
				// 是不是手气最佳
				if (rpItemModel.isBLuck == 1 && hasLeft == 0) {
					// 手气最佳
					tv_best.setVisibility(View.VISIBLE);
				} else {
					tv_best.setVisibility(View.INVISIBLE);
				}
				return itemHolder.getConvertView();
			} else {
				// 底部加载框
				ViewHolder holderAddCard = ViewHolder.get(context, convertView, parent, R.layout.jrmf_rp_item_list_buttom, position);
				ButtomBean buttomModel = (ButtomBean) rpItemModelList.get(position);
				ImageView imageview_progress_spinner = holderAddCard.getView(R.id.imageview_progress_spinner);
				TextView tv_title = holderAddCard.getView(R.id.tv_title);
				if (buttomModel.isMore) {
					imageview_progress_spinner.setVisibility(View.VISIBLE);
					tv_title.setText(getString(R.string.jrmf_rp_loading));
					AnimationDrawable drawable = (AnimationDrawable) imageview_progress_spinner.getDrawable();
					drawable.start();
				} else {
					imageview_progress_spinner.setVisibility(View.GONE);
					if (rpItemModelList != null && rpItemModelList.size() > 9) {
						tv_title.setText(getString(R.string.jrmf_rp_to_buttom));
					} else {
						tv_title.setText("");
					}
				}
				return holderAddCard.getConvertView();
			}
		}
	}

	@Override public void onScrollStateChanged(AbsListView view, int scrollState) {
		isScroll = scrollState != SCROLL_STATE_IDLE;
	}

	@Override public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if ((firstVisibleItem + visibleItemCount) == totalItemCount && isScroll) {
			View lastVisibleItemView = listView.getChildAt(listView.getChildCount() - 1);
			if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == mListViewHeight) {
				// 滚动到底部
				if (page <= pageCount) {
					// 加载数据
					loadNextPage();
				} else {
					if (buttomBean != null && buttomBean.isMore == true) {
						buttomBean.isMore = false;
						rpDetailAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	}

}
