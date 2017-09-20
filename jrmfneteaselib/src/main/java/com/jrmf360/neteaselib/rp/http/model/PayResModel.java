package com.jrmf360.neteaselib.rp.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

/**
 * Created by Administrator on 2016/3/28.
 */
public class PayResModel extends BaseModel {

    public String limitMoney;
    public String envelopeId;
    public String url;

    public String token_id;//微信支付
    public String paramStr;//支付包支付
}
