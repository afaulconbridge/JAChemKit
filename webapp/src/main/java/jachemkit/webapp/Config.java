package jachemkit.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.neo4j.NodeEntityScan;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

@SpringBootApplication
@EnableTransactionManagement
@EnableNeo4jRepositories("jachemkit.hashchem.neo")
@NodeEntityScan("jachemkit.hashchem.neo")
public class Config extends SpringBootServletInitializer {

	@Bean
	public Module getGuavaModule() {
	  return new GuavaModule();
	}

	public static void main(String[] args) {
		SpringApplication.run(Config.class, args);
	}
}
