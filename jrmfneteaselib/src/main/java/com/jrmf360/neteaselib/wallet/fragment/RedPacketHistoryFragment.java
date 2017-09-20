package com.jrmf360.neteaselib.wallet.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.adapter.ViewHolder;
import com.jrmf360.neteaselib.base.fragment.BaseFragment;
import com.jrmf360.neteaselib.base.display.DialogDisplay;
import com.jrmf360.neteaselib.base.fragment.LoadingDialogFragment;
import com.jrmf360.neteaselib.base.http.OkHttpModelCallBack;
import com.jrmf360.neteaselib.base.utils.ImageLoadUtil;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.base.utils.ToastUtil;
import com.jrmf360.neteaselib.base.view.CircleImageView;
import com.jrmf360.neteaselib.wallet.http.WalletHttpManager;
import com.jrmf360.neteaselib.wallet.http.model.ButtomModel;
import com.jrmf360.neteaselib.wallet.http.model.ReceiveRpModel;
import com.jrmf360.neteaselib.wallet.http.model.RpItemModel;
import com.jrmf360.neteaselib.wallet.http.model.SendRpItemModel;
import com.jrmf360.neteaselib.wallet.http.model.SendRpModel;
import com.jrmf360.neteaselib.wallet.ui.RpDetailActivity;

import java.util.ArrayList;

/**
 * 红包记录的fragment
 * @author honglin
 *
 */
public class RedPacketHistoryFragment extends BaseFragment implements OnScrollListener {
	
	private ListView listView;
	private boolean isScroll = false;
	private int index = -1;//0 收到的红包，1 发出的红包
	private ArrayList<Object> datas;
	private RedHistoryAdapter redHistoryAdapter;
	private int mListViewHeight,page = 1,pageSize = 10,maxPage;//page从0开始,表示当前的页数
	private ButtomModel buttomModel;
	
	//收到的红包
	private TextView tvInName,tvInMoney,tv_receiveRpNum,tv_receiveBestRpNum;
	private CircleImageView civInheader;
	
	//发出的红包
	private TextView tvOutName,tvOutMoney,tv_sendRpNum;
	private CircleImageView civOutHeader;

	@Override
	public int getLayoutId() {
		return R.layout.jrmf_w_fragment_red_packet_history;
	}
	
	@Override
	public void initView() {
		listView = (ListView) rootView.findViewById(R.id.listView);
	}
	
