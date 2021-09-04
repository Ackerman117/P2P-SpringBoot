package com.bjpowernode.springboot.service.user;

import com.bjpowernode.springboot.model.user.User;

/**
 * @author: gg
 * @create: 2021-08-29 11:32
 **/
public interface UserService {

    /** @Author gg
     * @Description //TODO 查询平台的用户数量
     * @Date 11:36 2021/8/29
     * @Param 
     * @return 
     **/
    Long queryAllUserCount();

    /**
     * 根据手机号码查询用户信息
     * @param phone
     * @return
     */
    User queryUserByPhone(String phone);

    /**
     * 根据手机号查询User
     * @param phone
     * @param loginPassword
     * @return
     */
    User register(String phone, String loginPassword) throws Exception;

    /**
     * 实名认证后更新用户信息
     * @param updateUser
     * @return
     */
    int updataByUserId(User updateUser);

    /**
     * 登录验证
     * @param phone
     * @param loginPassword
     * @return
     */
    User queryUserByPhoneAndPwd(String phone, String loginPassword);
}
