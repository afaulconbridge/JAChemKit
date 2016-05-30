package jachemkit.hashchem.repo;

import org.springframework.data.neo4j.repository.GraphRepository;

import jachemkit.hashchem.model.NeoReaction;

public interface ReactionRepository extends GraphRepository<NeoReaction> {

}
