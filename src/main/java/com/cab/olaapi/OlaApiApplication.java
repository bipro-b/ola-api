package com.cab.olaapi;

import com.cab.olaapi.config.RSAKeyRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


@EnableConfigurationProperties(RSAKeyRecord.class)
@SpringBootApplication
public class OlaApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(OlaApiApplication.class, args);
	}

}
