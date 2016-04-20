package jachemkit.hashchem;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

@Component
public class HashChemRunner implements CommandLineRunner {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void run(String... args) throws Exception {
		
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
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			//swallow exception because it should never happen
			throw new RuntimeException(e);
		}
		
		Iterator<Atom<String>> it = new DepthFirstIterator<Atom<String>,DefaultEdge>(graph,start);
		while(it.hasNext()) {
			Atom<String> next = it.next();
			try {
				md.update(next.t.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				//swallow exception because it should never happen
				throw new RuntimeException(e);
			}
		}
		byte[] hash = md.digest();
		List<Byte> listHash = new ArrayList<>(hash.length);
		for (int i = 0; i < hash.length; i++) {
			listHash.add(new Byte(hash[i]));
		}
		return Collections.unmodifiableList(listHash);
	}
	
	private class Atom<T>{
		public final T t;
		public Atom(T t){
			this.t = t;
		}
	}
}
