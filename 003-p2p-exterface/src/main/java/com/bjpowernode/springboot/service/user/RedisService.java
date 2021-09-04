package com.bjpowernode.springboot.service.user;

/**
 * @author: gg
 * @create: 2021-08-31 14:29
 */
public interface RedisService {
    void put(String phone, String code);

    String get(String phone);

    /**
     *
     * @return
     */
    Long getOnlyNumber();
}
