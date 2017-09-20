package com.jrmf360.neteaselib.wallet.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

/**
 * 银行卡信息
 * @author honglin
 *
 */
public class BankInfoModel extends BaseModel {
	public String bankNo;//银行编码 305
	public String bankCardNo;//银行卡号
	public String bankName;//银行名字
	public int isAuthentication;//是否实名
	public String realName;//真实姓名
	public String identityNo;//身份证号
}
