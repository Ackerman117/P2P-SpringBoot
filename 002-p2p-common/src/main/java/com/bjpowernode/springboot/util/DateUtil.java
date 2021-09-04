package com.bjpowernode.springboot.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 生成时间戳
 * @author: gg
 * @create: 2021-09-03 22:24
 */
public class DateUtil {

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
