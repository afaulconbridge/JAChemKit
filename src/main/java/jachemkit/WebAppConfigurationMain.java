package jachemkit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class WebAppConfigurationMain {
	
	@Bean
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(WebAppConfigurationMain.class, args);
	}
}
