package service;

import java.net.URISyntaxException;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import conn.conn;

@Path("project/")
public class ProjectService {
	
	conn userConn = new conn();
	
	@POST
	@Path("create")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response NewProject(@FormParam("Hw_Name") String name, @FormParam("readme") String readme, @FormParam("importUrl") String importUrl) throws URISyntaxException {
		userConn.createPrivateProject(name, readme, importUrl);
		
		java.net.URI location = new java.net.URI("../teacherManageHW.jsp");
		  return Response.temporaryRedirect(location).build();
	}
}
