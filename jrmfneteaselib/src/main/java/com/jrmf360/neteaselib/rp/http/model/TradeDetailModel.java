package com.jrmf360.neteaselib.rp.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

import java.util.List;

/**
 * 收支明细的model
 * @author honglin
 *
 */
public class TradeDetailModel extends BaseModel {
	public int pageCount;
	public List<TradeItemDetail> details;
}
