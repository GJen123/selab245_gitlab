package conn;

import org.gitlab.api.TokenType;
import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;

public class conn{
	private GitlabUser gitlabUser = new GitlabUser();
	private GitlabUser root = new GitlabUser();
	private GitlabAPI newUser;
	private List<GitlabUser> users = new ArrayList<GitlabUser>();
	private List<GitlabProject> projects = new ArrayList<GitlabProject>();
	//http://140.134.26.71:20080/api/v3/users?private_token=yUnRUT5ex1s3HU7yQ_g-
	private String _hostUrl = "http://140.134.26.71:20080";
	private String _apiToken = "yUnRUT5ex1s3HU7yQ_g-";
	private TokenType tokenType = TokenType.PRIVATE_TOKEN;
	private AuthMethod authMethod = AuthMethod.URL_PARAMETER;
	public String private_token;
	public GitlabSession rootSession;
	private List<GitlabGroup> groups = new ArrayList<GitlabGroup>();
	private List<GitlabGroupMember> groupMembers = new ArrayList<GitlabGroupMember>();
	
	public conn() {
		
	}
	
	private GitlabAPI gitlab = GitlabAPI.connect(_hostUrl, _apiToken, tokenType, authMethod);

	public GitlabSession getRootSession(){
		try {
			rootSession = GitlabAPI.connect(_hostUrl, "root", "iecsfcu123456");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rootSession;
	}
	
	public GitlabSession getSession(String Url, String userName, String password){
		GitlabSession userSession = new GitlabSession();
		try{
			userSession = GitlabAPI.connect(Url, userName, password);
		}catch (IOException e){
			System.out.println(e);
		}
		return userSession;
	}
	
	public String getToken(GitlabSession session){
		return session.getPrivateToken();
	}
	
	public GitlabAPI getUserAPI(String token){
		newUser = GitlabAPI.connect(_hostUrl, token, tokenType, authMethod);
		return newUser;
	}
	
	public List<GitlabProject> getProject(GitlabUser gitlabUser){
		try {
			projects = gitlab.getProjectsViaSudo(gitlabUser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projects;
	}
	
	public GitlabUser getUser(){
		try {
			gitlabUser = gitlab.getUser();
		}catch(IOException e) {
			System.out.println(e);	
		}
		return gitlabUser;
	}
	
	public List<GitlabUser> getUsers(){
		try{
			users = gitlab.getUsers();
		}catch(IOException e) {
			System.out.println(e);	
		}
		return users;
	}
	
	public String getProjectEvent(int projectId, String private_token){
		return "http://140.134.26.71:20080/api/v3/projects/"+projectId+"/events?private_token="+private_token;
	}
	
	public String getPrivate_token(GitlabUser user){
		private_token = user.getPrivateToken();
		return private_token;
	}
	
	public int getProjectsLength(List<GitlabProject> projects){
		return projects.size();
	}
	
	public GitlabUser getRoot(){
		try {
			root = gitlab.getUser(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}
	
	public List<GitlabGroup> getGroups(){
		try {
			groups = gitlab.getGroups();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groups;
	}
	
	public List<GitlabProject> getGroupProject(GitlabGroup group){
		try {
			projects = gitlab.getGroupProjects(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projects;
	}
	
	public List<GitlabGroupMember> getGroupMembers(GitlabGroup group){
		try {
			groupMembers = gitlab.getGroupMembers(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groupMembers;
	}
	
	public String getGroupUrl(GitlabGroup group){
		String groupUrl="http://140.134.26.71:20080/groups/"+group.getName();
		return groupUrl;
	}
	
	public boolean createPrivateProject(String Pname, String description){
		List<GitlabUser> users = getUsers();
//		GitlabUser user = users.get(0);
		
		try {
			for (GitlabUser user: users){
				if (user.getId() == 1) continue;
				gitlab.createUserProject(user.getId(), Pname);
//				gitlab.createUserProject(user.getId(), Pname, description, null, true, true, true, true, false, false, 0, null);
			}
			return true;
		}catch (IOException e){
			System.out.println(e);
		}
		return false;
	}
	
}


