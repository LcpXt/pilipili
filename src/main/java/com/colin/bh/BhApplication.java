package com.colin.bh;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.colin.bh.mapper")
@SpringBootApplication
public class BhApplication {

    public static void main(String[] args) {
        SpringApplication.run(BhApplication.class, args);
    }

}
