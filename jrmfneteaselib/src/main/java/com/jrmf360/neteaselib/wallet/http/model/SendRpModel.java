package com.jrmf360.neteaselib.wallet.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

import java.util.List;

/**
 * 发出去的红包
 * @author honglin
 */
public class SendRpModel extends BaseModel {

	public int maxPage;
	public String sendMoney;
	public String nickName;
	public int sendCount;
	public String avatar;
	public List<SendRpItemModel> sendHistoryList;
	
}
