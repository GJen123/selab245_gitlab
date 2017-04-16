package data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {

	private String ID; //學號
	
	private int gitLabId;

	private String userName; //登入用(英文) 學號
	
	private String name; //網頁顯示(中文)
	
	private String email; //學號@fcu.edu.tw
	
	private String password; //學號
	
	private String privateToken;

//	private String password;
//
//	private String email;
//
//	private String phone;

	public String getID() {
		return ID;
	}

	public void setID(String ID) {
		this.ID = ID;
	}

//	public String getPhone() {
//		return phone;
//	}
//
//	public void setPhone(String phone) {
//		this.phone = phone;
//	}

	public int getGitLabId() {
		return gitLabId;
	}

	public void setGitLabId(int gitLabId) {
		this.gitLabId = gitLabId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPrivateToken() {
		return privateToken;
	}

	public void setPrivateToken(String privateToken) {
		this.privateToken = privateToken;
	}

//	public String getPassword() {
//		return password;
//	}
//
//	public void setPassword(String password) {
//		this.password = password;
//	}
//
//	public String getEmail() {
//		return email;
//	}
//
//	public void setEmail(String email) {
//		this.email = email;
//	}

}
