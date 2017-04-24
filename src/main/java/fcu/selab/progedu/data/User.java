package fcu.selab.progedu.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable {

  private String id; // ??™è?’è?‚è•­

  private int gitLabId;

  private String userName; // ??™ç·¯??™ç?šå?™è?è•­(??™ç¨·??™è?è•­) ??™è?’è?‚è•­

  private String name; // ??™è?è•­??™è?è•­??™è?è•­??™ï¿½(??™è?è•­??™è?è•­)

  private String email; // ??™è?’è?‚è•­@fcu.edu.tw

  private String password; // ??™è?’è?‚è•­

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
