package com.bjpowernode.springboot.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.springboot.constants.Constants;
import com.bjpowernode.springboot.model.loan.LoanInfo;
import com.bjpowernode.springboot.service.loan.BidInfoService;
import com.bjpowernode.springboot.service.loan.LoanInfoService;
import com.bjpowernode.springboot.service.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @version 1.0
 * @program: P2P-SpringBoot
 * @description:
 * @author: gg
 * @create: 2021-08-28 21:04
 **/
@Controller
public class IndexController {

    @Reference(interfaceClass = LoanInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private LoanInfoService loanInfoService;
    @Reference(interfaceClass = UserService.class, version = "1.0.0", check = false, timeout = 15000)
    private UserService userService;
    @Reference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private BidInfoService bidInfoService;

    @RequestMapping("/index")
    public String toIndex(Model model) {

       /* // 模拟多线程高并发访问Redis(存在缓存击穿问题)
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 1000; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    // 开启线程执行任务
                    Double historyAvgRete = loanInfoService.queryHistoryAvgRate();
                    model.addAttribute("historyAvgRete", historyAvgRete);
                }
            });
        }
        // 关闭线程池
        executorService.shutdownNow();*/


        // 获取平台历史年化收益率
        Double historyAvgRete = loanInfoService.queryHistoryAvgRate();
        model.addAttribute(Constants.HISTORY_AVG_RATE, historyAvgRete);


        // 获取平台注册人数
        Long allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT, allUserCount);


        // 获取平台累计成交金额
        Double allBidMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BID_MONEY, allBidMoney);


        /*
            分析：以下三个产品
                尽管在页面上展示的效果不同，但是实际上都是查询产品列表
                根据产品类型获取产品列表(参数 产品类型, 页码, 每页显示的条数) 最终返回-->List<产品>
         */
        // 获取新手宝产品
        Map<String, Object> paramMap = new HashMap<>();
        // 产品类型为0，显示第1页，每页显示1个
        paramMap.put("productType", Constants.PRODUCT_TYPE_X);
        paramMap.put("currentPage", 0);  // 每个产品都是显示第1页
        paramMap.put("pageSize", 1); //每页显示的条数(新手宝只显示1条)
        List<LoanInfo> xLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList", xLoanInfoList);


        // 获取优选产品
        paramMap.put("productType", Constants.PRODUCT_TYPE_Y);
        paramMap.put("pageSize", 4);
        List<LoanInfo> yLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("yLoanInfoList", yLoanInfoList);


        // 获取散标产品
        paramMap.put("productType", Constants.PRODUCT_TYPE_S);
        paramMap.put("pageSize", 8);
        List<LoanInfo> sLoanInfoList = loanInfoService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList", sLoanInfoList);

        return "index";
    }
}