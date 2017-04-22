package fcu.selab.progedu.utils;

import java.io.IOException;

import org.junit.Test;

import fcu.selab.progedu.utils.ZipHandler;

public class ZipHandlerTest {
	
	@Test
	public void testUnzipFile(){
		ZipHandler unzip = new ZipHandler();
		String zipFilePath = this.getClass().getResource("MvnQuickStart.zip").getFile();
		try {
			unzip.unzip(zipFilePath, 0, "MvnQuickStart.zip");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
