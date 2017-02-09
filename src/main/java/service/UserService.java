package service;

import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.apache.commons.lang.StringUtils;
import org.gitlab.api.models.GitlabUser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import data.User;
import conn.conn;

@Path("user/")
public class UserService {
	
	conn userConn = new conn();

	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response upload(@FormDataParam("file") InputStream uploadedInputStream, @FormDataParam("file") FormDataContentDisposition fileDetail) throws URISyntaxException {
		String filePath = "C:\\Users\\WeiHan\\workspace\\GitLabEdu\\";
		String fileName = StringUtils.substringAfterLast(fileDetail.getFileName(), ":");
		String uploadedFileLocation = filePath + fileName;
		List<String> studentList = new ArrayList<String>();

		try {
			FileOutputStream out = new FileOutputStream(new File(uploadedFileLocation));
			int read = 0;
			byte[] bytes = new byte[1024];
			out = new FileOutputStream(new File(uploadedFileLocation));
			while ((read = uploadedInputStream.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			
			//parse file
			FileReader fr = new FileReader(uploadedFileLocation);
			BufferedReader br = new BufferedReader(fr);
			
			String line = "";
			String convert = "";
			
			while((line = br.readLine()) != null){
				String[] row = line.split(",");
				convert = row[0];
				for (int i=1; i<row.length; i++) {
					convert = convert + "," + row[i];
				}
				System.out.println(convert + "\n");
				
				studentList.add(convert);
			}
			register(studentList);
			
			fr.close();
			br.close();
			
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String output = "File successfully uploaded to : " + uploadedFileLocation;
		System.out.println(StringUtils.substringAfterLast(fileDetail.getFileName(), ":"));
		java.net.URI location = new java.net.URI("../teacherManageStudent.jsp");
		  return Response.temporaryRedirect(location).build();
	}
	
	public void register(List<String> data) {
		List<User> lsStudent = new ArrayList<User>();
		for(String lsData : data) {
			String[] row = lsData.split(",");
			String email = row[0] + "@fcu.edu.tw";
			String password = row[0];
			String userName = row[0];
			String fullName = row[1];
			String ID = row[0];
			
			User student = new User();
			student.setID(ID);
			student.setUserName(userName);
			student.setPassword(password);
			student.setEmail(email);
			student.setName(fullName);
			lsStudent.add(student);
			
			if(userConn.createUser(email, password, userName, fullName)) 
				System.out.println("register " + row[1] + " success!");
		}
		printStudent(lsStudent);
	}
	
	public void printStudent(List<User> student){
		String ID = "", userName = "", password = "", email = "", name = "";
		for(User user : student){
			ID = user.getID();
			userName = user.getUserName();
			password = user.getPassword();
			email = user.getEmail();
			name = user.getName();
			
			System.out.println("ID: " + ID + ", userName: " + userName + ", password: " + password + ", email: " + email + ", name: " + name);
		}
	}
	
	public List<GitlabUser> getUsers(){
		  return userConn.getUsers();
		 }
}
