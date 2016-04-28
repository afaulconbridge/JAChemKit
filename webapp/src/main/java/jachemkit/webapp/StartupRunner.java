package jachemkit.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import jachemkit.hashchem.HashChemistry;
import jachemkit.hashchem.HashMolecule;

@Component
public class StartupRunner implements CommandLineRunner  {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private HashChemistry hashChemistry;
	
	@Override
	public void run(String... args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		mapper.registerModule(new GuavaModule());

		
		HashMolecule mol = hashChemistry.createRandomMolecule();
		String json = mapper.writeValueAsString(mol); 
		log.info(json);
		
		HashMolecule mol2 = mapper.readValue(json, HashMolecule.class);
		String json2 = mapper.writeValueAsString(mol2); 

		log.info(json2);
		log.info("Equal? "+json.equals(json2));
	}

}
