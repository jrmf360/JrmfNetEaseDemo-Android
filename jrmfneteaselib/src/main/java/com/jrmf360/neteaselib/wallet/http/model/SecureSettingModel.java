package com.jrmf360.neteaselib.wallet.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

/**
 * 安全设置
 * @author honglin
 */
public class SecureSettingModel extends BaseModel {
//			{"isAuthentication":1,"hasBankCard":0,"isSetPwd":1,"respmsg":"成功","respstat":"0000"}

	public int isSetPwd;//是否设置密码
	public int isAuthentication;//是否实名认证
	public int hasBankCard;//有银行卡
	public int isCompleteInfo;//1 有姓名和身份证号
}
