package com.jrmf360.neteaselib.wallet.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

import java.util.List;

/**
 * @创建人 honglin
 * @创建时间 16/11/15 下午7:03
 * @类描述 支持的银行卡
 */
public class SupportBankModel extends BaseModel {
    public List<AccountModel> bankList;
}
