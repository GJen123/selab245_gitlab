package jenkins;

import org.junit.Assert;
import org.junit.Test;



public class JenkinsApiTest {
	
	@Test
	public void TestGetCrumb() {
		
		jenkinsApi ja = new jenkinsApi();
		String crumb = ja.getCrumb("GJen", "zxcv1234", "http://140.134.26.71:38080");
		System.out.println(crumb);
		
		//Assert.assertEquals("e390d46093102dac6c0ec903b77af0a0", crumb);
		
	}

}
