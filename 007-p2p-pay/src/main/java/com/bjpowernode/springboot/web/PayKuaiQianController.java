package com.bjpowernode.springboot.web;

import com.bjpowernode.springboot.service.KuaiQianService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: gg
 * @create: 2021-09-04 09:41
 */
@Controller
public class PayKuaiQianController {

    @Resource
    private KuaiQianService kuaiQianService;

    // 接受快钱的异步通知
    @GetMapping("/kq/notify")
    public void receiveKQNotify(HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("========receiveKQNotify=========");

        try {
            kuaiQianService.handlerKQNotify(request);
        }finally {
            PrintWriter out = response.getWriter();
            out.println("<result>1</result><redirecturl>http://47.113.198.114:9999/showKQ.html</redirecturl>");
            out.flush();
            out.close();
        }
    }

}
