package com.bjpowernode.springboot;

import com.alibaba.dubbo.spring.boot.annotation.EnableDubboConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubboConfiguration//开启Dubbo配置
@MapperScan(basePackages = "com.bjpowernode.springboot.mapper")
public class DataserviceMain8081 {

    public static void main(String[] args) {
        SpringApplication.run(DataserviceMain8081.class, args);
    }

}
