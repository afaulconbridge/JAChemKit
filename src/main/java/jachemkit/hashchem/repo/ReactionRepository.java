package jachemkit.hashchem.repo;

import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import jachemkit.hashchem.model.NeoReaction;

@RepositoryRestResource(collectionResourceRel = "reactions", path = "reactions")
public interface ReactionRepository extends GraphRepository<NeoReaction> {

}
