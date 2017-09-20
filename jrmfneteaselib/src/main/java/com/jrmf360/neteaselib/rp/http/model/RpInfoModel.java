package com.jrmf360.neteaselib.rp.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * @创建人 honglin
 * @创建时间 16/11/18 下午4:45
 * @类描述 抢红包- 获得红包信息
 */
public class RpInfoModel extends BaseModel {

	public int					pageCount;		// 下一页时使用

	public String				content;

	public String				username;

	// 0 正常未被领取状态 1红包已经被领取，2红包失效不能领取 3红包未失效单已经被领完4:普通红包并且用户点击自己红包
	public int					envelopeStatus;

	public String				avatar;

	public int					hasLeft;

	public int					receTotal;		// 抢红包的个数

	public int					total;

	public int					type;			// 1 拼手气红包,0普通红包

	public int					isGroup;		// 1 群红包

	public int					isSelf;			// 1 自己的红包

	public String				totalMoney;		// 总共的钱

	public String				recTotalMoney;	// 领取的钱

	public String				grabMoney;		// 抢到的钱

	public String				grabTimes;		// 抢红包用到的时间

	public List<RpItemModel>	receiveHistory;

	public class RpItemModel implements Serializable {

		public String	userid;

		public String	nickname;

		public String	redEnvelop_id;

		public String	money;

		public int		type;

		public int		isBLuck;

		public String	activateTime;

		public String	moneyYuan;

	}

}
