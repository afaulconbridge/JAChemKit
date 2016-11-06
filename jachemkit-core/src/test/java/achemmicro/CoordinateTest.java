package achemmicro;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Assert;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CoordinateTest {

	@Test
	public void simpleTest() {
		Coordinate elemA = Coordinate.from(0,0);
		Coordinate elemA2 = Coordinate.from(0,0);
		Coordinate elemB = Coordinate.from(0,1);
		

		Assert.assertTrue("A must equal A2", elemA.equals(elemA2));
		Assert.assertFalse("A must not equal B", elemA.equals(elemB));
		
		Assert.assertTrue("Hashcode must be consistent", elemA.hashCode() == elemA2.hashCode());
		
		Assert.assertEquals("Comparable must be consistent", 0, elemA.compareTo(elemA2));
		Assert.assertNotEquals("Comparable must be consistent", 0, elemA.compareTo(elemB));
	}
}
