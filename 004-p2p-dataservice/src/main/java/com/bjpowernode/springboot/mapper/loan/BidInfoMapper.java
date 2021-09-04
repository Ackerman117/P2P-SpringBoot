package com.bjpowernode.springboot.mapper.loan;

import com.bjpowernode.springboot.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    Double queryAllBidMoney();

    List<BidInfo> selectRecentlyBidInfoByLoanId(Map<String, Object> paramMap);

    /**
     * 根据loanId 查询出 投资列表
     * @param id
     * @return
     */
    List<BidInfo> selectBidInfoListByLoanId(Integer loanInfoId);
}