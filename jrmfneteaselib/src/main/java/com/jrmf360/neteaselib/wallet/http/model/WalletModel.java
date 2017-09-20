package com.jrmf360.neteaselib.wallet.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

/**
 * 我的钱包
 * 
 * @author honglin
 *
 */
public class WalletModel extends BaseModel {

	public String	balance;

	// public int isSetPwd;//1 需要设置 0不需要设置
	public int		isSupportRedEnvelope;	// 是否支持红包 1 支持 0不支持

	public int		userCardCount;			// 银行卡数量

	public int		isHasPwd;				// 1 有密码 0没有密码

	public int		isAuthentication;		// 认证 0 未认证 1 已认证
}
