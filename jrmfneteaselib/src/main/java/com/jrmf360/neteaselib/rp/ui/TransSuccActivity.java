package com.jrmf360.neteaselib.rp.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jrmf360.neteaselib.R;
import com.jrmf360.neteaselib.base.manager.CusActivityManager;
import com.jrmf360.neteaselib.base.utils.StringUtil;
import com.jrmf360.neteaselib.rp.widget.ActionBarView;

/**
 * @创建人 honglin
 * @创建时间 17/2/21 下午4:51
 * @类描述 转账成功页面
 */
public class TransSuccActivity extends TransBaseActivity {

    private TextView tv_username, tv_trans_money;

    private Button btn_trans_finish;

    public static void intent(Activity fromActivity,String receiptName,String transMoney){
        Intent intent = new Intent(fromActivity,TransSuccActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("receiptName",receiptName);
        bundle.putString("transMoney",transMoney);
        intent.putExtras(bundle);
        fromActivity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.jrmf_rp_activity_trans_succ;
    }

    @Override
    public void initView() {
        actionBarView = (ActionBarView) findViewById(R.id.actionbar);
        actionBarView.getIvBack().setVisibility(View.GONE);
        tv_username = (TextView) findViewById(R.id.tv_username);
        tv_trans_money = (TextView) findViewById(R.id.tv_trans_money);
        btn_trans_finish = (Button) findViewById(R.id.btn_trans_finish);
    }

    @Override
    public void initListener() {
        btn_trans_finish.setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle bundle) {
        if (bundle != null){
            String receiptName = bundle.getString("receiptName");
            String transMoney = bundle.getString("transMoney");
            if (receiptName.length() > 10){
                receiptName = receiptName.substring(0,10) + "...";
            }
            tv_username.setText(String.format(getString(R.string.jrmf_rp_trans_to_who),receiptName));
            tv_trans_money.setText(StringUtil.formatMoney(transMoney));
        }
    }

    @Override
    public void onClick(int id) {
        if (id == R.id.btn_trans_finish) {
            //点击完成-发送消息
//            TransPayActivity transPayActivity = CusActivityManager.getInstance().findActivity(TransPayActivity.class);
//            if (transPayActivity != null){
//                transPayActivity.finishWithResult();
//            }
            TransAccountActivity transAccountActivity = CusActivityManager.getInstance().findActivity(TransAccountActivity.class);
            if (transAccountActivity != null){
                transAccountActivity.finishWithResult();
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //屏蔽返回键
    }
}
