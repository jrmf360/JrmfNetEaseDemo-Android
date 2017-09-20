package com.jrmf360.neteaselib.wallet.ui;

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
import com.jrmf360.neteaselib.base.view.TitleBar;
import com.jrmf360.neteaselib.wallet.http.WalletHttpManager;
import com.jrmf360.neteaselib.wallet.http.model.ButtomModel;
import com.jrmf360.neteaselib.wallet.http.model.RpInfoModel;

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

	private int				mListViewHeight, page = 1, pageCount;

	private boolean			isScroll	= false;

	private ButtomModel buttomBean;

	private List<Object>	rpItemModelList;

	private String			rpId;

	private int				hasLeft;

	/**
	 * 跳转到红包详情页面
	 *
	 * @param fromActivity
	 * @param fromKey
	 * @param rpId
	 */
	public static void intent(Activity fromActivity, int fromKey, String rpId) {
		Intent intent = new Intent(fromActivity, RpDetailActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt("fromKey", fromKey);
		bundle.putString("rpId", rpId);
		intent.putExtras(bundle);
		fromActivity.startActivity(intent);
	}

	@Override public int getLayoutId() {
		return R.layout.jrmf_w_activity_rp_detail;
	}

	@Override public void initView() {
		titleBar = (TitleBar) findViewById(R.id.jrmf_w_titleBar);
		listView = (ListView) findViewById(R.id.listView);
		tv_look_rp_history = (TextView) findViewById(R.id.tv_look_rp_history);
		View headView = View.inflate(this, R.layout.jrmf_w_header_rp_detail, null);
		iv_avatar = (ImageView) headView.findViewById(R.id.iv_avatar);
		tv_username = (TextView) headView.findViewById(R.id.tv_username);
		tv_bless = (TextView) headView.findViewById(R.id.tv_bless);
		tv_rec_amount = (TextView) headView.findViewById(R.id.tv_rec_amount);
		tv_rp_num = (TextView) headView.findViewById(R.id.tv_rp_num);
		listView.addHeaderView(headView);
	}

	@Override public void initListener() {
		titleBar.getIvBack().setOnClickListener(this);
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
		titleBar.setTitle(SPManager.getInstance().getString(this, "partner_name", ""));
		if (bundle != null) {
			rpId = bundle.getString("rpId");
			rpItemModelList = new ArrayList<>();
			rpDetailAdapter = new RpDetailAdapter(this, rpItemModelList);
			listView.setAdapter(rpDetailAdapter);

			// 底部提示语
			int random = new Random().nextInt(10);
			tv_look_rp_history.setText(random % 2 == 0 ? getString(R.string.jrmf_w_receive_grab_tip1) : getString(R.string.jrmf_w_receive_grab_tip2));
			tv_look_rp_history.setTextColor(getResources().getColor(R.color.jrmf_w_gray));
			// 从网络加载数据
			DialogDisplay.getInstance().dialogLoading(context, getString(R.string.jrmf_w_loading),this);
			loadRpDetail(rpId);
		}
	}

	/**
	 * 显示上一页传递过来的数据
	 *
	 * @param rpInfoModel
	 */
	private void showData(RpInfoModel rpInfoModel) {
		hasLeft = rpInfoModel.hasLeft;
		// 先显示从上个页面传递过来的数据
		// 名字
		if (StringUtil.isNotEmpty(rpInfoModel.username)) {
			tv_username.setText(String.format(getString(R.string.jrmf_w_who_rp), rpInfoModel.username));
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
			DrawableUtil.setRightDrawable(context, tv_username, R.drawable.jrmf_w_ic_pin, true);
		} else {
			DrawableUtil.setRightDrawable(context, tv_username, R.drawable.jrmf_w_ic_pin, false);
		}
		// 红包金额
		if (StringUtil.isNotEmptyAndNull(rpInfoModel.grabMoney) && StringUtil.formatMoneyDouble(rpInfoModel.grabMoney) > 0) {
			// 抢到了钱
			SpannableString spannableString = new SpannableString(String.format(getString(R.string.jrmf_w_some_yuan),rpInfoModel.grabMoney));
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
					tv_rp_num.setText(String.format(getString(R.string.jrmf_w_single_rp_done), rpInfoModel.total, rpInfoModel.totalMoney));
				} else {
					// 未领完
					tv_rp_num.setText(String.format(getString(R.string.jrmf_w_single_rp), rpInfoModel.totalMoney));
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
							tv_rp_num.setText(String.format(getString(R.string.jrmf_w_single_rp_done), rpInfoModel.total, rpInfoModel.totalMoney));
						} else {
							// 未领完
							tv_rp_num.setText(String.format(getString(R.string.jrmf_w_single_rp), rpInfoModel.totalMoney));
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
							tv_rp_num.setText(String.format(getString(R.string.jrmf_w_single_rp), rpInfoModel.totalMoney));
							showReturnMonty();
						} else if (rpInfoModel.hasLeft == 0) {
							// 已领完
							tv_rp_num.setText(String.format(getString(R.string.jrmf_w_single_rp_done), rpInfoModel.total, rpInfoModel.totalMoney));
						} else {
							// 已领取未领完
							tv_rp_num.setText(String.format(getString(R.string.jrmf_w_self_rp_has_left), rpInfoModel.receTotal, rpInfoModel.total, rpInfoModel.recTotalMoney, rpInfoModel.totalMoney));
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
						tv_rp_num.setText(String.format(getString(R.string.jrmf_w_pin_rp_done), rpInfoModel.total, rpInfoModel.recTotalMoney, rpInfoModel.grabTimes));
					} else {
						// 未领完
						tv_rp_num.setText(String.format(getString(R.string.jrmf_w_self_rp_has_left), rpInfoModel.receTotal, rpInfoModel.total, rpInfoModel.recTotalMoney, rpInfoModel.totalMoney));
						showReturnMonty();
					}
				} else {
					// 别人
					if (rpInfoModel.hasLeft == 0) {
						// 已领完
						tv_rp_num.setText(String.format(getString(R.string.jrmf_w_pin_rp_other_done), rpInfoModel.total, rpInfoModel.grabTimes));
					} else {
						// 未领完
						tv_rp_num.setText(String.format(getString(R.string.jrmf_w_pin_rp_other), rpInfoModel.receTotal, rpInfoModel.total));
					}
				}
			}
		}

		pageCount = rpInfoModel.pageCount;
		if (rpInfoModel.receiveHistory != null && rpInfoModel.receiveHistory.size() > 0) {
			if (buttomBean == null){
				buttomBean = new ButtomModel();
				rpItemModelList.addAll(rpInfoModel.receiveHistory);
				rpItemModelList.add(buttomBean);
			}else{
				rpItemModelList.addAll(rpItemModelList.size() - 1, rpInfoModel.receiveHistory);
			}
			if (rpInfoModel.receiveHistory.size() > 9) {
				buttomBean.isMore = true;
			} else {
				buttomBean.isMore = false;
			}
		}
		rpDetailAdapter.notifyDataSetChanged();
	}

	/**
	 * 发送者查看－红包未领完
	 */
	private void showReturnMonty() {
		tv_look_rp_history.setVisibility(View.VISIBLE);
		tv_look_rp_history.setText(getString(R.string.jrmf_w_no_grab_tip));
		tv_look_rp_history.setTextColor(getResources().getColor(R.color.jrmf_w_gray));
	}


	/**
	 * 从网络获得红包详情
	 *
	 * @param rpId
	 */
	private void loadRpDetail(String rpId) {
		WalletHttpManager.rpDetail(context, rpId, page, new OkHttpModelCallBack<RpInfoModel>() {

			@Override public void onSuccess(final RpInfoModel rpInfoModel) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				if (rpInfoModel == null) {
					ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
					return;
				}
				if (rpInfoModel.isSuccess()) {
					page++;
					// 请求网络获得数据成功
					showData(rpInfoModel);
				}
			}

			@Override public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(context);
				ToastUtil.showToast(context, getString(R.string.jrmf_w_net_error_l));
			}
		});
	}

	/**
	 * 从网络加载下一页数据
	 */
	private void loadNextPage() {
		loadRpDetail(rpId);
	}

	@Override public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.tv_look_rp_history) {

		}
	}

	// -----------------------------adapter-------------------------------------------------------------------------------------------
	class RpDetailAdapter extends MulTypeListViewAdapter<Object> {

		public RpDetailAdapter(Context context, List<Object> datas) {
			super(context, datas);
		}

		@Override public int getItemViewType(int position) {
			if (rpItemModelList.get(position) instanceof ButtomModel) {
				return 1;
			} else {
				return 0;
			}
		}

		@Override public int getViewTypeCount() {
			return 2;
		}

		@Override public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0) {
				// 红包item
				RpInfoModel.RpItemModel rpItemModel = (RpInfoModel.RpItemModel) rpItemModelList.get(position);
				ViewHolder itemHolder = ViewHolder.get(context, convertView, parent, R.layout.jrmf_w_item_rp_detail, position);
				TextView tv_name = itemHolder.getView(R.id.tv_name);
				TextView tv_time = itemHolder.getView(R.id.tv_time);
				TextView tv_amount = itemHolder.getView(R.id.tv_amount);
				TextView tv_best = itemHolder.getView(R.id.tv_best);

				tv_name.setText(rpItemModel.nickname);
				tv_time.setText(rpItemModel.activateTime);
				tv_amount.setText(String.format(getString(R.string.jrmf_w_some_yuan),rpItemModel.moneyYuan));
				// 是不是手气最佳
				if (rpItemModel.isBLuck == 1) {
					// 手气最佳
					if (hasLeft != 0) {
						tv_best.setVisibility(View.INVISIBLE);
					} else {
						tv_best.setVisibility(View.VISIBLE);
					}
				} else {
					tv_best.setVisibility(View.INVISIBLE);
				}
				return itemHolder.getConvertView();
			} else {
				// 底部加载框
				ViewHolder holderAddCard = ViewHolder.get(context, convertView, parent, R.layout.jrmf_w_item_list_buttom, position);
				ButtomModel buttomModel = (ButtomModel) rpItemModelList.get(position);
				ImageView imageview_progress_spinner = holderAddCard.getView(R.id.imageview_progress_spinner);
				TextView tv_title = holderAddCard.getView(R.id.tv_title);
				if (buttomModel.isMore) {
					imageview_progress_spinner.setVisibility(View.VISIBLE);
					tv_title.setText(getString(R.string.jrmf_w_loading));
					AnimationDrawable drawable = (AnimationDrawable) imageview_progress_spinner.getDrawable();
					drawable.start();
				} else {
					imageview_progress_spinner.setVisibility(View.GONE);
					if (rpItemModelList != null && rpItemModelList.size() >= 10) {
						tv_title.setText(getString(R.string.jrmf_w_to_buttom));
					} else {
						tv_title.setText("");
					}
				}
				return holderAddCard.getConvertView();
			}
		}
	}

	@Override public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE) {
			isScroll = false;
		} else {
			isScroll = true;
		}
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
					if (buttomBean.isMore == true) {
						buttomBean.isMore = false;
						rpDetailAdapter.notifyDataSetChanged();
					}
				}
			}
		}
	}
}
