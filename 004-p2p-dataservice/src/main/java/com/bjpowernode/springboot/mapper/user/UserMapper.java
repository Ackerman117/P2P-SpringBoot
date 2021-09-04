package com.bjpowernode.springboot.mapper.user;

import com.bjpowernode.springboot.model.user.User;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    Long selectAllUserCount();

    User selectUserByPhone(String phone);

    User queryUserByPhoneAndPwd(String phone, String loginPassword);
}