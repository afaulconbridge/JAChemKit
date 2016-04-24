package jachemkit.webapp;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

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
	public HashChemistry hashChemistry() {
		return new HashChemistry();
	}

	public static void main(String[] args) {
		SpringApplication.run(Config.class, args);
	}
}
