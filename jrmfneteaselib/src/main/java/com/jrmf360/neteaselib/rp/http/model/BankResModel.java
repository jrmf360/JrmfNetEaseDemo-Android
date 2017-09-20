package com.jrmf360.neteaselib.rp.http.model;

import com.jrmf360.neteaselib.base.model.BaseModel;

import java.util.List;

/**
 * Created by Administrator on 2016/3/2.
 */
public class BankResModel extends BaseModel {

    public List<BankVo> bankList;


    public class BankVo extends BaseModel {
        public String bankName;
        public String bankname;
        public String bankno;
        public String bankpic;
        public String chinapayDaylimit;
        public String chinapayMonthlimit;
        public String chinapayOrderlimit;
        public int id;
        public String liandongDaylimit;
        public String liandongMonthlimit;
        public String liandongOrderlimit;
        public String lianlianDaylimit;
        public String lianlianMonthlimit;
        public String lianlianOrderlimit;
        public String logo_url;
        public String maxInvestLimit;
        public String maxInvestLimitDesc;
        public String payType;
        public String paytype;
        public String todayMaxInvestLimit;
        public String todayMaxInvestLimitDesc;
    }

}
