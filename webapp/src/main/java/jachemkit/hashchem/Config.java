package jachemkit.hashchem;

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

@SpringBootApplication
@EnableTransactionManagement
@EnableNeo4jRepositories(basePackages = "jachemkit.hashchem.neo")
public class Config extends Neo4jConfiguration {

	@Bean
	public SessionFactory getSessionFactory() {
		return new SessionFactory("jachemkit.hashchem.neo");
	}

	@Bean
	public Module getGuavaModule() {
	  return new GuavaModule();
	}

	public static void main(String[] args) {
		SpringApplication.run(Config.class, args);
	}
}
