package conn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.GitlabAPIException;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.*;

import data.GitlabData;
import db.UserDBManager;

public class Conn {
	GitlabData gitData = new GitlabData();
	
	private String _hostUrl = gitData.getHostUrl();
	private String _apiToken = gitData.getApiToken();
	private TokenType tokenType = TokenType.PRIVATE_TOKEN;
	private AuthMethod authMethod = AuthMethod.URL_PARAMETER;

	private GitlabAPI gitlab = GitlabAPI.connect(_hostUrl, _apiToken, tokenType, authMethod);
	
	private static UserDBManager dbManager = UserDBManager.getInstance();

	// http://140.134.26.71:20080/api/v3/users?private_token=yUnRUT5ex1s3HU7yQ_g-

	private static Conn conn = new Conn();

	public static Conn getInstance() {
	   return conn;
	}
	
	private Conn() {

	}

	private GitlabSession rootSession;

	public GitlabSession getRootSession() {

		try {
			rootSession = GitlabAPI.connect(_hostUrl, gitData.getUserName(), gitData.getPassWord());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rootSession;
	}

	public GitlabSession getSession(String Url, String userName, String password) {
		GitlabSession userSession = new GitlabSession();
		try {
			userSession = GitlabAPI.connect(Url, userName, password);
		} catch (IOException e) {
			System.out.println(e);
		}
		return userSession;
	}

	public String getToken(GitlabSession session) {
		return session.getPrivateToken();
	}

	public GitlabAPI getUserAPI(String token) {
		GitlabAPI newUser;
		newUser = GitlabAPI.connect(_hostUrl, token, tokenType, authMethod);
		return newUser;
	}

	public List<GitlabProject> getProject(GitlabUser gitlabUser) {
		List<GitlabProject> projects = new ArrayList<GitlabProject>();
		try {
			projects = gitlab.getProjectsViaSudo(gitlabUser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projects;
	}

	public List<GitlabProject> getAllProjects() {
		List<GitlabProject> projects = new ArrayList<GitlabProject>();
		try {
			projects = gitlab.getAllProjects();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projects;
	}

	public GitlabUser getUser() {
		GitlabUser gitlabUser = new GitlabUser();
		try {
			gitlabUser = gitlab.getUser();
		} catch (IOException e) {
			System.out.println(e);
		}
		return gitlabUser;
	}

	public List<GitlabUser> getUsers() {
		List<GitlabUser> users = new ArrayList<GitlabUser>();
		try {
			users = gitlab.getUsers();
		} catch (IOException e) {
			System.out.println(e);
		}
		return users;
	}

	public String getProjectEvent(int projectId, String private_token) {
		// return
		// "http://140.134.26.71:20080/api/v3/projects/"+projectId+"/events?private_token="+private_token;
		return _hostUrl + "/api/v3/projects/" + projectId + "/events?private_token=" + private_token;
	}

	public String getPrivate_token(GitlabUser user) {
		String private_token;
		private_token = user.getPrivateToken();
		return private_token;
	}

	public int getProjectsLength(List<GitlabProject> projects) {
		return projects.size();
	}

	public GitlabUser getRoot() {
		GitlabUser root = new GitlabUser();
		try {
			root = gitlab.getUser(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}

	public List<GitlabGroup> getGroups() {
		List<GitlabGroup> groups = new ArrayList<GitlabGroup>();
		try {
			groups = gitlab.getGroups();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groups;
	}

	public List<GitlabProject> getGroupProject(GitlabGroup group) {
		List<GitlabProject> projects = new ArrayList<GitlabProject>();
		try {
			projects = gitlab.getGroupProjects(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return projects;
	}

	public List<GitlabGroupMember> getGroupMembers(GitlabGroup group) {
		List<GitlabGroupMember> groupMembers = new ArrayList<GitlabGroupMember>();
		try {
			groupMembers = gitlab.getGroupMembers(group);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return groupMembers;
	}

	public String getGroupUrl(GitlabGroup group) {
		String groupUrl = _hostUrl + "/groups/" + group.getName();
		return groupUrl;
	}

	/**
	 * createUserProject(Integer userId, String name, String description, String
	 * defaultBranch, Boolean issuesEnabled, Boolean wallEnabled, Boolean
	 * mergeRequestsEnabled, Boolean wikiEnabled, Boolean snippetsEnabled,
	 * Boolean publik, Integer visibilityLevel, String importUrl)
	 */
	public boolean createPrivateProject(String Pname, String importUrl) {
		List<GitlabUser> users = getUsers();
		try {
			for (GitlabUser user : users) {
				if (user.getId() == 1)
					continue;
				gitlab.createUserProject(user.getId(), Pname, null, null, null, null, null, null, null, null, null, importUrl);
			}
			return true;
		} catch (IOException e) {
			System.out.println(e);
		}
		return false;
	}

	/**
	 * Create a new User createUser(String email, String password, String
	 * username, String fullName, String skypeId, String linkedIn, String
	 * twitter, String website_url, Integer projects_limit, String extern_uid,
	 * String extern_provider_name, String bio, Boolean isAdmin, Boolean
	 * can_create_group, Boolean skip_confirmation)
	 *
	 * @param email
	 *            User email
	 * @param password
	 *            Password
	 * @param username
	 *            User name
	 * @param fullName
	 *            Full name
	 * @param skypeId
	 *            Skype Id
	 * @param linkedIn
	 *            LinkedIn
	 * @param twitter
	 *            Twitter
	 * @param website_url
	 *            Website URL
	 * @param projects_limit
	 *            Projects limit
	 * @param extern_uid
	 *            External User ID
	 * @param extern_provider_name
	 *            External Provider Name
	 * @param bio
	 *            Bio
	 * @param isAdmin
	 *            Is Admin
	 * @param can_create_group
	 *            Can Create Group
	 * @param skip_confirmation
	 *            Skip Confirmation
	 * @return A GitlabUser
	 * @throws IOException
	 *             on gitlab api call error
	 * @see <a href=
	 *      "http://doc.gitlab.com/ce/api/users.html">http://doc.gitlab.com/ce/api/users.html</a>
	 */
	public boolean createUser(String email, String password, String userName, String fullName) {
		try {
			GitlabUser user = new GitlabUser();
			user = gitlab.createUser(email, password, userName, fullName, "", "", "", "", 10, "", "", "", false, true, null);
			dbManager.addUser(user);
			return true;
		} catch (IOException e) {
			System.out.println(e);
		}
		return false;
	}

	/**
	 * Creates a Group
	 *
	 * @param name
	 *            The name of the group. The name will also be used as the path
	 *            of the group.
	 * @return The GitLab Group
	 * @throws IOException
	 *             on gitlab api call error
	 */
	public GitlabGroup createGroup(String name) {
		try {
			return gitlab.createGroup(name);
		} catch (IOException e) {
			System.out.println(e);
		}
		return new GitlabGroup();
	}

	/**
	 * Add a group member.
	 *
	 * @param group
	 *            the GitlabGroup
	 * @param user
	 *            the GitlabUser
	 * @param accessLevel
	 *            the GitlabAccessLevel
	 * @return the GitlabGroupMember
	 * @throws IOException
	 *             on gitlab api call error
	 */
	public boolean addMember(int groupId, int userId, int level) {
		GitlabAccessLevel accessLevel = GitlabAccessLevel.Guest;
		if (level == 40) {
			accessLevel = GitlabAccessLevel.Master;
		} else if (level == 30) {
			accessLevel = GitlabAccessLevel.Developer;
		} else {
			accessLevel = GitlabAccessLevel.Guest;
		}

		try {
			gitlab.addGroupMember(groupId, userId, accessLevel);
			return true;
		} catch (IOException e) {

			e.printStackTrace();
		}
		return false;
	}

	public boolean createRootProject(String Pname) {
		try {
			gitlab.createUserProject(1, Pname, null, null, null, null, null, null, null, null, null, null);
			return true;
		} catch (IOException e) {
			System.out.println(e);
		}
		return false;
	}

	public int getAllCommits(int projectId){
		int count = 0;
		List<GitlabCommit> lsCommits = new ArrayList<GitlabCommit>();
		try {
			if(!gitlab.getAllCommits(projectId).isEmpty()){
				System.out.println("not empty");
				lsCommits = gitlab.getAllCommits(projectId);
				for(GitlabCommit commit : lsCommits){
					System.out.println("abc : " + commit.getAuthorName());
				}
			}else{
				System.out.println("empty");
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
//		for (GitlabCommit commit : lsCommits) {
//			if (!commit.getId().isEmpty())
//				count++;
//		}
		
		return count;
	}

}
