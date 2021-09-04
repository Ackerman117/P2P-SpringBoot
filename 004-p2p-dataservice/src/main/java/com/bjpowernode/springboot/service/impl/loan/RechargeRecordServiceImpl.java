package com.bjpowernode.springboot.service.impl.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.springboot.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.springboot.mapper.user.FinanceAccountMapper;
import com.bjpowernode.springboot.model.loan.RechargeRecord;
import com.bjpowernode.springboot.service.loan.RechargeRecordService;
import com.bjpowernode.springboot.service.user.RedisService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: gg
 * @create: 2021-09-03 22:37
 */
@Component
@Service(interfaceClass = RechargeRecordService.class, version = "1.0.0", timeout = 15000)
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Resource
    private RechargeRecordMapper rechargeRecordMapper;
    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public int addRecharge(RechargeRecord rechargeRecord) {

        return rechargeRecordMapper.insertSelective(rechargeRecord);
    }

    /**
     * 处理快钱异步通知
     *
     * @param orderId   商家订单号
     * @param payResult 快钱给的支付结果
     * @return
     */
    @Override
    public int doWithKQNotify(String orderId, Integer payResult) {
        if (ObjectUtils.allNotNull(orderId,payResult)) {

            //1. 查询订单号是否商家订单
            RechargeRecord rechargeRecord = rechargeRecordMapper.selectRechargeByOrderId(orderId);
            if (ObjectUtils.allNotNull(rechargeRecord)) {

                //2. 判断订单是否处理过，没有处理过的才能处理
                if (rechargeRecord.getRechargeStatus().equals("0")) {

                    //3. 更新账户余额
                    financeAccountMapper.updateFinanceAccoutByRecharge(rechargeRecord.getUid(),rechargeRecord.getRechargeMoney());

                    //4. 修改充值表中的充值状态
                    rechargeRecord.setRechargeStatus("1");
                    rechargeRecordMapper.updateByPrimaryKeySelective(rechargeRecord);
                }
            }
        }
        return 1;
    }
}
