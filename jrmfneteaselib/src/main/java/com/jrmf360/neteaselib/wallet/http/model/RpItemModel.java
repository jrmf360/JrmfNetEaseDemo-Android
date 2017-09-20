package com.jrmf360.neteaselib.wallet.http.model;
/**
 * 红包的item
 * @author honglin
 */
public class RpItemModel {
	public int  isGroup;//1 群红包 0普通红包
	public int  type;//普通 0 拼手气 1
	public int  isBLuck;//手气最佳 1 ，0不显示
	public String  activateTime;//时间
	public String  moneyYuan;//0.01
	public String	redEnvelop_id;

	
}
