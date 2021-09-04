package com.bjpowernode.springboot.service.loan;

import com.bjpowernode.springboot.model.loan.BidInfo;
import com.bjpowernode.springboot.model.loan.LoanInfo;
import com.bjpowernode.springboot.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;

/**
 * @program: P2P-SpringBoot
 * @author: gg
 * @create: 2021-08-28 21:37
 **/
public interface LoanInfoService {

    /**
     * @author gg
     * @description
     * @date 16:21 2021/8/29
     * @param
     * @return 111
     */
    Double queryHistoryAvgRate();

    /**
     * 根据产品类型查询产品列表
     * @param paramMap Map product currentPage pageSize
     * @return 产品列表
     */
    List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

    /**
     * 分页查询产品列表
     * @param paramMap
     * @return
     */
    PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap);

    /**
     * 根据id查询产品详情
     * @return
     */
    LoanInfo queryLoanInfoById(Integer id);

}
