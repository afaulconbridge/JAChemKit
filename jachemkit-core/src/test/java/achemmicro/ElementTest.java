package achemmicro;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Assert;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElementTest {

	@Test
	public void simpleTest() {
		Element<String> elemA = Element.build("A");
		Element<String> elemA2 = Element.build("A");
		Element<String> elemB = Element.build("B");
		

		Assert.assertTrue("A must equal A2", elemA.equals(elemA2));
		Assert.assertFalse("A must not equal B", elemA.equals(elemB));
		
		Assert.assertTrue("Hashcode must be consistent", elemA.hashCode() == elemA2.hashCode());
		
		Assert.assertEquals("Comparable must be consistent", 0, elemA.compareTo(elemA2));
		Assert.assertEquals("Comparable must be consistent", -1, elemA.compareTo(elemB));
	}
}
