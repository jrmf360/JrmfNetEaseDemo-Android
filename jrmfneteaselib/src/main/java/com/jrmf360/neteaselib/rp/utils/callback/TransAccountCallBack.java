package com.jrmf360.neteaselib.rp.utils.callback;

import com.jrmf360.neteaselib.rp.bean.TransAccountBean;

import java.io.Serializable;

/**
 * @创建人 honglin
 * @创建时间 17/3/10 上午10:46
 * @类描述 转账发起退款和确认收款的回调
 */
public interface TransAccountCallBack extends Serializable {

	long serialVersionUID = 3L;
	void transResult(TransAccountBean transAccountBean);

}
