package com.bjpowernode.springboot;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling // //开启Spring Task 定时器任务配置
@EnableDubboConfiguration // 开启Dubbo配置
@SpringBootApplication
public class Timer8083 {

    public static void main(String[] args) {
        SpringApplication.run(Timer8083.class, args);
    }

}
