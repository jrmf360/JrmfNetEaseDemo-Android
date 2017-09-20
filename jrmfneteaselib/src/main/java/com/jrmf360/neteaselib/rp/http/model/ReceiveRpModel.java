package com.jrmf360.neteaselib.rp.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

import java.util.List;

/**
 * 接受到的红包model
 * @author honglin
 *
 */
public class ReceiveRpModel extends BaseModel {
	public int maxPage;
	public int receiceLuckCount;//手气最佳
	public int receiveCount;//收到的红包
	public String receiveMoney;//收到的金额
	public String nickName;
	public String avatar;//头像
	public List<RpInfoModel.RpItemModel> receiveHistoryList;
}
