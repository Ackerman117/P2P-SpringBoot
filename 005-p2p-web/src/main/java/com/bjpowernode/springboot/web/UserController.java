package com.bjpowernode.springboot.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.springboot.model.user.FinanceAccount;
import com.bjpowernode.springboot.service.user.FinanceService;
import com.bjpowernode.springboot.service.user.RedisService;
import com.bjpowernode.springboot.util.HttpClientUtils;
import com.bjpowernode.springboot.util.Result;
import com.bjpowernode.springboot.constants.Constants;
import com.bjpowernode.springboot.model.user.User;
import com.bjpowernode.springboot.service.user.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author: gg
 * @create: 2021-08-30 12:25
 */
@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class, version = "1.0.0", check = false, timeout = 15000)
    private UserService userService;
    @Reference(interfaceClass = RedisService.class, version = "1.0.0", check = false, timeout = 15000)
    private RedisService redisService;
    @Reference(interfaceClass = FinanceService.class, version = "1.0.0", check = false, timeout = 15000)
    private FinanceService financeService;


    /**
     * 跳转到登录页面
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/loan/page/register")
    public String register(Model model) {
        return "register";
    }


    @RequestMapping(value = "/loan/checkPhone")
    @ResponseBody
    public Object checkPhone(@RequestParam(value = "phone", required = true) String phone) {
        // 判断数据库是否有该手机号
        User user = userService.queryUserByPhone(phone);
        if (ObjectUtils.allNotNull(user)) {
            return Result.error("手机号码已被注册，请更换手机号");
        }
        return Result.success();
    }


    @PostMapping(value = "/loan/register")
    public @ResponseBody
    Object loanRegister(HttpServletRequest request,
                        String phone,
                        String loginPassword,
                        String messageCode) {
        try {
            // 首先判断用户输入的验证码是否正确
            String redisCode = redisService.get(phone);
            if (!StringUtils.equals(messageCode, redisCode)) {
                return Result.error("验证码错误");
            }

            User user = userService.register(phone, loginPassword);
            // 用户信息存放到Session中去
            request.getSession().setAttribute(Constants.SESSION_USER, user);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("系统繁忙，请稍后重试。");
        }

        return Result.success();
    }

    @PostMapping(value = "/user/messageCode")
    @ResponseBody
    public Result messageCode(HttpServletRequest request,
                              @RequestParam(value = "phone", required = true) String phone) throws Exception {

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("appkey", "0e97b3c5e497b7637867e71c44a42b1e");
        paramMap.put("mobile", phone);
        // 生成一个随机验证码
        String messageCode = this.getRandomNumber(6);
        paramMap.put("content", "【凯信通】您的验证码是：" + messageCode);
        System.out.println(paramMap.get("content"));
        //String jsonString = HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong", paramMap);
        String jsonString = "{\n" +
                "\t\"code\": \"10000\",\n" +
                "\t\"charge\": false,\n" +
                "\t\"remain\": 0,\n" +
                "\t\"msg\": \"查询成功\",\n" +
                "\t\"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-6850193</remainpoint>\\n <taskID>162317289</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                "}";
        //将json格式的字符串转成json对象
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        //获取通信标示code
        String code = jsonObject.getString("code");
        // 判断
        if (!StringUtils.equals("10000", code)) {
            // 通信失败
            return Result.error("发送短信失败");
        }
        // 获取其中result中的xml数据
        String resultXmlString = jsonObject.getString("result");
        //dom4j+xpath解析xml
        Document document = DocumentHelper.parseText(resultXmlString);
        // 根据路径表达式来找到returnstatus标签
        Node node = document.selectSingleNode("/returnsms/returnstatus[1]");
        String text = node.getText();

        if (!StringUtils.equals(text, "Success")) {
            return Result.error("发送短信失败");
        }
        // 发送成功，将验证码保存在redis中
        redisService.put(phone, messageCode);

        return Result.success();
    }


    /**
     * 获取随机6位数验证码
     *
     * @param n
     * @return
     */
    private String getRandomNumber(int n) {

        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < n; i++) {
            int number = random.nextInt(10);
            sb.append(number);
        }

        return sb.toString();
    }


    @RequestMapping("/user/realName")
    public String realName() {
        return "realName";
    }


    @ResponseBody
    @RequestMapping(value = "/loan/realName")
    public Result realName(HttpServletRequest request,
                           @RequestParam(value = "phone", required = true) String phone,
                           @RequestParam(value = "realName", required = true) String realName,
                           @RequestParam(value = "idCard", required = true) String idCard,
                           @RequestParam(value = "messageCode", required = true) String messageCode) {

        // 调用第三方接口
        try {
            // 验证码校验
            String redisCode = redisService.get(phone);
            if (!StringUtils.equals(messageCode, redisCode)) {
                return Result.error("验证码错误");
            }

            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("appkey", "0e97b3c5e497b7637867e71c44a42b1e");
            paramMap.put("cardNo", idCard);
            paramMap.put("realName", realName);
            //String jsonString = HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test",paramMap);
            String jsonString = "{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 1305,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": {\n" +
                    "        \"error_code\": 0,\n" +
                    "        \"reason\": \"成功\",\n" +
                    "        \"result\": {\n" +
                    "            \"realname\": \"乐天磊\",\n" +
                    "            \"idcard\": \"350721197702134399\",\n" +
                    "            \"isok\": true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            // 把json格式的字符串转成json对象
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            // 从json对象中获取相关信息
            String code = jsonObject.getString("code");
            if (!StringUtils.equals(code, "10000")) {
                // 通信异常
                return Result.error("通信异常");
            }

            /*// 获取isok判断是否返回成功
            Boolean isOk = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");
            if (!isOk) {
                return Result.error("姓名与身份证匹配失败");
            }*/

            String outResult = jsonObject.getString("result");
            JSONObject parseObject = JSONObject.parseObject(outResult);

            String innerResult = parseObject.getString("result");
            JSONObject object = JSONObject.parseObject(innerResult);

            Boolean isok = object.getBoolean("isok");

            if (!isok) {
                return Result.error("姓名与身份证匹配失败");
            }

            // 从session中获取User
            User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
            sessionUser.setPhone(phone);
            sessionUser.setIdCard(idCard);
            sessionUser.setName(realName);
            // 将真实信息更新到用户表
            int row = userService.updataByUserId(sessionUser);
            if (row <= 0) {
                return Result.error("实名认证后，更新用户失败");
            }

            // 需要再次对数据库进行查询
            User user = userService.queryUserByPhone(phone);

            // 更新session中的信息
            request.getSession().setAttribute(Constants.SESSION_USER, user);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("通信异常，请稍后重试");
        }
        return Result.success();
    }


    /**
     * 查询账户
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/loan/myFinanceAccount")
    @ResponseBody
    public FinanceAccount myFinanceAccount(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        return financeService.queryFinanceAccountByUid(user.getId());
    }


    /**
     * 用户退出
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/loan/logout")
    public String logoutUser(HttpServletRequest request) {
        // 退出登录有两种方式
        // 1. 将session设置为失效状态(不建议)
        //request.getSession().invalidate();
        // 2. 删除session中的用户信息
        request.getSession().removeAttribute(Constants.SESSION_USER);
        // 注意，不能直接调整到index 而是重定向
        return "redirect:/index";    // 这里的/表示上下文根 /p2p
    }


    /**
     * 跳转到登录页面
     *
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/loan/page/login")
    public String pageLogin(HttpServletRequest request, Model model) {
        return "login";
    }


    /**
     * 验证登录
     *
     * @param request
     * @param phone
     * @param loginPassword
     * @param messageCode
     * @return
     */
    @RequestMapping(value = "/loan/login")
    public @ResponseBody
    Result login(HttpServletRequest request,
                 @RequestParam(value = "phone", required = true) String phone,
                 @RequestParam(value = "loginPassword", required = true) String loginPassword,
                 @RequestParam(value = "messageCode", required = true) String messageCode) {

        try {
            // 判断验证码是否正确
            String redisCode = redisService.get(phone);
            if (!StringUtils.equals(messageCode, redisCode)) {
                return Result.error("验证码错误");
            }

            // 请求登录[登录时1. 验证用户名和密码是否正确，更新登录时间所以要通道事务]
            User user = userService.queryUserByPhoneAndPwd(phone, loginPassword);

            // 将用户信息存放到session中
            if (!ObjectUtils.allNotNull(user)) {
                return Result.error("账号或密码错误");
            }

            // 修改最后登录时间
            user.setLastLoginTime(new Date());
            userService.updataByUserId(user);

            request.getSession().setAttribute(Constants.SESSION_USER, user);

        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败");
        }
        return Result.success();
    }

    /**
     * 进入用户中心
     * @param request
     * @param model
     * @return
     */
    @RequestMapping(value = "/loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model)  {

        // 先获取session中的数据
        User user = (User)request.getSession().getAttribute(Constants.SESSION_USER);
        // 根据用户id查询投资信息
        FinanceAccount financeAccount = financeService.queryFinanceAccountByUid(user.getId());
        model.addAttribute("financeAccount",financeAccount);

        return "myCenter";
    }



}
