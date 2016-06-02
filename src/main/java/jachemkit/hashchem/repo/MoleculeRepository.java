package jachemkit.hashchem.repo;

import org.springframework.data.neo4j.annotation.Query;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import jachemkit.hashchem.model.NeoMolecule;

@RepositoryRestResource(collectionResourceRel = "molecules", path = "molecules")
public interface MoleculeRepository extends GraphRepository<NeoMolecule> {

	@Query("MATCH (a)-[b:BUILT_FROM]-() WITH a, count(b) as atomcount WHERE atomcount = 1 RETURN a")
	Iterable<NeoMolecule> findAtoms();
}
