package com.jrmf360.neteaselib.wallet.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

/**
 * 账户信息
 * 
 * @author honglin
 */
public class AccountInfoModel extends BaseModel {

	public String	realName;

	public String	identityNo;

	public int		isAuthentication;

	public String	mobileNo;			// 有的话，就显示手机号，没有不显示手机号

	public int		isSetPwd;			// 1 需要设置密码 0 已经设置过密码
}
