package com.bjpowernode.springboot.service.impl.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.springboot.constants.Constants;
import com.bjpowernode.springboot.mapper.loan.BidInfoMapper;
import com.bjpowernode.springboot.mapper.loan.LoanInfoMapper;
import com.bjpowernode.springboot.mapper.user.FinanceAccountMapper;
import com.bjpowernode.springboot.model.loan.BidInfo;
import com.bjpowernode.springboot.model.loan.LoanInfo;
import com.bjpowernode.springboot.model.vo.BidUserVO;
import com.bjpowernode.springboot.service.loan.BidInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @description:
 * @author: gg
 * @create: 2021-08-29 14:31
 **/
@Component
@Service(interfaceClass = BidInfoService.class, version = "1.0.0", timeout = 1500)
public class BidInfoServiceImpl implements BidInfoService {


    @Resource
    private BidInfoMapper bidInfoMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private FinanceAccountMapper financeAccountMapper;
    @Resource
    private LoanInfoMapper loanInfoMapper;


    @Override
    public Double queryAllBidMoney() {
        // 从Redis中查询
        Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);
        if (!ObjectUtils.allNotNull(allBidMoney)) {

            synchronized (this) {
                allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BID_MONEY);
                if (!ObjectUtils.allNotNull(allBidMoney)) {

                    allBidMoney = bidInfoMapper.queryAllBidMoney();
                    redisTemplate.opsForValue().set(Constants.ALL_BID_MONEY,allBidMoney);
                    System.out.println("从MySql中查询平台累计成交金额");
                } else {
                    System.out.println("从Redis中查询平台累计成交金额");
                }
            }

        } else {
            System.out.println("从Redis中查询平台累计成交金额");
        }

        return allBidMoney;
    }

    @Override
    public List<BidInfo> queryRecentlyBidInfoByLoanId(Map<String, Object> paramMap) {


        return bidInfoMapper.selectRecentlyBidInfoByLoanId(paramMap);
    }

    @Transactional
    @Override
    public void invest(Map<String, Object> paramMap) throws Exception {

        // 取值
        Integer uid = (Integer) paramMap.get("uid");
        Integer loanId = (Integer) paramMap.get("loanId");
        Double bidMoney = (Double) paramMap.get("bidMoney");

        // 新增投资记录
        BidInfo bidInfo = new BidInfo();
        bidInfo.setUid(uid);
        bidInfo.setLoanId(loanId);
        bidInfo.setBidMoney(bidMoney);
        bidInfo.setBidTime(new Date());
        bidInfo.setBidStatus(1);
        // 执行新增投资记录操作
        int insertSelectiveCount = bidInfoMapper.insertSelective(bidInfo);
        if (insertSelectiveCount <= 0) {
            throw new Exception("更新投资记录失败");
        }

        // 更新账户可用余额
        int updateFinanAccountCount = financeAccountMapper.updateFinanceAccountMoney(paramMap);
        if (updateFinanAccountCount <= 0) {
            throw new Exception("更新账户可用余额失败");
        }

        // 解决超卖问题（多线程  乐观锁）
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        Integer version = loanInfo.getVersion();
        paramMap.put("version",version);
        // 更新产品可投金额
        int updateLeftProductMoneyCount = loanInfoMapper.updateLeftProductMoney(paramMap);
        if (updateLeftProductMoneyCount <= 0) {
            throw new Exception("更新产品可投金额失败");
        }


        // 查询产品信息，可投金额为0时，将产品状态从0改为1（1为满标）
        LoanInfo loanInfoDetail = loanInfoMapper.selectByPrimaryKey(loanId);
        if (0 == loanInfoDetail.getLeftProductMoney()) {
            loanInfoDetail.setId(loanId);
            loanInfoDetail.setProductStatus(1); // 更新产品状态
            loanInfoDetail.setProductFullTime(new Date());// 更新满标时间
            int rows = loanInfoMapper.updateByPrimaryKeySelective(loanInfoDetail);

            if (rows <= 0) {
                throw new Exception("更新产品信息失败");
            }
        }

        //将用户投资的信息存放到redis缓存中
        String phone = (String) paramMap.get("phone");
        redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP,phone,bidMoney);
    }

    @Override
    public List<BidUserVO> queryBidUserTop() {

        List<BidUserVO> bidUserVOList = new ArrayList<>();

        //从redis中获取投资排行榜
        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 5);

        //获取集合的迭代器
        Iterator<ZSetOperations.TypedTuple<Object>> iterator = set.iterator();

        //循环遍历
        while (iterator.hasNext()) {

            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String phone = (String) next.getValue();// 手机号
            Double bidMoney = next.getScore();// 金额

            BidUserVO bidUserVO = new BidUserVO();
            bidUserVO.setPhone(phone);
            bidUserVO.setBidMoney(bidMoney);

            bidUserVOList.add(bidUserVO);
        }


        return bidUserVOList;
    }

}
