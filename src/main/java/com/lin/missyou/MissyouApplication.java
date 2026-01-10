package com.lin.missyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;

/**
 * 别忘了启动Redis
 * redis-server
 */
@SpringBootApplication
public class MissyouApplication {

	public static void main(String[] args) {
		//返回ConfigurableApplicationContext
		SpringApplication.run(MissyouApplication.class, args);
	}
}
