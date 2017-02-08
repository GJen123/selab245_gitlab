package conn;

import org.gitlab.api.TokenType;
import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import jenkins.jenkinsApi;

public class conn{
	private String _hostUrl = "http://140.134.26.71:20080";
	private String _apiToken = "yUnRUT5ex1s3HU7yQ_g-";
	private TokenType tokenType = TokenType.PRIVATE_TOKEN;
	private AuthMethod authMethod = AuthMethod.URL_PARAMETER;
	
	private GitlabAPI gitlab = GitlabAPI.connect(_hostUrl, _apiToken, tokenType, authMethod);
	
	//http://140.134.26.71:20080/api/v3/users?private_token=yUnRUT5ex1s3HU7yQ_g-
	
	public conn() {
		
	}
	private GitlabSession rootSession;
	

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
		GitlabAPI newUser;
		newUser = GitlabAPI.connect(_hostUrl, token, tokenType, authMethod);
		return newUser;
	}
	
	public List<GitlabProject> getProject(GitlabUser gitlabUser){
		List<GitlabProject> projects = new ArrayList<GitlabProject>();
		try {
			projects = gitlab.getProjectsViaSudo(gitlabUser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projects;
	}
	
	public GitlabUser getUser(){
		GitlabUser gitlabUser = new GitlabUser();
		try {
			gitlabUser = gitlab.getUser();
		}catch(IOException e) {
			System.out.println(e);	
		}
		return gitlabUser;
	}
	
	public List<GitlabUser> getUsers(){
		List<GitlabUser> users = new ArrayList<GitlabUser>();
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
		String private_token;
		private_token = user.getPrivateToken();
		return private_token;
	}
	
	public int getProjectsLength(List<GitlabProject> projects){
		return projects.size();
	}
	
	public GitlabUser getRoot(){
		GitlabUser root = new GitlabUser();
		try {
			root = gitlab.getUser(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}
	
	public List<GitlabGroup> getGroups(){
		List<GitlabGroup> groups = new ArrayList<GitlabGroup>();
		try {
			groups = gitlab.getGroups();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groups;
	}
	
	public List<GitlabProject> getGroupProject(GitlabGroup group){
		List<GitlabProject> projects = new ArrayList<GitlabProject>();
		try {
			projects = gitlab.getGroupProjects(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projects;
	}
	
	public List<GitlabGroupMember> getGroupMembers(GitlabGroup group){
		List<GitlabGroupMember> groupMembers = new ArrayList<GitlabGroupMember>();
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
	
	/**
	 * createUserProject(Integer userId, 
	 * 					String name, 
	 * 					String description, 
	 * 					String defaultBranch, 
	 * 					Boolean issuesEnabled, 
	 * 					Boolean wallEnabled, 
	 * 					Boolean mergeRequestsEnabled, 
	 * 					Boolean wikiEnabled, 
	 *					Boolean snippetsEnabled, 
	 *					Boolean publik, 
	 *					Integer visibilityLevel, 
	 *					String importUrl)
	 */
	public boolean createPrivateProject(String Pname, String description){
		List<GitlabUser> users = getUsers();
		List<GitlabProject> projects = new ArrayList<GitlabProject>();
//		GitlabUser user = users.get(0);
		conn conn = new conn();
		httpConnect httpconn = new httpConnect();
		jenkinsApi jenkins = new jenkinsApi();
		try {
			for (GitlabUser user: users){
				if (user.getId() == 1) continue;
				gitlab.createUserProject(user.getId(), Pname);
				String jobName = user.getUsername()+"_"+Pname;
				String strUrl = "http://GJen:02031fefb728e700973b6f3e5023a64c@140.134.26.71:38080/createItem?name="+jobName;
				String proUrl = "http://140.134.26.71:20080/" + user.getUsername() + "/" + Pname + ".git";
				jenkins.postCreateJob("GJen", "zxcv1234", strUrl, jobName, proUrl);
//				gitlab.createUserProject(user.getId(), Pname, description, null, true, true, true, true, false, false, 0, null);
				if(description!=null){
					System.out.println("description not null");
					projects = conn.getProject(user);
					for(GitlabProject project : projects){
						
						String name = project.getName();
						int id = project.getId();
						String url = "http://140.134.26.71:20080/api/v3/projects/"+id+"/repository/files?private_token=yUnRUT5ex1s3HU7yQ_g-";
						if(name.equals(Pname)){
							httpconn.httpPostReadme( url, description);
						}
					}
				}
			}
			return true;
		}catch (IOException e){
			System.out.println(e);
		}
		return false;
	}
	
	/**
     * Create a new User
     * createUser(String email, String password, String username,
     *            String fullName, String skypeId, String linkedIn,
     *            String twitter, String website_url, Integer projects_limit,
     *            String extern_uid, String extern_provider_name,
     *            String bio, Boolean isAdmin, Boolean can_create_group,
     *            Boolean skip_confirmation)
     *
     * @param email                User email
     * @param password             Password
     * @param username             User name
     * @param fullName             Full name
     * @param skypeId              Skype Id
     * @param linkedIn             LinkedIn
     * @param twitter              Twitter
     * @param website_url          Website URL
     * @param projects_limit       Projects limit
     * @param extern_uid           External User ID
     * @param extern_provider_name External Provider Name
     * @param bio                  Bio
     * @param isAdmin              Is Admin
     * @param can_create_group     Can Create Group
     * @param skip_confirmation    Skip Confirmation
     * @return                     A GitlabUser
     * @throws IOException on gitlab api call error
     * @see <a href="http://doc.gitlab.com/ce/api/users.html">http://doc.gitlab.com/ce/api/users.html</a>
     */	
	public boolean createUser(String email, String password, String userName, String fullName) {
		try {
			gitlab.createUser(email, password, userName, fullName, "", "", "", "", 10, "", "", "", false, true, null);
			return true;
		}catch (IOException e){
			System.out.println(e);
		}
		return false;
	}
	
	 /**
     * Creates a Group
     *
     * @param name The name of the group. The
     *             name will also be used as the path
     *             of the group.
     * @return The GitLab Group
     * @throws IOException on gitlab api call error
     */
    public GitlabGroup createGroup(String name){
        try {
        	return gitlab.createGroup(name);
        }catch (IOException e) {
        	System.out.println(e);
        }
        return new GitlabGroup();
    }
    
    /**
     * Add a group member.
     *
     * @param group       the GitlabGroup
     * @param user        the GitlabUser
     * @param accessLevel the GitlabAccessLevel
     * @return the GitlabGroupMember
     * @throws IOException on gitlab api call error
     */
    public boolean addMember(int groupId, int userId, int level){
    	GitlabAccessLevel accessLevel = GitlabAccessLevel.Guest;
    	if(level == 40){
    		accessLevel = GitlabAccessLevel.Master;
    	}
    	else if(level == 30){
    		accessLevel = GitlabAccessLevel.Developer;
    	}
    	else{
    		accessLevel = GitlabAccessLevel.Guest;
    	}
    	
        try {
			gitlab.addGroupMember(groupId, userId, accessLevel);
			return true;
		}catch (IOException e){
			System.out.println(e);
		}
		return false;
    }
}


