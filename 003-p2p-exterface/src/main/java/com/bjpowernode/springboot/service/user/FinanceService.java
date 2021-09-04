package com.bjpowernode.springboot.service.user;

import com.bjpowernode.springboot.model.user.FinanceAccount;

/**
 * @author: gg
 * @create: 2021-08-31 20:11
 */
public interface FinanceService {
    FinanceAccount queryFinanceAccountByUid(Integer uid);
}
