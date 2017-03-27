package conn;

import org.junit.Test;

public class HttpConnectTest{
	@Test
	public void testHttpPostSrc(){
		HttpConnect httpConn = new HttpConnect();
		String file_path = "";
		String url = "";
		String fileContent = "";
		httpConn.httpPostFile(file_path, url, fileContent);
	}
}