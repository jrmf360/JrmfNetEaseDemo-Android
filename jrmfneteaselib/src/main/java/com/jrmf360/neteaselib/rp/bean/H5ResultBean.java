package com.jrmf360.neteaselib.rp.bean;

/**
 * @创建人 honglin
 * @创建时间 17/2/12 下午2:40
 * @类描述 一句话描述 你的UI
 */
public class H5ResultBean {
	// http://192.168.30.120:8081/mfkj-api/uccb/result.shtml?transCode=***&resultCode=*****&respMsg=****
	/**
	 * 客户交易密码设置 U00003 客户交易密码修改 U00004 客户交易密码重置 U00005 免密协议签订 U00009 发红包 T00004
	 */
	public String	transCode;

	public String	resultCode;	// SUCCESS 成功 FAILURE 失败

	public String	respMsg;	// 响应信息

}
