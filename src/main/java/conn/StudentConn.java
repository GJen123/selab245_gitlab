package conn;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;

import data.GitlabData;

public class StudentConn{
	
	GitlabData data = new GitlabData();
	
	String private_token;
	private GitlabUser gitlabUser = new GitlabUser();
	private String _hostUrl = data.getHostUrl();
	private String _apiToken = data.getApiToken();
	private TokenType tokenType = TokenType.PRIVATE_TOKEN;
	private AuthMethod authMethod = AuthMethod.URL_PARAMETER;
	private GitlabAPI gitlab;
	private List<GitlabProject> project = new ArrayList<GitlabProject>();
	
	public StudentConn (String private_token){
		this.private_token = private_token;
		gitlab = GitlabAPI.connect(_hostUrl, private_token, tokenType, authMethod);
		
	}
	
	public GitlabUser getUser(){
		try {
			gitlabUser = gitlab.getUser();
		}catch(IOException e) {
			System.out.println(e);	
		}
		return gitlabUser;
	}
	
	public String getUserName(){
		return gitlabUser.getName();
	}
	
	public List<GitlabProject> getProject(){
		try {
			project = gitlab.getProjects();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return project;
	}
}