package UnZipped;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import conn.HttpConnect;
import data.GitlabData;

public class UnZip{
	HttpConnect httpConn = new HttpConnect();
    private static final String tempDir = System.getProperty("java.io.tmpdir");
    
    GitlabData gitData = new GitlabData();
    
    private String hostUrl = gitData.getHostUrl();
    
    private String token = gitData.getApiToken();

    /**
     * Size of the buffer to read/write data
     */
	private static final int BUFFER_SIZE = 4096;
    /**
     * Extracts a zip file specified by the zipFilePath to a directory specified by
     * destDirectory (will be created if does not exists)
     * @param zipFilePath
     * @param destDirectory
     * @throws IOException
     */
    public void unzip(String zipFilePath, Integer projectId, String folderName) throws IOException {
    	String destDirectory =  tempDir + "uploads\\";
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();
        // iterates over entries in the zip file
        while (entry != null) {
            String filePath = destDirectory + File.separator + entry.getName();
            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
                String fileContent = readFile(filePath);
                
                //folderLength = 選擇的.zip檔案名稱
                int folderLength = folderName.length();
                //計算folderName的大小
                int index = entry.getName().indexOf(folderName);
                //fileName = 取得folderName.zip後面的所有String , -3是因為.zip
                String fileName = entry.getName().substring(index+folderLength-3);
                
                //---httpPost to Gitlab---
                String url = hostUrl + "/api/v3/projects/"+projectId+"/repository/files?private_token=" + token;
                httpConn.httpPostFile(fileName, url, fileContent);
                //------------------------
                
            } else {
                // if the entry is a directory, make the directory
                File dir = new File(filePath);
                dir.mkdir();
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }
    /**
     * Extracts a zip entry (file entry)
     * @param zipIn
     * @param filePath
     * @throws IOException
     */
    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }
    
    private String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            return sb.toString();
        } finally {
            br.close();
        }
    }
}