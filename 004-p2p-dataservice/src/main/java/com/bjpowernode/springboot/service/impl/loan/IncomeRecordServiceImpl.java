package com.bjpowernode.springboot.service.impl.loan;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.springboot.constants.Constants;
import com.bjpowernode.springboot.mapper.loan.BidInfoMapper;
import com.bjpowernode.springboot.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.springboot.mapper.loan.LoanInfoMapper;
import com.bjpowernode.springboot.mapper.user.FinanceAccountMapper;
import com.bjpowernode.springboot.model.loan.BidInfo;
import com.bjpowernode.springboot.model.loan.IncomeRecord;
import com.bjpowernode.springboot.model.loan.LoanInfo;
import com.bjpowernode.springboot.service.loan.IncomeRecordService;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: gg
 * @create: 2021-09-02 22:09
 */
@Component
@Service(interfaceClass = IncomeRecordService.class, version = "1.0.0", timeout = 3000)
public class IncomeRecordServiceImpl implements IncomeRecordService {


    @Resource
    private IncomeRecordMapper incomeRecordMapper;
    @Resource
    private LoanInfoMapper loanInfoMapper;
    @Resource
    private BidInfoMapper bidInfoMapper;
    @Resource
    private FinanceAccountMapper financeAccountMapper;


    @Override
    public void genrateIncomePlan() throws Exception {

        // 目标：生成收益计划数据
            /*IncomeRecord incomeRecord = new IncomeRecord();
            incomeRecord.setBidId();*/

        // 1.查询投资产品信息（条件 productStatus==1）==> List<IncomeRecord>
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductStatus(1);
        // 2.遍历已经满标的产品（productStatus==1） 得到每一条满标产品
        for (LoanInfo loanInfo : loanInfoList) {

            // 3.根据每一条满标产品  得到投资记录
            List<BidInfo> bidInfoList = bidInfoMapper.selectBidInfoListByLoanId(loanInfo.getId());
            // 4.遍历投资记录
            for (BidInfo bidInfo : bidInfoList) {

                // 5.根据每一条投资记录信息（和产品信息）  生成收益计划数据
                IncomeRecord incomeRecord = new IncomeRecord();
                incomeRecord.setUid(bidInfo.getUid());
                incomeRecord.setLoanId(loanInfo.getId());
                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setIncomeStatus(0);// 0：未返还   1：已返还

                // 收益时间(Date) = 产品满标时间(Date) + 产品周期(int 天|月)
                Date incomeDate = null;
                //收益金额 = 投资金额 * 日利率 * 天数;
                Double incomeMoney = null;

                //判断当前产品的类型
                if (Constants.PRODUCT_TYPE_X.equals(loanInfo.getProductType())) {
                    //新手宝
                    incomeDate = DateUtils.addDays(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle();

                } else {
                    //优选或散标
                    incomeDate = DateUtils.addMonths(loanInfo.getProductFullTime(),loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle()*30;

                }

                incomeMoney = Math.round(incomeMoney*Math.pow(10,2))/Math.pow(10,2);

                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);

                // 收益金额
                incomeRecordMapper.insertSelective(incomeRecord);
            }


            // 2. 更新当前产品的状态为2，满标且生成收益计划
            loanInfo.setProductStatus(2);
            int rows = loanInfoMapper.updateByPrimaryKeySelective(loanInfo);
            if (rows <= 0) {
                throw new Exception("当前产品标识为:"+loanInfo.getId()+"的状态从1更新成2失败");
            }
        }


    }


    /**
     * 返还收益
     */
    @Transactional
    @Override
    public void generateIncomeBack() throws Exception {

        //获取收益记录状态为0且收益时间等于当前时间的收益计划 -> 返回List<收益计划>
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordListByIncomeStatusAndCurDate(0);

        Map<String,Object> paramMap = new HashMap<>();

        //循环遍历,获取到每一条收益计划
        for (IncomeRecord incomeRecord : incomeRecordList) {

            paramMap.put("uid",incomeRecord.getUid());
            paramMap.put("bidMoney",incomeRecord.getBidMoney());
            paramMap.put("incomeMoney",incomeRecord.getIncomeMoney());

            //将投资的本金和利息返还给对应用户的帐户
            int faRows = financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);
            if (faRows <= 0) {
                throw new Exception("返还收益更新账户余额失败");
            }

            //将当前收益计划的状态更新为1已返还
            incomeRecord.setIncomeStatus(1);
            int irRows = incomeRecordMapper.updateByPrimaryKeySelective(incomeRecord);
            if (irRows <= 0) {
                throw new Exception("返还收益更新收益状态失败");
            }
        }
    }
}
