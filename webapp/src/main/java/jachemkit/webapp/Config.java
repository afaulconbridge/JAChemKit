package jachemkit.webapp;

import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import jachemkit.hashchem.HashChemistry;

@EnableNeo4jRepositories(basePackages = "jachemkit")
@EnableTransactionManagement
@SpringBootApplication
public class Config extends Neo4jConfiguration {

	@Bean
	public SessionFactory getSessionFactory() {
		return new SessionFactory(getConfiguration(),"jachemkit");
	}

	@Bean
	public Configuration getConfiguration() {
		Configuration config = new Configuration();
		config.driverConfiguration().setDriverClassName("org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver");
		return config;
	}
	
	@Bean
	public Module customModule() {
	  return new GuavaModule();
	}

	@Bean
	public HashChemistry hashChemistry() {
		return new HashChemistry();
	}

	public static void main(String[] args) {
		SpringApplication.run(Config.class, args);
	}
}
