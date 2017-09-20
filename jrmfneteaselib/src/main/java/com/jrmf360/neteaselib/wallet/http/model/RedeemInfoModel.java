package com.jrmf360.neteaselib.wallet.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

import java.util.List;

/**
 * 提现进入获得信息的model
 * @author honglin
 */
public class RedeemInfoModel extends BaseModel {
	public String balance;
	public String transitamount;//在途资金
	public String redeemDesc;
	public String maxRedeemMoney;
	public List<AccountModel> accountList;
	public int isSetPwd;//0 不需要设置密码， 1 需要设置密码
}




