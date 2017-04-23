package conn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.exception.LoadConfigFailureException;

public class StudentConn {
  GitlabConfig gitData = GitlabConfig.getInstance();

  String privateToken;
  private GitlabUser user = new GitlabUser();
  private String hostUrl;
  private String apiToken;
  private TokenType tokenType = TokenType.PRIVATE_TOKEN;
  private AuthMethod authMethod = AuthMethod.URL_PARAMETER;
  private GitlabAPI gitlab;
  private List<GitlabProject> project = new ArrayList<GitlabProject>();

  /**
   * Constructor
   * @param privateToken      The student private token from gitlab
   * @throws LoadConfigFailureException on properties call error
   * @throws IOException on gitlab get user error
   */
  public StudentConn(String privateToken) throws LoadConfigFailureException, IOException {
    this.privateToken = privateToken;
    hostUrl = gitData.getGitlabHostUrl();
    apiToken = gitData.getGitlabApiToken();
    gitlab = GitlabAPI.connect(hostUrl, privateToken, tokenType, authMethod);
    getUser();
  }

  public void getUser() throws IOException {
    user = gitlab.getUser();
  }

  public String getUsername() {
    return user.getUsername();
  }

  /**
   * Get user's projects
   * @return list of projects
   * @throws IOException on gitlab get projects error
   */
  public List<GitlabProject> getProject() throws IOException {
    project = gitlab.getProjects();
    return project;
  }

  /**
   * Get gitlab project commit counts
   * @param projectId          The gitlab project id
   * @return commit counts
   * @throws IOException on gitlab call error
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
   * Get all gitlab project's commits
   * @param projectId        The gitlab project id
   * @return list of commits
   * @throws IOException on gitlab call error
   */
  public List<GitlabCommit> getAllCommits(int projectId) throws IOException {
    List<GitlabCommit> lsCommits = new ArrayList<GitlabCommit>();
    lsCommits = gitlab.getAllCommits(projectId);
    return lsCommits;
  }
}