package com.bjpowernode.springboot;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfiguration // 开启Dubbo配置
public class Pay9090 {

    public static void main(String[] args) {
        SpringApplication.run(Pay9090.class, args);
    }

}