	@Override
	public void initListener() {
		listView.setOnScrollListener(this);
		listView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi") @Override
            public void onGlobalLayout() {
                mListViewHeight = listView.getHeight();

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                	listView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                	listView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if (datas != null && position > 0){
					//去除第一个条目和最后一个条目
					if (index == 0){
						//收到的红包
						if (datas.get(position - 1) instanceof RpItemModel){
							RpItemModel rpModel  = (RpItemModel) datas.get(position - 1);
							RpDetailActivity.intent(fromActivity,0,rpModel.redEnvelop_id);
						}
					}else{
						//发出的红包
						if (datas.get(position - 1) instanceof  SendRpItemModel){
							SendRpItemModel sendRpItemModel  = (SendRpItemModel) datas.get(position - 1);
							RpDetailActivity.intent(fromActivity,0,sendRpItemModel.id);
						}
					}
				}
			}
		});
	}
	
	@Override
	protected void initData(Bundle bundle) {
		if (bundle != null) {
			index = bundle.getInt("index");
			if (index == 0) {
				View inView = View.inflate(fromActivity, R.layout.jrmf_w_header_receive_rp, null);
				tvInName = (TextView) inView.findViewById(R.id.tv_name);
				tvInMoney = (TextView) inView.findViewById(R.id.tv_money);
				tv_receiveRpNum = (TextView) inView.findViewById(R.id.tv_receiveRpNum);
				tv_receiveBestRpNum = (TextView) inView.findViewById(R.id.tv_receiveBestRpNum);
				civInheader = (CircleImageView) inView.findViewById(R.id.civ_header);
				listView.addHeaderView(inView);
			}else if(index == 1){
				View outView = View.inflate(getActivity(), R.layout.jrmf_w_header_send_rp, null);
				tvOutName = (TextView) outView.findViewById(R.id.tv_name);
				tvOutMoney = (TextView) outView.findViewById(R.id.tv_money);
				tv_sendRpNum = (TextView) outView.findViewById(R.id.tv_sendRpNum);
				civOutHeader = (CircleImageView) outView.findViewById(R.id.civ_header);
				listView.addHeaderView(outView);
			}
			loadHttpData();
		}
		//给布局添加头部，底部必须在设置adapter之前
		listView.setAdapter(redHistoryAdapter = new RedHistoryAdapter());
	}
	
	/**
	 * 从网络加载红包数据
	 */
	private void loadHttpData() {
		
		if (index == 0) {
			//收到的红包
			loadReceiveRp();
		}else{
			loadSendRp();
		}
		DialogDisplay.getInstance().dialogLoading(fromActivity, getString(R.string.jrmf_w_loading), (LoadingDialogFragment.LoadingDialogListener) fromActivity);
	}


	/**
	 * 请求发出的红包
	 */
	private void loadSendRp() {
		WalletHttpManager.loadSendRpInfo(fromActivity, page, pageSize, new OkHttpModelCallBack<SendRpModel>() {

			@Override
			public void onSuccess(SendRpModel sendRpModel) {
				DialogDisplay.getInstance().dialogCloseLoading(fromActivity);
				if (fromActivity.isFinishing()){
					return;
				}
				if (sendRpModel == null) {
					ToastUtil.showToast(fromActivity, getString(R.string.jrmf_w_net_error_l));
					return;
				}
				if (sendRpModel.isSuccess()) {
					if (page == 1){
						if (StringUtil.isNotEmptyAndNull(sendRpModel.nickName)){
							tvOutName.setText(sendRpModel.nickName);
						}
						tvOutMoney.setText(StringUtil.formatMoney(sendRpModel.sendMoney));
						if (StringUtil.isNotEmpty(sendRpModel.avatar)){
							ImageLoadUtil.getInstance().loadImage(civOutHeader,sendRpModel.avatar);
						}
						String num = String.format(getString(R.string.jrmf_w_send_rp_count),sendRpModel.sendCount);
						SpannableString spannableString = new SpannableString(num);
						spannableString.setSpan(new RelativeSizeSpan(1.5f),4,num.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						spannableString.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.jrmf_w_red_dark)),4,num.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						tv_sendRpNum.setText(spannableString);
						maxPage = sendRpModel.maxPage;
					}
					page ++;
					if (sendRpModel.sendHistoryList != null && sendRpModel.sendHistoryList.size() > 0) {
						//有发出红包
						//先添加底部item
						if (buttomModel == null) {
							buttomModel = new ButtomModel();
							if (sendRpModel.sendHistoryList.size() > 9) {
								buttomModel.isMore = true;
							}else{
								buttomModel.isMore = false;
							}
						}
						if (datas == null) {
							datas = new ArrayList<Object>();
						}
						if (!datas.contains(buttomModel)) {
							datas.add(buttomModel);
						}
						datas.addAll(datas.size() - 1,sendRpModel.sendHistoryList);
						redHistoryAdapter.notifyDataSetChanged();
					}else{
						if (buttomModel != null){
							buttomModel.isMore = false;
							redHistoryAdapter.notifyDataSetChanged();
						}
					}
				}else{
					ToastUtil.showToast(fromActivity, sendRpModel.respmsg);
				}
			}
			@Override
			public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(fromActivity);	
				ToastUtil.showToast(fromActivity, getString(R.string.jrmf_w_net_error_l));
			}
		});
	}

	/**
	 * 加载收到的红包
	 */
	private void loadReceiveRp() {
		WalletHttpManager.loadReceiveRpInfo(fromActivity,page,pageSize,new OkHttpModelCallBack<ReceiveRpModel>() {
			@Override
			public void onSuccess(ReceiveRpModel receiveRpModel) {
				DialogDisplay.getInstance().dialogCloseLoading(fromActivity);
				if (receiveRpModel == null) {
					ToastUtil.showToast(fromActivity, getString(R.string.jrmf_w_net_error_l));
					return;
				}
				if (receiveRpModel.isSuccess()) {
					if (page == 1){
						//第一次加载数据
						if (StringUtil.isNotEmptyAndNull(receiveRpModel.nickName)){
							tvInName.setText(receiveRpModel.nickName);
						}
						tvInMoney.setText(StringUtil.formatMoney(receiveRpModel.receiveMoney));
						tv_receiveRpNum.setText(receiveRpModel.receiveCount+"");
						tv_receiveBestRpNum.setText(receiveRpModel.receiceLuckCount+"");
						//头像
						if (StringUtil.isNotEmpty(receiveRpModel.avatar)){
							ImageLoadUtil.getInstance().loadImage(civInheader,receiveRpModel.avatar);
						}
						maxPage = receiveRpModel.maxPage;//最大页数
					}
					page++;
					if (receiveRpModel.receiveHistoryList != null && receiveRpModel.receiveHistoryList.size() > 0) {
						//有收到红包
						//先添加底部item
						if (buttomModel == null) {
							buttomModel = new ButtomModel();
							if (receiveRpModel.receiveHistoryList.size() > 9) {
								buttomModel.isMore = true;
							}else{
								buttomModel.isMore = false;
							}
						}
						if (datas == null) {
							datas = new ArrayList<Object>();
						}
						if (!datas.contains(buttomModel)) {
							datas.add(buttomModel);
						}
						datas.addAll(datas.size() - 1,receiveRpModel.receiveHistoryList);
						redHistoryAdapter.notifyDataSetChanged();
					}else{
						if (buttomModel != null){
							buttomModel.isMore = false;
							redHistoryAdapter.notifyDataSetChanged();
						}
					}
				}else {
					ToastUtil.showToast(fromActivity, receiveRpModel.respmsg);
				}
			}

			@Override
			public void onFail(String result) {
				DialogDisplay.getInstance().dialogCloseLoading(fromActivity);	
				ToastUtil.showToast(fromActivity, getString(R.string.jrmf_w_net_error_l));
			}
		});
	}



