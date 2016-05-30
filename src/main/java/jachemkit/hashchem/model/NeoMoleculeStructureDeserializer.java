package jachemkit.hashchem.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class NeoMoleculeStructureDeserializer  extends StdDeserializer<UnmodifiableUndirectedGraph<NeoAtom,DefaultEdge>> {

	protected NeoMoleculeStructureDeserializer() {
		super(UnmodifiableUndirectedGraph.class);
	}

	@Override
	public UnmodifiableUndirectedGraph<NeoAtom,DefaultEdge> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		//setup a json parser
		ObjectCodec objectCodec = p.getCodec();
		JsonNode root = objectCodec.readTree(p);

		//create the wrapped graph
		SimpleGraph<NeoAtom,DefaultEdge> simpleGraph = new SimpleGraph<>(DefaultEdge.class);

		//need a list of vertexs to resolve the reference based edges
		List<NeoAtom> vertexs = new ArrayList<>();
		
		//add all the vertexs to both the graph and temp list
		JsonNode jsonVertexs = root.findValue("verticies");
		for (JsonNode jsonVertex : (Iterable<JsonNode>) () -> jsonVertexs.elements()) {
			NeoAtom vertex = objectCodec.treeToValue(jsonVertex, NeoAtom.class);
			vertexs.add(vertex);
			simpleGraph.addVertex(vertex);
		}

		JsonNode jsonEdges = root.findValue("edges");
		for (JsonNode jsonEdge : (Iterable<JsonNode>) () -> jsonEdges.elements()) {
			//need to create another parser to handle each list of references
			JsonParser edgeParser = jsonEdge.traverse();
			//need to configure it
			edgeParser.setCodec(objectCodec);
			//now we can generate the pair of vertex ids
			List<Integer> edgeList = edgeParser.readValueAs(new TypeReference<List<Integer>>(){});
			//turn that into an edge on the graph we are building
			NeoAtom sourceVertex = vertexs.get(edgeList.get(0));
			NeoAtom targetVertex = vertexs.get(edgeList.get(1));
			simpleGraph.addEdge(sourceVertex, targetVertex);			
		}
		
		//return an unmodifiable version of the wraped graph
		return new UnmodifiableUndirectedGraph<>(simpleGraph);
	}

}
