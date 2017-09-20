package com.jrmf360.neteaselib.wallet.http.model;
/**
 * 发出的红包的item model
 * @author honglin
 *
 */
public class SendRpItemModel {
	public String num;
	public int type;
	public String id;
	public String payTime;
	public String receiveNum;
	public String moneyYuan;

	public int isEffect;// 1不过期
}
