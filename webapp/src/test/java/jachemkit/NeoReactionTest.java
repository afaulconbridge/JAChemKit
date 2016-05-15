package jachemkit;

import static org.junit.Assert.*;

import java.util.Comparator;

import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;

import jachemkit.hashchem.Config;
import jachemkit.hashchem.neo.MoleculeRepository;
import jachemkit.hashchem.neo.NeoAtom;
import jachemkit.hashchem.neo.NeoMolecule;
import jachemkit.hashchem.neo.NeoReaction;
import jachemkit.hashchem.neo.ReactionRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Config.class)
public class NeoReactionTest {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MoleculeRepository moleculeRepository;
	
	@Autowired
	private ReactionRepository reactionRepository;

	@Test
	public void testPersistance() {

		//create a molecule
		NeoAtom a1 = new NeoAtom(ImmutableList.of(1,2,3,4,5,6,7,8));
		
		NeoMolecule mol = new NeoMolecule(a1);		
		mol.addAtom(new NeoAtom (ImmutableList.of(2,2,3,4,5,6,7,8)), a1);
		mol.addAtom(new NeoAtom (ImmutableList.of(2,2,3,4,5,6,7,8)), a1);
		NeoAtom  a4 = new NeoAtom(ImmutableList.of(3,2,3,4,5,6,7,8));
		mol.addAtom(a4, a1);
		mol.addAtom(new NeoAtom (ImmutableList.of(5,2,3,4,5,6,7,8)), a4);
		
		//persist it
		mol = moleculeRepository.save(mol);
		
		//create a reaction
		Multiset<NeoMolecule> reactants = HashMultiset.create();
		reactants.add(mol);
		reactants.add(mol);
		log.info("reactants: "+reactants);
		log.info("reactants.size(): "+reactants.size());
		
		Multiset<NeoMolecule> products = HashMultiset.create();	
		products.add(mol);
		products.add(mol);	
		NeoReaction reaction = new NeoReaction(reactants, products);
		
		log.info("reaction.getReactants(): "+reaction.getReactants());
		log.info("reaction.getReactants().size(): "+reaction.getReactants().size());
		
		//persist the reaction
		reaction = reactionRepository.save(reaction);
		
		log.info("reaction.getReactants(): "+reaction.getReactants());
		log.info("reaction.getReactants().size(): "+reaction.getReactants().size());
		
		//now load it
		//NeoReaction reaction2 = reactionRepository.findAll().iterator().next();
		NeoReaction reaction2 = reactionRepository.findOne(reaction.getNeoId());

		log.info("reaction.getReactants(): "+reaction.getReactants());
		log.info("reaction.getReactants().size(): "+reaction.getReactants().size());
		
		//test it
		int two = 2;
		assertEquals("Must be two molecules in reactants before persisting", two, reaction.getReactants().size());
		assertEquals("Must be two molecules in reactants after persisting", two, reaction2.getReactants().size());
		assertEquals("Must be one types of molecules in reactants", 1, reaction2.getReactants().elementSet().size());

		assertEquals("Must be two molecules in products before persisting", two, reaction.getProducts().size());
		assertEquals("Must be two molecules in products after persisting", two, reaction2.getProducts().size());
		assertEquals("Must be one types of molecules in products", 1, reaction2.getProducts().elementSet().size());
	}
	
}
