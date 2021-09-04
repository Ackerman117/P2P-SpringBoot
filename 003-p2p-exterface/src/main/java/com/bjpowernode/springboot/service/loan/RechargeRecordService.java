package com.bjpowernode.springboot.service.loan;

import com.bjpowernode.springboot.model.loan.RechargeRecord;

/**
 * @author: gg
 * @create: 2021-09-03 22:35
 */
public interface RechargeRecordService {
    /**
     * 生成订单
     * @param rechargeRecord
     * @return
     */

    int addRecharge(RechargeRecord rechargeRecord);

    /**
     * 处理快钱异步通知
     * @param orderId 商家订单号
     * @param payResult 快钱给的支付结果
     * @return int 处理结果
     */
    int doWithKQNotify(String orderId, Integer payResult);

}
