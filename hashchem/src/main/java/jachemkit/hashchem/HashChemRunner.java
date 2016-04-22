package jachemkit.hashchem;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

import jachemkit.core.Atom;
import jachemkit.core.Molecule;

@Component
public class HashChemRunner implements CommandLineRunner {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void run(String... args) throws Exception {

		//create a random moleucle
		Molecule<String> mol = new Molecule<>();
		Atom<String> a = new Atom<String>("A");
		Atom<String> b = new Atom<String>("B");
		mol.addVertex(a);
		mol.addVertex(b);
		mol.addEdge(a,b);
		//test if it is stable
		getBreakingEdges(mol);
		
		/*
		SimpleGraph<Atom<String>, DefaultEdge> molA = new SimpleGraph<>(DefaultEdge.class);
		SimpleGraph<Atom<String>, DefaultEdge> molB = new SimpleGraph<>(DefaultEdge.class);
		
		molA.addVertex(new Atom<String>("A"));
		molB.addVertex(new Atom<String>("B"));
		
		//now do an actual reaction
		for (Atom<String> atomA: molA.vertexSet()) {
			List<Byte> hashA = getHash(molA, atomA);
			for (Atom<String> atomB: molB.vertexSet()) {
				//should probably do this outside of the inner loop
				List<Byte> hashB = getHash(molB, atomB);
				Integer score = getHashDiff(hashA, hashB);
				log.info("score = "+score);
			}
		}
		*/
	}
	
	private Set<DefaultEdge> getBreakingEdges(Molecule<String> mol) {
		if (!(new ConnectivityInspector(mol)).isGraphConnected()) {
			throw new IllegalArgumentException("Must be a single connected component");
		}
		
		Set<DefaultEdge> breakingEdges = new HashSet<>();
		for (DefaultEdge e : new HashSet<>(mol.edgeSet())) {
			Atom<String> source = mol.getEdgeSource(e);
			Atom<String> target = mol.getEdgeTarget(e);
			//test if this edge can break
			//remove it
			mol.removeEdge(e);
			//evaluate
			int diff = getHashDiff(getHash(mol, source), getHash(mol, target));
			log.info("diff = "+diff);
			//return
			mol.addEdge(source, target);
		}
		return breakingEdges;
	}
	
	private Integer getHashDiff(List<Byte> hashA, List<Byte> hashB) {
		//TODO check lengths
		Integer total = 0;
		for (Byte b : hashA) {
			total += b;
		}
		for (Byte b : hashB) {
			total -= b;
		}
		return total;
	}
	
	private List<Byte> getHash(Graph<Atom<String>,DefaultEdge> graph, Atom<String> start) {
		//if the hashing is order sensitive (md5) need this to iterate in a reliable and consistent manner 
		//  this doesnt because it uses set and its hard to order arbitrary graph nodes
		//so use a order independent (commutative) hashing algorithm (e.g. addition with overflow)
		//  then we dont care about the ordering of nodes
		//    but then the structure of the graph wont matter, only content
		byte[] hash;
		try {
		hash = start.body.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			//swallow exception because it should never happen
			throw new RuntimeException(e);
		}
		Iterator<Atom<String>> it = new DepthFirstIterator<Atom<String>,DefaultEdge>(graph,start);
		while(it.hasNext()) {
			Atom<String> next = it.next();
			byte[] nextHash;
			try {
				nextHash = next.body.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				//swallow exception because it should never happen
				throw new RuntimeException(e);
			}
			for (int i = 0; i < hash.length; i++) {
				hash[i] += nextHash[i];
			}
		}
		List<Byte> listHash = new ArrayList<>(hash.length);
		for (int i = 0; i < hash.length; i++) {
			listHash.add(new Byte(hash[i]));
		}
		return Collections.unmodifiableList(listHash);
	}
}
