package com.jrmf360.neteaselib.rp.http.model;

/**
 * 交易记录 item的内容
 * 
 * @author honglin
 *
 */
public class TradeItemDetail {

	public String	name;			// 发送红包

	public String	money;

	public String	opType;

	public String	orderNo;

	public int		transferType;

	public String	opTime;

	public String	moneyYuan;

	public String	dateMonth;

	public String	charge;			// 手续费

	public String	tradeName;		// 交易记录右下角文字

	public String	remark;			// 备注

	public String	payTypeDesc;			// 支付方式，如果为空则隐藏item

	public String	bankName;		// 银行名称

	public String	bankCardNo;		// 银行号

}
