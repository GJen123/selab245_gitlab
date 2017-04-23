package conn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabAccessLevel;
import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabGroup;
import org.gitlab.api.models.GitlabGroupMember;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabSession;
import org.gitlab.api.models.GitlabUser;

import data.User;
import db.UserDbManager;
import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.exception.LoadConfigFailureException;

public class Conn {
  private static Conn INSTANCE;
  
  private Conn() throws LoadConfigFailureException {
    INSTANCE = new Conn();
    hostUrl = gitData.getGitlabHostUrl();
    apiToken = gitData.getGitlabApiToken();
  }
  

  public static Conn getInstance() {
    return INSTANCE;
  }
  
  GitlabConfig gitData = GitlabConfig.getInstance();

  private String hostUrl;
  private String apiToken;
  private TokenType tokenType = TokenType.PRIVATE_TOKEN;
  private AuthMethod authMethod = AuthMethod.URL_PARAMETER;

  private GitlabAPI gitlab = GitlabAPI.connect(hostUrl, apiToken, tokenType, authMethod);

  private static UserDbManager dbManager = UserDbManager.getInstance();

  HttpConnect httpConn = HttpConnect.getInstance();
  
  
  
  /**
   * Get root session from Gitlab
   * @return root's session from Gitlab
   */
  public GitlabSession getRootSession() throws LoadConfigFailureException {
    GitlabSession rootSession = null;
    try {
      rootSession = GitlabAPI.connect(hostUrl, 
                                      gitData.getGitlabRootUsername(), 
                                      gitData.getGitlabRootPassword());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return rootSession;
  }

  /**
   * Get user session by username and password
   * 
   * @param userName Gitlab username
   * @param password Gitlab password
   * @return User's Session
   * @throws IOException on gitlab api call error
   * */
  public GitlabSession getSession(String userName, String password) throws IOException {
    GitlabSession userSession = new GitlabSession();
    userSession = GitlabAPI.connect(hostUrl, userName, password);
    return userSession;
  }

  public String getToken(GitlabSession session) {
    return session.getPrivateToken();
  }

  /**
   * Get a new GitlabApi by user token
   * @param token A token from user
   * @return a new GitlabApi
   */
  public GitlabAPI getUserApi(String token) {
    GitlabAPI newUser;
    newUser = GitlabAPI.connect(hostUrl, token, tokenType, authMethod);
    return newUser;
  }

  /**
   * Get a list of project by user
   * @param user A user from database
   * @return The project list of user
   * @throws IOException on gitlab api call error
   */
  public List<GitlabProject> getProject(User user) throws IOException {
    GitlabUser gitlabUser = new GitlabUser();
    gitlabUser.setId(user.getGitLabId());
    return getProject(gitlabUser);
  }

  /**
   * Get a list of project by GitlabUser
   * @param user of gitlab
   * @return The project list of user
   * @throws IOException on gitlab api call error
   */
  public List<GitlabProject> getProject(GitlabUser user) throws IOException {

    List<GitlabProject> projects = new ArrayList<GitlabProject>();
    projects = gitlab.getProjectsViaSudo(user);
    return projects;
  }
  
  /**
   * Get all user's list of projects
   * @throws IOException on gitlab api call error
   */
  public List<GitlabProject> getAllProjects() throws IOException {
    List<GitlabProject> projects = new ArrayList<GitlabProject>();
    projects = gitlab.getAllProjects();
    return projects;
  }

  /**
   * Get a gitlab user
   * @throws IOException on gitlab api call error
   */
  public GitlabUser getUser() throws IOException {
    GitlabUser gitlabUser = new GitlabUser();
    gitlabUser = gitlab.getUser();
    return gitlabUser;
  }

  /**
   * Get all user from Gitlab
   * @return a list of users
   * @throws IOException on gitlab api call error
   */
  public List<GitlabUser> getUsers() throws IOException {
    List<GitlabUser> users = new ArrayList<GitlabUser>();
    users = gitlab.getUsers();
    return users;
  }

  /**
   * Get a private token by GitlabUser
   * @param user A Gitlab user
   * @return a private token of user
   */
  public String getPrivateToken(GitlabUser user) {
    String privateToken;
    privateToken = user.getPrivateToken();
    return privateToken;
  }

  public int getProjectsLength(List<GitlabProject> projects) {
    return projects.size();
  }

  /**
   * Get GitlabUser of Root
   * @return GitlabUser of Root
   * @throws IOException on gitlab api call error
   */
  public GitlabUser getRoot() throws IOException {
    GitlabUser root = new GitlabUser();
    root = gitlab.getUser(1);
    return root;
  }

  /**
   * Get all groups of Gitlab
   * @return a list of groups from Gitlab
   * @throws IOException on gitlab api call error
   */
  public List<GitlabGroup> getGroups() throws IOException {
    List<GitlabGroup> groups = new ArrayList<GitlabGroup>();
    groups = gitlab.getGroups();
    return groups;
  }

  /**
   * Get a list of project from group
   * @param group A group form Gitlab
   * @return a list of project from group
   * @throws IOException on gitlab api call error
   */
  public List<GitlabProject> getGroupProject(GitlabGroup group) throws IOException {
    List<GitlabProject> projects = new ArrayList<GitlabProject>();
    projects = gitlab.getGroupProjects(group);
    return projects;
  }

  /**
   * Get a list of group's member
   * @param group a group from Gitlab
   * @return a list of group's member
   * @throws IOException on gitlab api call error
   */
  public List<GitlabGroupMember> getGroupMembers(GitlabGroup group) throws IOException {
    List<GitlabGroupMember> groupMembers = new ArrayList<GitlabGroupMember>();
    groupMembers = gitlab.getGroupMembers(group);
    return groupMembers;
  }

  public String getGroupUrl(GitlabGroup group) {
    String groupUrl = hostUrl + "/groups/" + group.getName();
    return groupUrl;
  }

  /**
   * createUserProject(Integer userId, String name, String description, String defaultBranch,
   * Boolean issuesEnabled, Boolean wallEnabled, Boolean mergeRequestsEnabled, Boolean wikiEnabled,
   * Boolean snippetsEnabled, Boolean publik, Integer visibilityLevel, String importUrl)
   * @throws IOException on gitlab api call error
   */
  public boolean createPrivateProject(String proName, String proUrl) throws IOException {
    List<GitlabUser> users = getUsers();
    for (GitlabUser user : users) {
      if (user.getId() == 1 || user.getId() == 2) {
        continue;
      }
      GitlabProject project = gitlab.createUserProject(user.getId(), proName, null, null, null,
                                                       null, null, null, null, null, null, proUrl);
    }
    return true;
  }

  /**
   * Create a new User
   * 
   * @param email          User email
   * @param password       User password
   * @param userName       User name
   * @param fullName       Full name
   * @return true or false
   * @throws LoadConfigFailureException on a instance call error
   * @throws IOException on gitlab api call error
   */
  public boolean createUser(String email, String password, String userName, String fullName) 
                 throws LoadConfigFailureException, IOException {
    GitlabUser user = new GitlabUser();
    user = gitlab.createUser(email, password, userName, fullName, 
           "", "", "", "", 10, "", "", "", false, true, null);
    String privateToken = INSTANCE.getSession(userName, password).getPrivateToken();
    user.setPrivateToken(privateToken);
    dbManager.addUser(user);
    return true;
  }

  /**
   * Creates a Group
   *
   * @param name
   *          The name of the group. The name will also be used as the path of the group.
   * @return The GitLab Group
   * @throws IOException
   *           on gitlab api call error
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
   * Add a member to group
   * @param groupId         Group id
   * @param userId          User id
   * @param level           User level in group
   * @return true or false
   * @throws IOException on gitlab api call error
   */
  public boolean addMember(int groupId, int userId, int level) throws IOException {
    GitlabAccessLevel accessLevel = GitlabAccessLevel.Guest;
    if (level == 40) {
      accessLevel = GitlabAccessLevel.Master;
    } else if (level == 30) {
      accessLevel = GitlabAccessLevel.Developer;
    } else {
      accessLevel = GitlabAccessLevel.Guest;
    }

    gitlab.addGroupMember(groupId, userId, accessLevel);
    return true;
  }

  /**
   * Create a root project
   * @param proName         Project name
   * @return true or false
   * @throws IOException on gitlab api call error
   */
  public boolean createRootProject(String proName) throws IOException {
    gitlab.createUserProject(1, proName, null, null, null, null, null, null, null, null, 10, null);
    return true;
  }

  /**
   * Get commit counts from project
   * @param projectId     Project id
   * @return a count of commit
   * @throws IOException on gitlab api call error
   */
  public int getAllCommitsCounts(int projectId) throws IOException {
    int count = 0;
    List<GitlabCommit> lsCommits = new ArrayList<GitlabCommit>();
    if (!gitlab.getAllCommits(projectId).isEmpty()) {
      lsCommits = gitlab.getAllCommits(projectId);
      count = lsCommits.size();
    } else {
      count = 0;
    }
    return count;
  }

  /**
   * Replace the project url
   * @param oldUrl      The old url of project
   * @return the new url
   */
  public String getReplaceUrl(String oldUrl) {
    String oldStr = oldUrl.substring(0, 19);
    oldUrl = oldUrl.replace(oldStr, hostUrl);
    return oldUrl;
  }

}
