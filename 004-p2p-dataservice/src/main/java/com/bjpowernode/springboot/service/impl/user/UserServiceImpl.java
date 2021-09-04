package com.bjpowernode.springboot.service.impl.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.springboot.constants.Constants;
import com.bjpowernode.springboot.mapper.user.FinanceAccountMapper;
import com.bjpowernode.springboot.mapper.user.UserMapper;
import com.bjpowernode.springboot.model.user.FinanceAccount;
import com.bjpowernode.springboot.model.user.User;
import com.bjpowernode.springboot.service.user.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: gg
 * @create: 2021-08-29 11:37
 **/
@Component
@Service(interfaceClass = UserService.class, version = "1.0.0", timeout = 15000)
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public Long queryAllUserCount() {
        Long allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);

        // 同步代码块 + 双重验证
        if (!ObjectUtils.allNotNull(allUserCount)) {

            // 同步代码块
            synchronized (this) {
                allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
                if (!ObjectUtils.allNotNull(allUserCount)) {
                    allUserCount = userMapper.selectAllUserCount();
                    redisTemplate.opsForValue().set(Constants.ALL_USER_COUNT, allUserCount, 7, TimeUnit.DAYS);
                    System.out.println("从MySql中获取平台总人数");
                } else {
                    System.out.println("从Redis中获取平台总人数");
                }
            }

        }  else {
            System.out.println("从Redis中获取平台总人数");
        }

        return allUserCount;
    }

    @Override
    public User queryUserByPhone(String phone) {

        return userMapper.selectUserByPhone(phone);
    }

    @Override
    public User register(String phone, String loginPassword) throws Exception {

        User user = new User();
        user.setPhone(phone);
        user.setAddTime(new Date());
        user.setLoginPassword(loginPassword);
        user.setLastLoginTime(new Date());

        int userRows = userMapper.insertSelective(user);
        if (0 == userRows) {
            throw new Exception("添加用户失败");
        }

        // 再次查询数据库
        //User userDetail = userMapper.selectUserByPhone(phone);


        // 新增账号，初始化余额是888
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setUid(user.getId());
        financeAccount.setAvailableMoney(888.0);
        int faRows = financeAccountMapper.insertSelective(financeAccount);
        if (0 == faRows) {
            throw new Exception("添加账户失败");
        }

        return user;
    }

    @Override
    public int updataByUserId(User updateUser) {

        return userMapper.updateByPrimaryKey(updateUser);
    }

    @Override
    public User queryUserByPhoneAndPwd(String phone, String loginPassword) {

        return userMapper.queryUserByPhoneAndPwd(phone,loginPassword);
    }
}
