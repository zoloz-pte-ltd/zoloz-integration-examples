package com.zoloz.example;


import com.alibaba.fastjson.parser.ParserConfig;
import com.zoloz.example.autoconfig.ApiClientConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;




/**
 * @author hans.zy@antfin.com
 * @date 2022/6/10  9:21 pm
 */
@Import({ServerInfoListener.class, ApiClientConfig.class})
@SpringBootApplication
public class Application {
    public static void main(String[] args) {

        ParserConfig.getGlobalInstance().setSafeMode(true);

        SpringApplication.run(Application.class,args);
    }
}