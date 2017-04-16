package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.ws.rs.core.Response.Status;

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
import data.Project;
import db.ProjectDBManager;
import db.UserDBManager;
import jenkins.JenkinsApi;

@Path("project/")
public class ProjectService {
	
	private GitlabData gitData = new GitlabData();
	private JenkinsData jenkinsData = new JenkinsData();
	private Conn userConn = Conn.getInstance();
	private JenkinsApi jenkins = new JenkinsApi();
	private UnZip unzip = new UnZip();
	private GitlabUser root = userConn.getRoot();
	private HttpConnect httpConn = new HttpConnect();
	private ProjectDBManager dbManager = ProjectDBManager.getInstance();
	private UserDBManager UserDB = UserDBManager.getInstance();
	private List<GitlabUser> users = userConn.getUsers();
	
	private static final String tempDir = System.getProperty("java.io.tmpdir");
	
	@POST
	@Path("create")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response NewProject(
			@FormDataParam("Hw_Name") String name, 
			@FormDataParam("Hw_README") String readMe, 
			@FormDataParam("fileRadio") String fileType, 
			@FormDataParam("file") InputStream uploadedInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail) {
		
		boolean hasTemplate = false;
		boolean isSave = true;
		
		try{
			//先create root project
			userConn.createRootProject(name);
			Integer projectId = getNewProId(name);
			String projectUrl = getNewProUrl(name);
			String filePath = null;
			String folderName = null;
			StringBuilder sb = new StringBuilder();
			
			//如果有選擇範例程式
			if(!fileDetail.getFileName().isEmpty()){
				hasTemplate = true;
				
				//取得所選擇的.zip檔案名稱
				folderName = fileDetail.getFileName();
				//將檔案存到C://User/AppData/Temp/uploads/
				filePath = storeFileToTemp(fileDetail.getFileName(), uploadedInputStream);
			}else{   //沒有選擇範例程式
				filePath = this.getClass().getResource("MvnQuickStart.zip").getFile();
				folderName = "MvnQuickStart.zip";
			}
			unzipFile(filePath, projectId, folderName);
			// config_javac.xml 裡需要的command line
			sb = unzip.getStringBuilder();
			
			if(!readMe.equals("<br>")){
				String readmeUrl = gitData.getHostUrl() + "/api/v3/projects/"+projectId+"/repository/files?private_token=" + gitData.getApiToken();
				httpConn.httpPostReadme(readmeUrl, readMe);
			}
			
			//create 每個學生的project
			System.out.println("projectId : " + projectId);
			forkProjectFromRoot(projectId);
			//userConn.createPrivateProject(name, projectId);
			
			//---jenkins create job---
			String jenkinsUrl = "http://" + jenkinsData.getUrl();
			String jenkinsCrumb = jenkins.getCrumb(jenkinsData.getUserName(), jenkinsData.getPassWord(), jenkinsUrl);
			jenkins.createRootJob(name, jenkinsCrumb, fileType, sb);
			jenkins.createJenkinsJob(name, jenkinsCrumb, fileType, sb);
			jenkins.buildJob(name, jenkinsCrumb);
			//-----------------------
			
			addProject(name, readMe, fileType, hasTemplate);
		}catch(Exception e){
			isSave = false;
			e.printStackTrace();
		}
		
//		java.net.URI location = new java.net.URI("../teacherHW.jsp");
//		  return Response.temporaryRedirect(location).build();
		Response response = Response.ok().build();
	    if (!isSave) {
	      response = Response.serverError().status(Status.INTERNAL_SERVER_ERROR).build();
	    }
	    return response;
	}
	
	public void unzipFile(String filePath, int projectId, String folderName){
		try {
			//unzip file
			unzip.unzip(filePath, projectId, folderName);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addProject(String name, String description, String type, boolean hasTemplate){
		Project project = new Project();
		
		project.setName(name);
		project.setDescription(description);
		project.setType(type);
		project.setHasTemplate(hasTemplate);
		
		dbManager.addProject(project);
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
	
	private void forkProjectFromRoot(int forkedIdFromRoot){
		for(GitlabUser user : users){
			String userName = user.getUsername();
			String userPrivateToken = UserDB.getUser(userName).getPrivateToken();
			httpConn.httpPostForkProject(userPrivateToken, forkedIdFromRoot);
		}
	}
}
