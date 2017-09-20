package com.jrmf360.neteaselib.rp.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

/**
 * @创建人 honglin
 * @创建时间 17/3/1 下午2:09
 * @类描述 转账详情
 */
public class TransDetailModel extends BaseModel {

	public boolean	isSelf;			// 是否是自己发起的转账

	public String	amount;			// 金额

	public String	username;		// 接受者用户昵称

	public String	sendUserName;	// 发起者用户昵称

	public int		transferType;	// 转账类型 0 等待领取 1 已领取 2 退还 3过期

	public String	transferTime;	// 发起转账的时间

	public String	dealTime;		// 领取时间、过期时间、退回时间

}
