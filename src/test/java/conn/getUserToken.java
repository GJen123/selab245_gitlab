package conn;

import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import java.util.ArrayList;
import java.util.List;
import org.gitlab.api.models.*;

import fcu.selab.progedu.conn.Conn;
import fcu.selab.progedu.data.GitlabData;

public class getUserToken {
	
GitlabData gitData = new GitlabData();
	
	static Conn c = Conn.getInstance();
	
	private static List<GitlabUser> users = c.getUsers();
	
	private String _hostUrl = gitData.getHostUrl();
	private String _apiToken = gitData.getApiToken();
	private TokenType tokenType = TokenType.PRIVATE_TOKEN;
	private AuthMethod authMethod = AuthMethod.URL_PARAMETER;

	private GitlabAPI gitlab = GitlabAPI.connect(_hostUrl, _apiToken, tokenType, authMethod);
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		for(GitlabUser user : users){
			System.out.println(c.getPrivate_token(user));
		}
	}

}
