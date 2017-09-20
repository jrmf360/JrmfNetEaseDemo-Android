package com.jrmf360.neteaselib.rp.bean;

/**
 * @创建人 honglin
 * @创建时间 17/3/10 上午10:12
 * @类描述 转账信息
 */
public class TransAccountBean {

	private String	transferOrder;	// 转账单号

	private String	transferDesc;	// 转账描述

	// private String receiveUid; // 接收者id

	private String	transferAmount;	// 转账金额;

	private String		transferStatus;	// 0：发起转账 1：已收款 2：退还

	public String getTransferOrder() {
		return transferOrder;
	}

	public void setTransferOrder(String transferOrder) {
		this.transferOrder = transferOrder;
	}

	public String getTransferDesc() {
		return transferDesc;
	}

	public void setTransferDesc(String transferDesc) {
		this.transferDesc = transferDesc;
	}

	public String getTransferAmount() {
		return transferAmount;
	}

	public void setTransferAmount(String transferAmount) {
		this.transferAmount = transferAmount;
	}

	public String getTransferStatus() {
		return transferStatus;
	}

	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}
}
