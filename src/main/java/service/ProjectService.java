package service;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.gitlab.api.models.GitlabUser;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.util.PortalUtil;

import UnZipped.UnZip;
import conn.conn;
import jenkins.jenkinsApi;

@Path("project/")
public class ProjectService {
	
	conn userConn = new conn();
	jenkinsApi jenkins = new jenkinsApi();
	UnZip unzip = new UnZip();
	
	@POST
	@Path("create")
//	@Consumes("mutilpart/form-data")
//	@Consumes(MediaType.MULTIPART_FORM_DATA)
//	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.MULTIPART_FORM_DATA})
	@Produces(MediaType.APPLICATION_JSON)
	public Response NewProject(ActionRequest request, ActionResponse response) throws URISyntaxException {
		UploadPortletRequest uploadRequest = PortalUtil.getUploadPortletRequest(request);
		File file = uploadRequest.getFile("file");
		String Hw_Name = ParamUtil.getString(uploadRequest, "Hw_Name");
		System.out.println("Hw_Name : " + Hw_Name);
		System.out.println("file : " + file.getName());
		
		java.net.URI location = new java.net.URI("../teacherManageHW.jsp");
		  return Response.temporaryRedirect(location).build();
	}
	
//	@POST
//	@Path("create")
////	@Consumes(MediaType.MULTIPART_FORM_DATA)
////	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//	@Consumes({MediaType.APPLICATION_FORM_URLENCODED,MediaType.MULTIPART_FORM_DATA})
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response NewProject(
//			@FormParam("Hw_Name") String name, 
//			@FormParam("importUrl") String importUrl,
//			@FormDataParam("file") InputStream uploadedInputStream,
//			@FormDataParam("file") FormDataContentDisposition fileDetail) throws URISyntaxException {
//		name = "OOP-"+name;
//		userConn.createPrivateProject(name,importUrl);
//		System.out.println("filePath : "+fileDetail.getFileName());
//		
//		//---jenkins create job---
//		String jenkinsUrl = "http://140.134.26.71:38080";
//		String jenkinsCrumb = jenkins.getCrumb("GJen", "zxcv1234", jenkinsUrl);
//		jenkins.createJenkinsJob(name, jenkinsCrumb);
//		jenkins.buildJob(name, jenkinsCrumb);
//		//-----------------------
//		
//		java.net.URI location = new java.net.URI("../teacherManageHW.jsp");
//		  return Response.temporaryRedirect(location).build();
//	}
}
