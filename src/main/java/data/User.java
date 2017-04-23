package data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {

  private String id; // 嚙褒賂蕭

  private int gitLabId;

  private String userName; // 嚙緯嚙皚嚙踝蕭(嚙稷嚙踝蕭) 嚙褒賂蕭

  private String name; // 嚙踝蕭嚙踝蕭嚙踝蕭嚙�(嚙踝蕭嚙踝蕭)

  private String email; // 嚙褒賂蕭@fcu.edu.tw

  private String password; // 嚙褒賂蕭

  private String privateToken;

  // private String password;
  //
  // private String email;
  //
  // private String phone;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  // public String getPhone() {
  // return phone;
  // }
  //
  // public void setPhone(String phone) {
  // this.phone = phone;
  // }

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

  // public String getPassword() {
  // return password;
  // }
  //
  // public void setPassword(String password) {
  // this.password = password;
  // }
  //
  // public String getEmail() {
  // return email;
  // }
  //
  // public void setEmail(String email) {
  // this.email = email;
  // }

}
