package jachemkit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;

import jachemkit.hashchem.neo.NeoAtom;
import jachemkit.hashchem.neo.NeoMolecule;
import jachemkit.hashchem.neo.NeoReaction;

@RunWith(SpringRunner.class)
public class NeoReactionTest {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

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
		
		
		assertEquals("Must be two molecules in reactants", 2, reaction.getReactants().size());
		assertEquals("Must be one types of molecules in reactants", 1, reaction.getReactants().elementSet().size());

		assertEquals("Must be two molecules in products", 2, reaction.getProducts().size());
		assertEquals("Must be one types of molecules in products", 1, reaction.getProducts().elementSet().size());
	}
	
}
