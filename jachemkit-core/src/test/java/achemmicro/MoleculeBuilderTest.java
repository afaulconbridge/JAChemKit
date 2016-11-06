package achemmicro;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

import org.junit.Assert;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MoleculeBuilderTest {

	@Test
	public void simpleTest() {		
		MoleculeBuilder<String> builder = new MoleculeBuilder<>();
		builder.fromElement(0, 0, "A").fromElement(1, 1, "B");
		Assert.assertTrue("Should have two components", builder.findComponents().size() == 2);
		Assert.assertTrue("Should have two molecules", builder.buildAll().size() == 2);
		Assert.assertTrue("Should have two distinct molecules", ImmutableSet.copyOf(builder.buildAll()).size() == 2);
	}
	
	@Test
	public void bondTest() {		
		MoleculeBuilder<String> builder = new MoleculeBuilder<>();
		builder.fromElement(0, 0, "A")
			.fromElement(0, 1, "A")
			.fromBond(0, 0, 0, 1)
			.fromElement(1, 0, "B")
			.fromElement(1, 1, "B")
			.fromBond(1, 0, 1, 1);
		Assert.assertTrue("Should have two components", builder.findComponents().size() == 2);
		Assert.assertTrue("Should have two molecules", builder.buildAll().size() == 2);
		Assert.assertTrue("Should have two distinct molecules", ImmutableSet.copyOf(builder.buildAll()).size() == 2);
	}
	@Test
	public void offset() {		
		MoleculeBuilder<String> builder = new MoleculeBuilder<>();
		builder.fromElement(1, 1, "B").fromElement(1, 2, "B").fromBond(1, 1, 1, 2);
		Molecule<String> b = builder.build();
		Assert.assertTrue("Must be able to get from 0,0", b.getElement(0, 0).equals("B"));
		Assert.assertTrue("Must be able to get from 0,1", b.getElement(0, 1).equals("B"));
		Assert.assertTrue("One bond from 0,0 to 0,1", b.getBondedFrom(0,0).equals(ImmutableSortedSet.of(Coordinate.from(0, 1))));
	}
}
