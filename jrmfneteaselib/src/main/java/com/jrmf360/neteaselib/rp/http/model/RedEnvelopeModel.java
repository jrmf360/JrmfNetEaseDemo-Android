package com.jrmf360.neteaselib.rp.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

import java.util.List;

/**
 * 发送个人红包接口的返回数据 Created by Administrator on 2016/3/1.
 */
public class RedEnvelopeModel extends BaseModel { // Unresolved

	public String				balance;

	public int					getBalance;			// 1 获得余额 0没有获得余额

	public String				envelopeId;

	public List<AccountModel>	myBankcards;

	public int					isSupportAliPay;

	public int					isSupportWeChat;

	public int					isSupportWeChatPay;

	public int					isSupportMulCard;

	public int					hasPwd;				// 是否有交易密码

}
