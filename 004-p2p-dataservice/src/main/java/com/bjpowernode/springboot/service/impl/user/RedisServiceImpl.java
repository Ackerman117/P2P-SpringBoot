package com.bjpowernode.springboot.service.impl.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.springboot.constants.Constants;
import com.bjpowernode.springboot.service.user.RedisService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author: gg
 * @create: 2021-08-31 14:31
 */
@Component
@Service(interfaceClass = RedisService.class, version = "1.0.0", timeout = 15000)
public class RedisServiceImpl implements RedisService {

    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public void put(String phone, String code) {

        redisTemplate.opsForValue().set(phone,code, 30, TimeUnit.MINUTES);

    }

    @Override
    public String get(String phone) {

        String result = (String) redisTemplate.opsForValue().get(phone);

        return result;
    }

    @Override
    public Long getOnlyNumber() {

        return redisTemplate.opsForValue().increment(Constants.ONLY_NUMBER,1);
    }
}
