package com.xiao.demo.biz;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author lix wang
 */
@Configuration
public class TestConfiguration {
    class Service1 {
        @Getter
        @Setter
        private Client1 client1;
        public Service1() {
            System.out.println("I am creating async 1");
        }
    }

    class Client1 {
        public Client1() {
            System.out.println("I am creating client 1");
        }
    }

    @Bean
    public Client1 createClient1() {
        return new Client1();
    }

    @Bean
    public Service1 createService1() {
        Service1 service1 = new Service1();
        service1.setClient1(createClient1());
        return service1;
    }

    @Bean Service1 createService2() {
        Service1 service1 = new Service1();
        service1.setClient1(createClient1());
        return service1;
    }
}
