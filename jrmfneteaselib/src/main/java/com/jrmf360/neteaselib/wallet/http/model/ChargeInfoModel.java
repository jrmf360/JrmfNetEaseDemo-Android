package com.jrmf360.neteaselib.wallet.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

import java.util.List;

/**
 * 充值页面
 * @author honglin
 *
 */
public class ChargeInfoModel extends BaseModel {
	public int isSetPwd;
	public List<AccountModel> accountList;
}
