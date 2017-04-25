package conn;

import java.util.List;

import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabUser;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.conn.Conn;

public class getUserToken {

  GitlabConfig gitData = GitlabConfig.getInstance();

  static Conn c = Conn.getInstance();

  private static List<GitlabUser> users = c.getUsers();

  private String _hostUrl = gitData.getGitlabHostUrl();
  private String _apiToken = gitData.getGitlabApiToken();
  private TokenType tokenType = TokenType.PRIVATE_TOKEN;
  private AuthMethod authMethod = AuthMethod.URL_PARAMETER;

  private GitlabAPI gitlab = GitlabAPI.connect(_hostUrl, _apiToken, tokenType, authMethod);

  public static void main(String[] args) {
    // TODO Auto-generated method stub

    for (GitlabUser user : users) {
      System.out.println(c.getPrivate_token(user));
    }
  }

}
