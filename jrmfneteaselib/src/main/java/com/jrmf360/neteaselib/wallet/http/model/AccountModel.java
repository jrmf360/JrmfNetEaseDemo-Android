package com.jrmf360.neteaselib.wallet.http.model;
/**
 * 银行卡账户信息
 * @author honglin
 *
 */
public class AccountModel{
	public String bankCardNo;// 银行卡号
    public String bankCardNoDesc;// 银行卡后四位
    public String payOrderlimit;//限额
    public String bankName;// 民生银行
    public int isRedeemCard;//是否是可提现银行卡
    public String logo_url;
    public String color;//背景色



    public String bankNo;// 305 银行编码
    public String cardHolder;// 李彦宏
    public String createTime;
    public String identityNo;// 身份证号
    public String isCloud; // 0，1
    public String isValiMobile;// 0，1
    public String mobileNo;// 手机号
    public String mobileToken;
    public String partnerId;
    public String subPartnerId;
    public String userId;
}
