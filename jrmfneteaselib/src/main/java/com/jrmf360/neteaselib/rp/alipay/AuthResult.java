package com.jrmf360.neteaselib.rp.alipay;

import java.util.Map;

/**
 * @创建人 honglin
 * @创建时间 2017/4/10 上午10:28
 * @类描述 支付宝授权回调
 */
public class AuthResult {
    private String resultStatus;
    private String result;
    private String memo;


    public AuthResult(Map<String, String> resultMap) {
        this.resultStatus = resultMap.get("resultStatus");
        this.result = resultMap.get("result");
        this.memo = resultMap.get("memo");
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public void setResultStatus(String resultStatus) {
        this.resultStatus = resultStatus;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
