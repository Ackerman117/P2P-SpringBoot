package com.bjpowernode.springboot.service.loan;

import com.bjpowernode.springboot.model.loan.BidInfo;
import com.bjpowernode.springboot.model.vo.BidUserVO;

import java.util.List;
import java.util.Map;

/**
 * @author: gg
 * @create: 2021-08-29 14:30
 **/
public interface BidInfoService {

    Double queryAllBidMoney();

    /**
     * 根据产品id，查询该产品最近的10条投资记录
     * @param paramMap
     * @return
     */
    List<BidInfo> queryRecentlyBidInfoByLoanId(Map<String, Object> paramMap);

    /**
     * 用户投资
     * @param paramMap
     */
    void invest(Map<String, Object> paramMap) throws Exception;

    /**
     * 投资排行榜
     * @return
     */
    List<BidUserVO> queryBidUserTop();
}
