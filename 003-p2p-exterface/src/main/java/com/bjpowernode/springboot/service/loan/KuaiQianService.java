package com.bjpowernode.springboot.service.loan;

import java.util.Map;

/**
 * @author: gg
 * @create: 2021-09-03 20:22
 */
public interface KuaiQianService {


    Map<String, String> makeKuaiQianRequestParam(String orderNo, String yuan, String name, String phone, String remoteAddr);
}
