package jachemkit.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jachemkit.hashchem.repo.MoleculeRepository;

@Controller
public class MoleculeController {

	@Autowired
	private MoleculeRepository moleculerepository;
	
    @RequestMapping("molecules/{id}")
    public String greeting(@PathVariable Long id, Model model) {
        model.addAttribute("mol", moleculerepository.findOne(id));
        return "molecule";
    }

}