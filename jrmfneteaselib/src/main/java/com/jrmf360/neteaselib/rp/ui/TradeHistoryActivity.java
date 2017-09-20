//package com.jrmf360.neteaselib.rp.ui;
///**
// * 交易记录的主页面
// */
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.view.ViewPager;
//import android.support.v4.view.ViewPager.OnPageChangeListener;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.jrmf360.neteaselib.R;
//import com.jrmf360.neteaselib.base.utils.ScreenUtil;
//import com.jrmf360.neteaselib.rp.adapter.TradeDetailAdapter;
//import com.jrmf360.neteaselib.rp.fragment.TradeDetailFragment;
//import com.jrmf360.neteaselib.rp.widget.ActionBarView;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 收支明细页面
// * @author honglin
// */
//public class TradeHistoryActivity extends BaseActivity implements OnPageChangeListener {
//	private List<Fragment> mFragmentList = new ArrayList<>();
//	private TradeDetailAdapter tradeDetailAdapter;
//	private ViewPager viewPager;
//	private TradeDetailFragment allFragment,inFragment,outFragment;
//    /**
//     * 指示线的宽
//     */
//    private int lineWidth;
//	/**
//     * Tab显示内容TextView
//     */
//    private TextView tv_all, tv_in, tv_out;
//
//    private LinearLayout ll_all,ll_in,ll_out;
//    /**
//     * Tab的那个引导线
//     */
//    private ImageView mTabLineIv;
//
//	public static void intent(Activity fromActivity,String userId,String thirdToken){
//		Intent intent = new Intent(fromActivity,TradeHistoryActivity.class);
//		Bundle bundle = new Bundle();
//		bundle.putString("userId",userId);
//		bundle.putString("thirdToken",thirdToken);
//		intent.putExtras(bundle);
//		fromActivity.startActivity(intent);
//	}
//
//
//	@Override
//	public int getLayoutId() {
//		return R.layout.jrmf_rp_activity_trade_history;
//	}
//
//	@Override
//	public void initView() {
//		actionBarView = (ActionBarView) findViewById(R.id.actionbar);
//		tv_all = (TextView) this.findViewById(R.id.tv_all);
//		tv_in = (TextView) this.findViewById(R.id.tv_in);
//		tv_out = (TextView) this.findViewById(R.id.tv_out);
//	    mTabLineIv = (ImageView) this.findViewById(R.id.id_tab_line_iv);
//	    ll_all = (LinearLayout) this.findViewById(R.id.ll_all);
//	    ll_in = (LinearLayout) this.findViewById(R.id.ll_in);
//	    ll_out = (LinearLayout) this.findViewById(R.id.ll_out);
//
//		viewPager = (ViewPager) findViewById(R.id.viewPager);
//	}
//	@Override
//	public void initListener() {
//		actionBarView.getIvBack().setOnClickListener(this);
//		ll_all.setOnClickListener(this);
//		ll_in.setOnClickListener(this);
//		ll_out.setOnClickListener(this);
//		viewPager.setOnPageChangeListener(this);
//	}
//	@Override
//	protected void initData(Bundle bundle) {
//		String userId = bundle.getString("userId");
//		String thirdToken = bundle.getString("thirdToken");
//		allFragment = TradeDetailFragment.newInstance(userId,thirdToken,0);
//		inFragment = TradeDetailFragment.newInstance(userId,thirdToken,1);
//		outFragment = TradeDetailFragment.newInstance(userId,thirdToken,2);
//		mFragmentList.add(allFragment);
//		mFragmentList.add(inFragment);
//		mFragmentList.add(outFragment);
//		tradeDetailAdapter = new TradeDetailAdapter(getSupportFragmentManager(),mFragmentList);
//		viewPager.setAdapter(tradeDetailAdapter);
//		initTabLineWidth();
//		viewPager.setCurrentItem(0);
//	}
//	@Override
//	public void onClick(int id) {
//		if (id == R.id.iv_back) {
//			finish();
//		}else if(id == R.id.ll_all){
//			viewPager.setCurrentItem(0);
//		}else if(id == R.id.ll_in){
//			viewPager.setCurrentItem(1);
//		}else if(id == R.id.ll_out){
//			viewPager.setCurrentItem(2);
//		}
//	}
//
//	/**
//     * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。
//     */
//	@Override
//	public void onPageScrollStateChanged(int state) {
//
//	}
//
//	/**
//     * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比
//     * offsetPixels:当前页面偏移的像素位置
//     */
//	@Override
//	public void onPageScrolled(int position, float offset, int offsetPixels) {
//		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv.getLayoutParams();
//		int translateX = lineWidth * position + offsetPixels / mFragmentList.size();
//		lp.leftMargin = translateX;
////		lp.setMarginStart(translateX);
//		mTabLineIv.setLayoutParams(lp);
//	}
//
//	@Override
//	public void onPageSelected(int position) {
//		resetTextView();
//		if (position == 0) {
//            tv_all.setTextColor(getResources().getColor(R.color.jrmf_rp_title_bar));
//		}else if(position == 1){
//            tv_in.setTextColor(getResources().getColor(R.color.jrmf_rp_title_bar));
//		}else if(position == 2){
//            tv_out.setTextColor(getResources().getColor(R.color.jrmf_rp_title_bar));
//		}
//	}
//
//	/**
//     * 设置滑动条的宽度为屏幕的1/3(根据Tab的个数而定)
//     */
//    private void initTabLineWidth() {
//        int screenWidth = ScreenUtil.getScreenWidth(context);
//        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv.getLayoutParams();
//        lineWidth = lp.width = screenWidth / mFragmentList.size();
//        mTabLineIv.setLayoutParams(lp);
//    }
//
//	/**
//     * 重置颜色
//     */
//    private void resetTextView() {
//        tv_all.setTextColor(getResources().getColor(R.color.color_959595));
//        tv_in.setTextColor(getResources().getColor(R.color.color_959595));
//        tv_out.setTextColor(getResources().getColor(R.color.color_959595));
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
//
