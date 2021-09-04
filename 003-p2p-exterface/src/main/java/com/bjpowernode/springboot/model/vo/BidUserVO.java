package com.bjpowernode.springboot.model.vo;

import java.io.Serializable;

/**
 * 投资排行榜
 * @author: gg
 * @create: 2021-09-02 20:45
 */
public class BidUserVO implements Serializable {

    private String phone;
    private Double bidMoney;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getBidMoney() {
        return bidMoney;
    }

    public void setBidMoney(Double bidMoney) {
        this.bidMoney = bidMoney;
    }
}
