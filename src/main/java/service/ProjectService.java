package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.servlet.annotation.MultipartConfig;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

import UnZipped.UnZip;
import conn.Conn;
import conn.HttpConnect;
import data.GitlabData;
import data.JenkinsData;
import jenkins.JenkinsApi;

@Path("project/")
public class ProjectService {
	
	GitlabData gitData = new GitlabData();
	JenkinsData jenkinsData = new JenkinsData();
	Conn userConn = Conn.getInstance();
	JenkinsApi jenkins = new JenkinsApi();
	UnZip unzip = new UnZip();
	GitlabUser root = userConn.getRoot();
	HttpConnect httpConn = new HttpConnect();
	private static final String tempDir = System.getProperty("java.io.tmpdir");
	
	@POST
	@Path("create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response NewProject(
			@FormDataParam("Hw_Name") String name, 
			@FormDataParam("Hw_README") String readMe, 
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) throws URISyntaxException {
		
		//先create root project
		userConn.createRootProject(name);
		Integer projectId = getNewProId(name);
		String projectUrl = getNewProUrl(name);
		
		//如果有選擇範例程式
		if(!fileDetail.getFileName().isEmpty()){
			//取得所選擇的.zip檔案名稱
			String folderName = fileDetail.getFileName();
			//將檔案存到C://User/AppData/Temp/uploads/
			String filePath = storeFileToTemp(fileDetail.getFileName(), uploadedInputStream);

			try {
				//unzip file
				unzip.unzip(filePath, projectId, folderName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{   //沒有選擇範例程式
			String filePath = this.getClass().getResource("MvnQuickStart.zip").getFile();
			String folderName = "MvnQuickStart.zip";
			try {
				//unzip file
				unzip.unzip(filePath, projectId, folderName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(!readMe.equals("<br>")){
			String readmeUrl = gitData.getHostUrl() + "/api/v3/projects/"+projectId+"/repository/files?private_token=" + gitData.getApiToken();
			httpConn.httpPostReadme(readmeUrl, readMe);
		}
		
		//create 每個學生的project
		System.out.println("projectUrl : " + projectUrl);
		userConn.createPrivateProject(name, projectUrl);
		
		//---jenkins create job---
		String jenkinsUrl = "http://" + jenkinsData.getUrl();
		String jenkinsCrumb = jenkins.getCrumb(jenkinsData.getUserName(), jenkinsData.getPassWord(), jenkinsUrl);
		jenkins.createRootJob(name, jenkinsCrumb);
		jenkins.createJenkinsJob(name, jenkinsCrumb);
		jenkins.buildJob(name, jenkinsCrumb);
		//-----------------------
		
		java.net.URI location = new java.net.URI("../teacherManageHW.jsp");
		  return Response.temporaryRedirect(location).build();
	}
	
	private String storeFileToTemp(String fileName, InputStream uploadedInputStream){
		String tempDir = System.getProperty("java.io.tmpdir");
		String uploadDir = tempDir + "uploads\\";
		try {
			createFolderIfNotExists(uploadDir);
		} catch (SecurityException se) {
			System.out.println(se.toString());
		}
		String uploadedFileLocation = uploadDir + fileName;
		try {
			saveToFile(uploadedInputStream, uploadedFileLocation);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		return uploadedFileLocation;
	}
	
	/**
	 * Utility method to save InputStream data to target location/file
	 * 
	 * @param inStream
	 *            - InputStream to be saved
	 * @param target
	 *            - full path to destination file
	 */
	private void saveToFile(InputStream inStream, String target)
			throws IOException {
		OutputStream out = null;
		int read = 0;
		byte[] bytes = new byte[1024];
		out = new FileOutputStream(new File(target));
		while ((read = inStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();
	}
	
	/**
	 * Creates a folder to desired location if it not already exists
	 * 
	 * @param dirName
	 *            - full path to the folder
	 * @throws SecurityException
	 *             - in case you don't have permission to create the folder
	 */
	private void createFolderIfNotExists(String dirName)
			throws SecurityException {
		File theDir = new File(dirName);
		if (!theDir.exists()) {
			theDir.mkdir();
		}
	}

	private Integer getNewProId(String name){
		Integer id = null;
		List<GitlabProject> rootProjects = userConn.getProject(root);
		for(GitlabProject project : rootProjects){
			String proName = project.getName();
			if(proName.equals(name)){
				id = project.getId();
			}
		}
		return id;
	}
	
	private String getNewProUrl(String name){
		String url = null;
		List<GitlabProject> rootProjects = userConn.getProject(root);
		for(GitlabProject project : rootProjects){
			String proName = project.getName();
			if(proName.equals(name)){
				url = project.getWebUrl();
				url = url.replace("0912fe2b3e43", "root:iecsfcu123456@140.134.26.71:20080");
				url = url+".git";
			}
		}
		return url;
	}
}
