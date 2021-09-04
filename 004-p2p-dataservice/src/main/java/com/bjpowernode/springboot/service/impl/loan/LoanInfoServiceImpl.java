package com.bjpowernode.springboot.service.impl.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.springboot.constants.Constants;
import com.bjpowernode.springboot.mapper.loan.LoanInfoMapper;
import com.bjpowernode.springboot.model.loan.BidInfo;
import com.bjpowernode.springboot.model.loan.LoanInfo;
import com.bjpowernode.springboot.model.vo.PaginationVO;
import com.bjpowernode.springboot.service.loan.LoanInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @version 1.0
 * @program: P2P-SpringBoot
 * @description:
 * @author: gg
 * @create: 2021-08-28 21:42
 **/
@Component
@Service(interfaceClass = LoanInfoService.class, version = "1.0.0", timeout = 15000)
public class LoanInfoServiceImpl implements LoanInfoService {

    @Resource
    private LoanInfoMapper loanInfoMapper;
    @Resource
    private RedisTemplate<Object,Object> redisTemplate;

    /** @Author gg
     * @Description //TODO 查询历史平均年化收益率
     * @Date 21:47 2021/8/28
     * @Param
     * @return
     **/
    @Override
    public Double queryHistoryAvgRate() {

        // 修改Redis的key的序列化方式（修改过一次即可）
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 1.从Redis中获取数据
        Double historyAvgRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVG_RATE);
        // 2.判断historyAvgRate是否为空
        // 双重验证+同步代码块 解决Redis缓存击穿问题
        if (!ObjectUtils.allNotNull(historyAvgRate)) {

            // 设置同步代码块
            synchronized (this) {
                // 1.1 从Redis中获取数据
                historyAvgRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVG_RATE);
                // 2.1 判断historyAvgRate是否为空
                if (!ObjectUtils.allNotNull(historyAvgRate)) {// 不为空返回true;  为空返回false

                    // 此处Redis中值为空，需要查询MySql
                    // 模拟高并发情况
                    historyAvgRate = loanInfoMapper.selectHistoryAvgRate();
                    // 从MySql查到的数据，存到Redis中仅7天
                    redisTemplate.opsForValue().set(Constants.HISTORY_AVG_RATE,historyAvgRate,7, TimeUnit.DAYS);
                    System.out.println("从MySql中获取历史平均年化收益率");
                } else {
                    System.out.println("从Redis中获取历史平均年化收益率");
                }
            }

        } else {
            System.out.println("从Redis中获取历史平均年化收益率");
        }

        return historyAvgRate;
    }

    @Override
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {

        return loanInfoMapper.selectLoanInfoListByProductType(paramMap);
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoByPage(Map<String, Object> paramMap) {

        // 封装一个PaginationVO（分页）对象
        PaginationVO<LoanInfo> paginationVO = new PaginationVO<>();

        // 查询产品列（productType（可选）、currentPage（当前页）、pageSize（每页显示的条数））
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductType(paramMap);
        paginationVO.setDataList(loanInfoList);

        // （根据产品类型，可选） 查询总条数
        Integer total = loanInfoMapper.selectTotal(paramMap);
        paginationVO.setTotal(total);

        return paginationVO;
    }

    @Override
    public LoanInfo queryLoanInfoById(Integer id) {



        return loanInfoMapper.selectByPrimaryKey(id);
    }


}
