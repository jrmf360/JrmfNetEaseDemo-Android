package com.jrmf360.neteaselib.rp.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

/**
 * Created by Administrator on 2016/3/29.
 */
public class SubmitCardResModel extends BaseModel {

	public String	bankCardNo;

	public String	bankNo;				// 203

	public String	identityNo;			// 203

	public int	isAuthentication;	// 是否绑过卡 0：第一次绑卡； 1：多次绑卡（原来的hasAccount）

	public String	realName;

	public String	bankName;

}
