package com.bjpowernode.springboot.service.impl.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.springboot.mapper.user.FinanceAccountMapper;
import com.bjpowernode.springboot.model.user.FinanceAccount;
import com.bjpowernode.springboot.service.user.FinanceService;
import com.bjpowernode.springboot.service.user.RedisService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: gg
 * @create: 2021-08-31 20:13
 */
@Component
@Service(interfaceClass = FinanceService.class, version = "1.0.0", timeout = 15000)
public class FinanceServiceImpl implements FinanceService {

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryFinanceAccountByUid(Integer uid) {

        return financeAccountMapper.selectByUid(uid);
    }
}
