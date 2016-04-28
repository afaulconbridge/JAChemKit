package jachemkit.hashchem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.UnmodifiableUndirectedGraph;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class UnmodifiableUndirectedGraphSerializer<V,E>  extends StdSerializer<UnmodifiableUndirectedGraph<V,E>> {

	protected UnmodifiableUndirectedGraphSerializer() {
		super(UnmodifiableUndirectedGraph.class, true);
	}

	@Override
	public void serialize(UnmodifiableUndirectedGraph<V,E> value, JsonGenerator gen,
			SerializerProvider serializers) throws IOException, JsonProcessingException {
        gen.writeStartObject();
        
        List<V> vertexList = new ArrayList<>(value.vertexSet().size());
        vertexList.addAll(value.vertexSet());
        gen.writeFieldName("verticies");
        gen.writeStartArray();
        for (V vertex : vertexList) {
        	gen.writeObject(vertex);
        }
        gen.writeEndArray();

        gen.writeFieldName("edges");
        gen.writeStartArray();
        for (E edge : value.edgeSet()) {
            gen.writeStartArray();
            gen.writeNumber(vertexList.indexOf(value.getEdgeSource(edge)));
            gen.writeNumber(vertexList.indexOf(value.getEdgeTarget(edge)));
            gen.writeEndArray();
        }
        gen.writeEndArray();
        
        gen.writeEndObject();
	}

}
