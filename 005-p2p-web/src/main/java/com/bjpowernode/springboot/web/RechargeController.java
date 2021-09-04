package com.bjpowernode.springboot.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.springboot.constants.Constants;
import com.bjpowernode.springboot.model.loan.RechargeRecord;
import com.bjpowernode.springboot.model.user.User;
import com.bjpowernode.springboot.service.loan.KuaiQianService;
import com.bjpowernode.springboot.service.loan.RechargeRecordService;
import com.bjpowernode.springboot.service.user.RedisService;
import com.bjpowernode.springboot.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 * 充值controller
 *
 * @author: gg
 * @create: 2021-09-03 17:40
 */
@Controller
public class RechargeController {


    @Reference(interfaceClass = KuaiQianService.class, version = "1.0.0", check = false, timeout = 1500)
    private KuaiQianService kuaiQianService;
    @Reference(interfaceClass = RechargeRecordService.class, version = "1.0.0", check = false, timeout = 1500)
    private RechargeRecordService rechargeRecordService;
    @Reference(interfaceClass = RedisService.class, version = "1.0.0", check = false, timeout = 1500)
    private RedisService redisService;


    /**
     * 跳转到支付页面
     *
     * @return
     */
    @RequestMapping(value = "/loan/page/toRecharge")
    public String toRecharge() {

        return "toRecharge";
    }

    // 进行支付操作
    @RequestMapping("/loan/toRecharge")
    public String submitRecharge(HttpServletRequest request,
                                 Model model,
                                 @RequestParam("rechargeMoney") String yuan) throws Exception {

        // TODO 生成订单
        // 全局唯一订单号 = 时间戳 + redis唯一数组
        String rechargeNo = DateUtil.getTimestamp() + redisService.getOnlyNumber();

        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);

        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setUid(user.getId());
        rechargeRecord.setRechargeMoney(Double.parseDouble(yuan));
        rechargeRecord.setRechargeStatus("0"); //0 充值中、1. 充值成功、 2 充值失败
        rechargeRecord.setRechargeTime(new Date());
        rechargeRecord.setRechargeDesc("快钱充值");
        rechargeRecord.setRechargeNo(rechargeNo);

        int rows = rechargeRecordService.addRecharge(rechargeRecord);
        if (rows != 1) {
            throw new Exception("充值，生成订单失败");
        }


        Map<String, String> map = kuaiQianService.makeKuaiQianRequestParam(rechargeNo, yuan, user.getName(), user.getPhone(), request.getRemoteAddr());
        model.addAllAttributes(map);









        //TODO 创建充值记录
        return "kuaiQianForm";

    }
}
