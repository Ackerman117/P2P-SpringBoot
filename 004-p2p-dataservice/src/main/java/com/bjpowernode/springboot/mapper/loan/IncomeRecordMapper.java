package com.bjpowernode.springboot.mapper.loan;

import com.bjpowernode.springboot.model.loan.IncomeRecord;

import java.util.List;

public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);

    /**
     * 查询出待返还收益的记录（记录）
     * @param incomeStatus
     * @return
     */
    List<IncomeRecord> selectIncomeRecordListByIncomeStatusAndCurDate(int incomeStatus);
}