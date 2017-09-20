package com.jrmf360.neteaselib.rp.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

/**
 * @创建人 honglin
 * @创建时间 16/11/28 下午3:59
 * @类描述 进入发红包页面获得发红包的信息
 */
public class InitRpInfo extends BaseModel {

    public String summary;
    public String maxLimitMoney;
    public String envelopeName;
    public int maxCount;
    public int isAuthentication; //0未实名；1已实名

}
