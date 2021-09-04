package com.bjpowernode.springboot.mapper.user;

import com.bjpowernode.springboot.model.user.FinanceAccount;

import java.util.Map;

public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    FinanceAccount selectByUid(Integer uid);

    int updateFinanceAccountMoney(Map<String, Object> paramMap);

    /**
     * 返还收益（更新账户余额）
     * @param paramMap
     * @return
     */
    int updateFinanceAccountByIncomeBack(Map<String, Object> paramMap);

    int updateFinanceAccoutByRecharge(Integer uid, Double rechargeMoney);
}