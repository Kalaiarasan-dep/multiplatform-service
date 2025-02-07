package in.hashconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource(value = { "classpath:notification-module.xml" })
@ComponentScan(basePackages = {"in.hashconnect", "in.hashconnect.common"})
public class NotificationServiceApplication {



    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }
}