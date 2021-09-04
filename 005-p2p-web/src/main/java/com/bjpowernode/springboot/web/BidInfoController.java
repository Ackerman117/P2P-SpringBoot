package com.bjpowernode.springboot.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.springboot.constants.Constants;
import com.bjpowernode.springboot.model.user.User;
import com.bjpowernode.springboot.service.loan.BidInfoService;
import com.bjpowernode.springboot.util.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: gg
 * @create: 2021-09-02 16:39
 */
@RestController
public class BidInfoController {

    @Reference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false)
    private BidInfoService bidInfoService;


    /**
     * 立即投资操作
     * @param request
     * @param loanId
     * @param bidMoney
     * @return
     */
    @RequestMapping(value = "/loan/invest")
    public Result invest(HttpServletRequest request,
                         @RequestParam(value = "loanId", required = true) Integer loanId,
                         @RequestParam(value = "bidMoney", required = true) Double bidMoney) {
        try {
            User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("uid",user.getId());
            paramMap.put("loanId",loanId);
            paramMap.put("bidMoney",bidMoney);
            paramMap.put("phone",user.getPhone());

            // 用户投资（1. 更新产品可投金额 2. 更新账户可用余额 3. 新增投资记录 4. 判断产品是否满标）
            // 以上需要的参数 产品标示(ID) 投资金额 用户标示
            bidInfoService.invest(paramMap);
        }catch (Exception e) {
            e.printStackTrace();
            Result.error("投资失败");
        }
        return Result.success();
    }

}
