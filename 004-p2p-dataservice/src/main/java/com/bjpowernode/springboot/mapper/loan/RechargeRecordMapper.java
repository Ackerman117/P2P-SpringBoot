package com.bjpowernode.springboot.mapper.loan;

import com.bjpowernode.springboot.model.loan.RechargeRecord;

public interface RechargeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RechargeRecord record);

    int insertSelective(RechargeRecord record);

    RechargeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RechargeRecord record);

    int updateByPrimaryKey(RechargeRecord record);

    /**
     * 根据订单号查询订单信息
     * @param orderId
     * @return
     */
    RechargeRecord selectRechargeByOrderId(String orderId);
}