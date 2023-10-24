package cn.zeroeden.im.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: Zero
 * @time: 2023/10/24
 * @description:
 */

@SpringBootApplication(scanBasePackages = "cn.zeroeden.im.service")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
