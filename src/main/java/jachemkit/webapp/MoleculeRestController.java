package jachemkit.webapp;

import java.io.IOException;
import java.io.StringWriter;

import org.jgrapht.ext.DOTExporter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jachemkit.hashchem.model.NeoAtom;
import jachemkit.hashchem.model.NeoMolecule;
import jachemkit.hashchem.repo.MoleculeRepository;
import jachemkit.hashchem.service.StructureService;

@RestController
public class MoleculeRestController {

	@Autowired
	private MoleculeRepository moleculerepository;

	@Autowired
	private StructureService structureService;
    
    @RequestMapping(value="molecules/{id}", method=RequestMethod.GET, produces="text/plain")
    public String getDotOfMoleucle(@PathVariable Long id) {
        NeoMolecule mol = moleculerepository.findOne(id);
        if (mol == null) throw new RuntimeException("Cannot find id "+id);
        
        UnmodifiableUndirectedGraph<NeoAtom, DefaultEdge> graph = structureService.getStructure(mol);
        
        //TODO use better naming than defaults
        DOTExporter<NeoAtom, DefaultEdge> exporter = new DOTExporter<>();
        String dot = null;
        try (StringWriter writer = new StringWriter()){
        	exporter.export(writer, graph);
        	dot = writer.toString();
        } catch (IOException e) {
			throw new RuntimeException(e);
		}
        
        
        return dot;
    }
}
