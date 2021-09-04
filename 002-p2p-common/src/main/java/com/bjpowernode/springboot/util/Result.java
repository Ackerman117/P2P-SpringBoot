package com.bjpowernode.springboot.util;

import java.util.HashMap;

/**
 * @author: gg
 * @create: 2021-08-30 16:37
 */
public class Result extends HashMap<String,Object> {

    /**
     * 响应成功结果
     * @return
     */
    public static Result success() {
        Result result = new Result();
        result.put("code",1);
        result.put("success",true);

        return result;
    }


    /**
     * 响应失败结果
     * @param message
     * @return
     */
    public static Result error(String message) {
        Result result = new Result();
        result.put("code",-1);
        result.put("success",false);
        result.put("message",message);

        return result;
    }

}
