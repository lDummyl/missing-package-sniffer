package web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class Publisher {
    public static void main(String[] args) {
        SpringApplication.run(Publisher.class, args);
    }
}
