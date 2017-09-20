package com.jrmf360.neteaselib.base.model;

import com.jrmf360.neteaselib.base.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/1.
 */
public class BaseModel implements Serializable {
    private static final long serialVersionUID = 2l;
    public String respstat;
    public String respmsg;

    /**
     * 判断请求是否成功
     * @return
     */
    public boolean isSuccess(){
        if (StringUtil.isNotEmpty(respstat) && "0000".equals(respstat)) {
            return true;
        }else {
            return false;
        }
    }
}

