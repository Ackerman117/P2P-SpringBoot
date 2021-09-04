package com.bjpowernode.springboot.mapper.loan;

import com.bjpowernode.springboot.model.loan.BidInfo;
import com.bjpowernode.springboot.model.loan.LoanInfo;

import java.util.List;
import java.util.Map;

public interface LoanInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(LoanInfo record);

    int insertSelective(LoanInfo record);

    LoanInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(LoanInfo record);

    int updateByPrimaryKey(LoanInfo record);

    /** @Author gg
     * @Description //TODO 查询历史平均年化收益率
     * @Date 21:52 2021/8/28
     * @Param 
     * @return 
     **/
    Double selectHistoryAvgRate();


    List<LoanInfo> selectLoanInfoListByProductType(Map<String, Object> paramMap);

    Integer selectTotal(Map<String, Object> paramMap);

    int updateLeftProductMoney(Map<String, Object> paramMap);

    /**
     * 查询出  产品状态为1（已满标，但是没有生成收益计划的）的产品
     * @param productStatus
     * @return
     */
    List<LoanInfo> selectLoanInfoListByProductStatus(int productStatus);
}