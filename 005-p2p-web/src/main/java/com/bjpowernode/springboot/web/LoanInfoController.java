package com.bjpowernode.springboot.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.springboot.constants.Constants;
import com.bjpowernode.springboot.model.loan.BidInfo;
import com.bjpowernode.springboot.model.loan.LoanInfo;
import com.bjpowernode.springboot.model.user.FinanceAccount;
import com.bjpowernode.springboot.model.user.User;
import com.bjpowernode.springboot.model.vo.BidUserVO;
import com.bjpowernode.springboot.model.vo.PaginationVO;
import com.bjpowernode.springboot.service.loan.BidInfoService;
import com.bjpowernode.springboot.service.loan.LoanInfoService;
import com.bjpowernode.springboot.service.user.FinanceService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: gg
 * @create: 2021-08-29 18:54
 */
@Controller
public class LoanInfoController {

    @Reference(interfaceClass = LoanInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private LoanInfoService loanInfoService;
    @Reference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false, timeout = 15000)
    private BidInfoService bidInfoService;
    @Reference(interfaceClass = FinanceService.class, version = "1.0.0", check = false, timeout = 15000)
    private FinanceService financeService;



    @RequestMapping("loan/loan")
    public String loan(Model model,
                       @RequestParam(value = "ptype", required = false) Integer ptype,
                       @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage) {
        // 分页显示产品列表，参数（产品类型(可以为空),当前页currentPage(默认值是1)，每页显示的条数）
        // 返回的参数：List<LoanInfo>   分页信息
        Integer pageSize = 9;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("currentPage", (currentPage - 1) * pageSize);
        paramMap.put("pageSize", pageSize);

        if (ObjectUtils.allNotNull(ptype)) {
            paramMap.put("productType", ptype);
        }
        PaginationVO<LoanInfo> paginationVO = loanInfoService.queryLoanInfoByPage(paramMap);

        // 产品列表
        List<LoanInfo> loanInfoList = paginationVO.getDataList();
        model.addAttribute("loanInfoList", loanInfoList);
        // 总条数
        Integer total = paginationVO.getTotal();
        model.addAttribute("total", total);
        // 当前页
        model.addAttribute("currentPage", currentPage);

        // 计算出总页数
        Integer totalPage = (total - 1)/pageSize + 1;
        model.addAttribute("totalPage", totalPage);

        // 点击换页时需要参数ptype
        if (ObjectUtils.allNotNull(ptype)) {
            model.addAttribute("ptype",ptype);
        }

        // TODO 投资排行榜
        List<BidUserVO> bidUserVOList = bidInfoService.queryBidUserTop();
        model.addAttribute("bidUserVOList",bidUserVOList);

        return "loan";
    }

    /**
     * 产品详情页数据查询
     * @param model
     * @param id 产品ID
     * @return
     */
    @RequestMapping(value = "/loan/loanInfo")
    public String loanInfo(Model model,
                           HttpServletRequest request,
                           @RequestParam(value = "id", required = true) Integer id) {
        // 根据产品ID查询产品信息
        LoanInfo loanInfo = loanInfoService.queryLoanInfoById(id);
        model.addAttribute("loanInfo",loanInfo);

        // 查询投资记录(多表查询)
        // 参数：产品id，currentPage:0 pageSize:10
        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("loanId",id);
        paramMap.put("currentPage",0);
        paramMap.put("pageSize",10);
        List<BidInfo> bidInfoList = bidInfoService.queryRecentlyBidInfoByLoanId(paramMap);
        model.addAttribute("bidInfoList",bidInfoList);

        // TODO 立即投资

        // 查询可用资金
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        if (ObjectUtils.allNotNull(user)) {
            // user不为空
            FinanceAccount financeAccount = financeService.queryFinanceAccountByUid(user.getId());
            model.addAttribute("availableMoney",financeAccount.getAvailableMoney());
        }


        return "loanInfo";
    }

}
