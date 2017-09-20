package com.jrmf360.neteaselib.rp.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

import java.util.List;

/**
 * @创建人 honglin
 * @创建时间 17/3/1 上午10:07
 * @类描述 用户点击转账获得的信息
 */
public class TransModel extends BaseModel {

	public String				balance;

	public boolean				isWarn;				// 是否具有相同的交易 1 是 0否

	public String				isVailPwd;			// 有没有交易密码

	public String				transferOrderNo;	// 交易单号

	public List<AccountModel>	myBankcards;

	public String				bSetPwd;			// 是否需要设置交易密码

	public String				isSupportAliPay;

	// public String isSupportWeChat;

	public String				isSupportWeChatPay;

	public String				isSupportMulCard;

	public int					isAuthentication;	// 是否真实名

	public int					isComplete;			// 是否有身份信息

	public int					hasPwd;				// 是否有交易密码

	public String				realName;

	public int					count;				//有几笔相同的转账

}
