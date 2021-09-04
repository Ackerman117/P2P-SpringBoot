package com.bjpowernode.springboot.timer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.springboot.service.loan.IncomeRecordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author: gg
 * @create: 2021-09-02 21:56
 */
@Component
public class TimerManager {

    @Reference(interfaceClass = IncomeRecordService.class, version = "1.0.0", check = false, timeout = 3000)
    private IncomeRecordService incomeRecordService;
/*
    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomePlan() throws Exception {
        System.out.println("==生成收益计划开始==");
        incomeRecordService.genrateIncomePlan();
        System.out.println("==生成收益计划结束==");
    }*/

    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomeBack() throws Exception {
        System.out.println("==返回收益计划开始==");
        incomeRecordService.generateIncomeBack();
        System.out.println("==返回收益计划结束==");

    }

}
