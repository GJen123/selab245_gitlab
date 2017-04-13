package Unzipped;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Assert;
import org.junit.Test;

import UnZipped.UnZip;

public class UnzipTest {
	
	@Test
	public void testExtractFile(){
		UnZip unzip = new UnZip();
		String zipFilePath = "C:\\Users\\GJen\\Desktop\\test\\JavacQuickStart.zip";
		String filePath = "C:\\Users\\GJen\\Desktop\\test\\JavacQuickStartABCDE";
		
		try {
			unzip.unzip(zipFilePath, 0, "JavacQuickStart.zip");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
