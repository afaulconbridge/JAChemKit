package achemmicro;

import org.springframework.stereotype.Service;

@Service
public class AsciiRenderer {

	public String toAscii(Molecule mol) {
		StringBuilder sb = new StringBuilder();
		//print top edge
		sb.append("+");
		for (int i = 0; i < mol.getWidth(); i++) {
			sb.append("-");
		}
		sb.append("+\n");
		//for each row
		for (int y = mol.getHeight()-1; y >=0; y--) {
			//print element
			sb.append("|");
			for (int x = 0; x < mol.getWidth(); x++) {
				if (mol.getElement(x, y).isPresent()) {
					sb.append(mol.getElement(x,y).get());
				} else {
					sb.append(" ");
				}
			}
			sb.append("|\n");
		
		}
		//print bottom edge
		sb.append("+");
		for (int i = 0; i < mol.getWidth(); i++) {
			sb.append("-");
		}
		sb.append("+");
		return sb.toString();
	}
}

