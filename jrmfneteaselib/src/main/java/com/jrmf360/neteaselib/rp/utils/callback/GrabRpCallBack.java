package com.jrmf360.neteaselib.rp.utils.callback;

import java.io.Serializable;

/**
 * @创建人 honglin
 * @创建时间 16/11/24 下午5:00
 * @类描述 抢红包的回调接口
 */
public interface GrabRpCallBack extends Serializable {

	long serialVersionUID = 2L;
    void grabRpResult(int rpStatus);
}
