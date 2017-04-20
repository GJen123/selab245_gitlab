package conn;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.gitlab.api.AuthMethod;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabCommit;
import org.gitlab.api.models.GitlabProject;
import org.gitlab.api.models.GitlabUser;

import data.GitlabData;

public class StudentConn{
	
	GitlabData gitData = new GitlabData();
	
	String private_token;
	private GitlabUser user = new GitlabUser();
	private String _hostUrl = gitData.getHostUrl();
	private String _apiToken = gitData.getApiToken();
	private TokenType tokenType = TokenType.PRIVATE_TOKEN;
	private AuthMethod authMethod = AuthMethod.URL_PARAMETER;
	private GitlabAPI gitlab;
	private List<GitlabProject> project = new ArrayList<GitlabProject>();
	
	public StudentConn (String private_token){
		this.private_token = private_token;
		gitlab = GitlabAPI.connect(_hostUrl, private_token, tokenType, authMethod);
		getUser();
	}
	
	public void getUser(){
		try {
			user = gitlab.getUser();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getUsername(){
		return user.getUsername();
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
	
	public int getAllCommitsCounts(int projectId){
		int count = 0;
		List<GitlabCommit> lsCommits = new ArrayList<GitlabCommit>();
		try {
			if(!gitlab.getAllCommits(projectId).isEmpty()){
				lsCommits = gitlab.getAllCommits(projectId);
				count = lsCommits.size();
			}else{
				count = 0;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public List<GitlabCommit> getAllCommits(int projectId){
		List<GitlabCommit> lsCommits = new ArrayList<GitlabCommit>();
		try {
			lsCommits = gitlab.getAllCommits(projectId);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return lsCommits;
	}
}