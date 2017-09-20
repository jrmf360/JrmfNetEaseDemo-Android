//package com.jrmf360.neteaselib.rp.fragment;
//
//import android.annotation.SuppressLint;
//import android.graphics.drawable.AnimationDrawable;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.widget.AbsListView;
//import android.widget.AbsListView.OnScrollListener;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.ViewAnimator;
//
//import com.jrmf360.neteaselib.R;
//import com.jrmf360.neteaselib.base.adapter.ViewHolder;
//import com.jrmf360.neteaselib.base.fragment.BaseFragment;
//import com.jrmf360.neteaselib.base.fragment.DialogDisplay;
//import com.jrmf360.neteaselib.base.fragment.LoadingDialogFragment;
//import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
//import com.jrmf360.neteaselib.base.utils.StringUtil;
//import com.jrmf360.neteaselib.base.utils.ToastUtil;
//import com.jrmf360.neteaselib.rp.bean.ButtomBean;
//import com.jrmf360.neteaselib.rp.http.RpHttpManager;
//import com.jrmf360.neteaselib.rp.http.model.TradeDetailModel;
//import com.jrmf360.neteaselib.rp.http.model.TradeItemDetail;
//import com.jrmf360.neteaselib.rp.ui.TradeDetailActivity;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 交易详情的Fragment 根据
// * @author honglin
// *
// */
//public class TradeDetailFragment extends BaseFragment implements OnScrollListener{
//	private TradeDetailAdapter tradeDetailAdapter;
//	private ArrayList<Object> datas;
//	private ButtomBean buttomModel;
//	private ListView listView;
//	private ViewAnimator viewAnim;
//	private int mListViewHeight,index = 0,page = 1,pageCount;
//	private boolean isScroll = false,isLoad = false;
//	private String userToken,userId;
//
//	 public static TradeDetailFragment newInstance(String userId,String userToken,int index){
//		 TradeDetailFragment fragment = new TradeDetailFragment();
//         Bundle bundle = new Bundle();
//         bundle.putInt("index", index);
//         bundle.putString("userId", userId);
//         bundle.putString("thirdToken", userToken);
//         fragment.setArguments(bundle);
//         return fragment;
//	 }
//
//	@Override
//	public int getLayoutId() {
//		return R.layout.jrmf_rp_fragment_trade_detail;
//	}
//	@Override
//	public void initView() {
//		listView = (ListView) rootView.findViewById(R.id.listView);
//		viewAnim = (ViewAnimator) rootView.findViewById(R.id.viewAnim);
//	}
//
//	@Override
//	public void initListener() {
//		 listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//	            @SuppressLint("NewApi") @Override
//	            public void onGlobalLayout() {
//	                mListViewHeight = listView.getHeight();
//
//	                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
//	                	listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//	                } else {
//	                	listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//	                }
//	            }
//	        });
//
//		listView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
//				if (position < datas.size() - 1){
//					TradeItemDetail itemDetail = (TradeItemDetail) datas.get(position);
////					TradeDetailActivity.intent(fromActivity,itemDetail.opType,itemDetail.opTime,itemDetail.orderNo,
////							itemDetail.moneyYuan,itemDetail.transferType,itemDetail.charge,itemDetail.tradeName,itemDetail.remark,itemDetail.payTypeDesc);
//
//					TradeDetailActivity.intent(fromActivity, itemDetail.opType, itemDetail.opTime, itemDetail.orderNo,
//							itemDetail.moneyYuan, itemDetail.transferType, itemDetail.charge, itemDetail.tradeName, itemDetail.remark,
//							itemDetail.bankName, itemDetail.bankCardNo, itemDetail.payTypeDesc);
//				}
//			}
//		});
//
//		listView.setOnScrollListener(this);
//	}
//
//	@Override
//	protected void initData(Bundle bundle) {
//		if (bundle == null) {
//			return;
//		}
//		index = bundle.getInt("index");
//		userToken = bundle.getString("thirdToken");
//		userId = bundle.getString("userId");
//		tradeDetailAdapter = new TradeDetailAdapter();
//		listView.setAdapter(tradeDetailAdapter);
//
//		loadDataByIndex();
//		DialogDisplay.getInstance().dialogLoading(fromActivity, getString(R.string.jrmf_rp_loading), (LoadingDialogFragment.LoadingDialogListener) fromActivity);
//	}
//
//	/**
//	 * 根据索引加载网络数据
//	 */
//	private void loadDataByIndex() {
//		isLoad = true;
//		RpHttpManager.tradeHistory(fromActivity,userId,userToken, index, page, new OkHttpModelCallBack<TradeDetailModel>() {
//
//			@Override public void onSuccess(TradeDetailModel tradeDetailModel) {
//				DialogDisplay.getInstance().dialogCloseLoading(fromActivity);
//				if (fromActivity.isFinishing()){
//					return;
//				}
//				if (tradeDetailModel == null) {
//					ToastUtil.showToast(fromActivity, getString(R.string.jrmf_rp_network_error));
//					return;
//				}
//				if (tradeDetailModel.isSuccess()) {
//					List<TradeItemDetail> list = tradeDetailModel.details;
//					if (list != null && list.size() > 0) {
//						// 先添加底部的item
//						if (buttomModel == null) {
//							buttomModel = new ButtomBean();
//							buttomModel.isMore = list.size() > 9;
//						}
//						if (datas == null) {
//							datas = new ArrayList<Object>();
//						}
//						if (!datas.contains(buttomModel)) {
//							datas.add(buttomModel);
//						}
//						datas.addAll(datas.size() - 1, tradeDetailModel.details);
//						if (page == 1) {
//							viewAnim.setDisplayedChild(1);
//							pageCount = tradeDetailModel.pageCount;
//						}
//						page++;
//						tradeDetailAdapter.notifyDataSetChanged();
//						isLoad = false;
//					}else{
//						if (buttomModel != null){
//							buttomModel.isMore = false;
//							tradeDetailAdapter.notifyDataSetChanged();
//						}
//					}
//				} else {
//					ToastUtil.showToast(fromActivity, tradeDetailModel.respmsg);
//					isLoad = false;
//				}
//			}
//
//			@Override public void onFail(String result) {
//				DialogDisplay.getInstance().dialogCloseLoading(fromActivity);
//				ToastUtil.showToast(fromActivity, getString(R.string.jrmf_rp_network_error));
//				isLoad = false;
//			}
//		});
//	}
//
//	/**
//	 * viewHolder
//	 * @author honglin
//	 *
//	 */
//	class TradeDetailAdapter extends BaseAdapter{
//
//		@Override
//		public int getCount() {
//			return datas == null ? 0 : datas.size();
//		}
//
//		@Override
//		public Object getItem(int position) {
//			return datas == null ? null : datas.get(position);
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return position;
//		}
//
//		@Override
//		public int getItemViewType(int position) {
//			if (position == datas.size() - 1) {
//				return 1;
//			}else{
//				return 0;
//			}
//		}
//		@Override
//		public int getViewTypeCount() {
//			return 2;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			int type = getItemViewType(position);
//			if (type == 0) {
//				//交易记录
//				TradeItemDetail itemDetail = (TradeItemDetail) datas.get(position);
//				ViewHolder holderCardList = ViewHolder.get(getActivity(), convertView, parent, R.layout.jrmf_rp_item_trade_history, position);
//				TextView tv_month = holderCardList.getView(R.id.tv_month);
//				TextView tv_tradeType = holderCardList.getView(R.id.tv_tradeType);
//				TextView tv_tradeTime = holderCardList.getView(R.id.tv_tradeTime);
//				TextView tv_tradeMoney = holderCardList.getView(R.id.tv_tradeMoney);
//				TextView tv_tradeState = holderCardList.getView(R.id.tv_tradeState);
//
//				//时间处理
//				if (position == 0) {
//					tv_month.setVisibility(View.VISIBLE);
//					tv_month.setText(itemDetail.dateMonth);
//				}else{
//					//比较当前月时间是否与上一个条目相等
//					TradeItemDetail lastItemDetail = (TradeItemDetail) datas.get(position - 1);
//					if (StringUtil.isNotEmptyAndNull(itemDetail.dateMonth) && !itemDetail.dateMonth.equals(lastItemDetail.dateMonth)) {
//						//时间不相等
//						tv_month.setVisibility(View.VISIBLE);
//						tv_month.setText(itemDetail.dateMonth);
//					}else{
//						tv_month.setVisibility(View.GONE);
//					}
//				}
//				//交易状态
//				tv_tradeState.setText(itemDetail.tradeName);
//
//				tv_tradeType.setText(itemDetail.name);
//				tv_tradeTime.setText(itemDetail.opTime);
//				if ("in".equals(itemDetail.opType)){
//					//收入
//					tv_tradeMoney.setText("+"+itemDetail.moneyYuan);
//					tv_tradeMoney.setTextColor(getResources().getColor(R.color.jrmf_rp_title_bar));
//				}else{
//					//支出
//					tv_tradeMoney.setText("-"+itemDetail.moneyYuan);
//					tv_tradeMoney.setTextColor(getResources().getColor(R.color.color_1bfd00));
//				}
//				return holderCardList.getConvertView();
//			}else{
//				//底部加载更多
//				ViewHolder holderAddCard = ViewHolder.get(getActivity(), convertView, parent, R.layout.jrmf_rp_item_list_buttom, position);
//				ButtomBean buttomModel = (ButtomBean) datas.get(position);
//				ImageView imageview_progress_spinner = holderAddCard.getView(R.id.imageview_progress_spinner);
//				TextView tv_title = holderAddCard.getView(R.id.tv_title);
//				if (buttomModel.isMore) {
//					imageview_progress_spinner.setVisibility(View.VISIBLE);
//					tv_title.setText(getString(R.string.jrmf_rp_loading));
//					AnimationDrawable drawable = (AnimationDrawable) imageview_progress_spinner.getDrawable();
//					drawable.start();
//				}else {
//					imageview_progress_spinner.setVisibility(View.GONE);
//					if (datas != null && datas.size() > 9){
//						tv_title.setText(getString(R.string.jrmf_rp_to_buttom));
//					}else{
//						tv_title.setText("");
//					}
//				}
//				return holderAddCard.getConvertView();
//			}
//		}
//	}
//
//	public void onScrollStateChanged(AbsListView view, int scrollState) {
////		OnScrollListener.SCROLL_STATE_IDLE 停止滚动
////		OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 正在滚动
////		OnScrollListener.SCROLL_STATE_FLING 惯性滑动
//		isScroll = scrollState != SCROLL_STATE_IDLE;
//	}
//
//	@Override
//	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
//        if ((firstVisibleItem + visibleItemCount) == totalItemCount && isScroll) {
//            View lastVisibleItemView = listView.getChildAt(listView.getChildCount() - 1);
//            if (lastVisibleItemView != null && lastVisibleItemView.getBottom() <= listView.getHeight() && !isLoad) {
////            	ToastUtil.showToast(fromActivity, "滚动到底部");
//            	if (page <= pageCount) {
//					//还有页数
//            		loadDataByIndex();
//				}else{
//					if (buttomModel != null && buttomModel.isMore == true){
//						buttomModel.isMore = false;
//						tradeDetailAdapter.notifyDataSetChanged();
//					}
//				}
//            }
//        }
//    }
//}
//
//
//
//
//
//
//
//
//
