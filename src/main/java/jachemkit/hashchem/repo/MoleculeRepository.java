package jachemkit.hashchem.repo;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;

import jachemkit.hashchem.model.NeoMolecule;

public interface MoleculeRepository extends GraphRepository<NeoMolecule> {

	@Query("MATCH (a)-[b:BUILT_FROM]-() WITH a, count(b) as atomcount WHERE atomcount = 1 RETURN a")
	Iterable<NeoMolecule> findAtoms();
}
