package com.bjpowernode.springboot.service.loan;

/**
 * @author: gg
 * @create: 2021-09-02 22:07
 */
public interface IncomeRecordService {
    void genrateIncomePlan() throws Exception;

    void generateIncomeBack() throws Exception;
}