//-----------------------------------adapter-------------------------------------------------------------------------------------
	class RedHistoryAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return datas == null ? 0 : datas.size();
		}

		@Override
		public Object getItem(int position) {
			return datas == null ? null : datas.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public int getItemViewType(int position) {
			if (position == datas.size() - 1){
				return 1;
			}else{
				return 0;
			}
		}
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			if (type == 0) {
				//交易记录
				ViewHolder holderCardList = ViewHolder.get(getActivity(), convertView, parent, R.layout.jrmf_w_item_rp_history, position);
				TextView tv_status = holderCardList.getView(R.id.tv_status);
				TextView tv_rp_type = holderCardList.getView(R.id.tv_rp_type);
				TextView tv_rp_time = holderCardList.getView(R.id.tv_rp_time);
				TextView tv_mount = holderCardList.getView(R.id.tv_mount);
				
				if (index == 0) {
					//收到的红包
					RpItemModel rpModel  = (RpItemModel) datas.get(position);
					//是否是拼手气红包
					if (rpModel.type == 1) {
						//拼手气-显示图标
						tv_rp_type.setText(getString(R.string.jrmf_w_luck_rp));
						setRightDrawable(tv_rp_type, true);
					}else{
						//普通红包
						tv_rp_type.setText(getString(R.string.jrmf_w_normal_rp));
						setRightDrawable(tv_rp_type, false);
					}
					//时间
					tv_rp_time.setText(rpModel.activateTime);
					//金额
					tv_mount.setText(String.format(getString(R.string.jrmf_w_some_yuan),rpModel.moneyYuan));
					//手气最佳
					if (rpModel.isBLuck == 1) {
						//手气最佳
						tv_status.setVisibility(View.VISIBLE);
					}else{
						//不显示
						tv_status.setVisibility(View.INVISIBLE);
					}
				}else{
					//发出的红包
					SendRpItemModel sendRpItemModel  = (SendRpItemModel) datas.get(position);
					//是否是拼手气红包
					if (sendRpItemModel.type == 1) {
						//拼手气-显示图标
						tv_rp_type.setText(getString(R.string.jrmf_w_luck_rp));
						setRightDrawable(tv_rp_type, true);
					}else{
						//普通红包
						tv_rp_type.setText(getString(R.string.jrmf_w_normal_rp));
						setRightDrawable(tv_rp_type, false);
					}
					//时间
					tv_rp_time.setText(sendRpItemModel.payTime);
					//金额
					tv_mount.setText(String.format(getString(R.string.jrmf_w_some_yuan),sendRpItemModel.moneyYuan));
					//手气最佳
					tv_status.setTextColor(getResources().getColor(R.color.color_a0a0a0));
					setLeftDrawable(tv_status, false);
					if (sendRpItemModel.isEffect == 1){
						if (StringUtil.formatMoneyDouble(sendRpItemModel.receiveNum) == StringUtil.formatMoneyDouble(sendRpItemModel.num)){
							tv_status.setText(String.format(getString(R.string.jrmf_w_rp_receive_rate),sendRpItemModel.receiveNum,sendRpItemModel.num));
						}else{
							tv_status.setText(String.format(getString(R.string.jrmf_w_rp_receive_rate2),sendRpItemModel.receiveNum,sendRpItemModel.num));
						}
					}else{
						//已过期
						tv_status.setText(getString(R.string.jrmf_w_timeout));
					}
				}
				return holderCardList.getConvertView();
			}else{
				//底部加载更多
				ViewHolder holderAddCard = ViewHolder.get(getActivity(), convertView, parent, R.layout.jrmf_w_item_list_buttom, position);
				ButtomModel buttomModel = (ButtomModel) datas.get(position);
				ImageView imageview_progress_spinner = holderAddCard.getView(R.id.imageview_progress_spinner);
				TextView tv_title = holderAddCard.getView(R.id.tv_title);
				if (buttomModel.isMore) {
					imageview_progress_spinner.setVisibility(View.VISIBLE);
					tv_title.setText(getString(R.string.jrmf_w_loading));
					AnimationDrawable drawable = (AnimationDrawable) imageview_progress_spinner.getDrawable();
					drawable.start();
				}else {
					imageview_progress_spinner.setVisibility(View.GONE);
					if (datas != null && datas.size() > 9){
						tv_title.setText(getString(R.string.jrmf_w_to_buttom));
					}else{
						tv_title.setText("");
					}
				}
				return holderAddCard.getConvertView();
			}
		}
	}
	
	/**
	 * 设置右边的drawable
	 * @param textView
	 */
	private void setRightDrawable(TextView textView,boolean isShow){
		Drawable drawable= getResources().getDrawable(R.drawable.jrmf_w_ic_pin);
		/// 这一步必须要做,否则不会显示.  
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
		if (isShow) {
			textView.setCompoundDrawables(null,null,drawable,null);  
		}else{
			//隐藏Drawables  
			textView.setCompoundDrawables(null,null,null,null); 
		}
	}
	
	/**
	 * 设置左边的drawable
	 * @param textView
	 */
	private void setLeftDrawable(TextView textView,boolean isShow){
		Drawable drawable= getResources().getDrawable(R.drawable.jrmf_w_crown);
		/// 这一步必须要做,否则不会显示.  
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());  
		if (isShow) {
			textView.setCompoundDrawables(drawable,null,null,null);  
		}else{
			//隐藏Drawables  
			textView.setCompoundDrawables(null,null,null,null); 
		}
	}
	
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
//		OnScrollListener.SCROLL_STATE_IDLE 停止滚动
//		OnScrollListener.SCROLL_STATE_TOUCH_SCROLL 正在滚动
//		OnScrollListener.SCROLL_STATE_FLING 惯性滑动
		if (scrollState == SCROLL_STATE_IDLE) {
			isScroll = false;
		}else {
			isScroll = true;
		}
	}
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
        if ((firstVisibleItem + visibleItemCount) == totalItemCount && isScroll) {
            View lastVisibleItemView = listView.getChildAt(listView.getChildCount() - 1);
            if (lastVisibleItemView != null && lastVisibleItemView.getBottom() == mListViewHeight) {
            	//滚动到底部
            	if (page <= maxPage) {
					//加载数据 
            		if (index == 0) {
						//收到的红包
            			loadReceiveRp();
					}else{
						//发出的红包
						loadSendRp();
					}
				}else{
					if (buttomModel != null && buttomModel.isMore == true){
						buttomModel.isMore = false;
						redHistoryAdapter.notifyDataSetChanged();
					}
				}
            }
        }
    }
}












