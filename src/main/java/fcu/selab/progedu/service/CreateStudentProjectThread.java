package fcu.selab.progedu.service;

import fcu.selab.progedu.conn.Conn;

public class CreateStudentProjectThread extends Thread {
  
  private int id;
  
  private String userName;
  
  private String email;
  
  private String projectName;
  
  private String rootProjectUrl;
  
  private ProjectService2 projectService = new ProjectService2();
  private Conn conn = Conn.getInstance();
  
  /**
   * constructor
   * 
   * @param id : user id
   * @param userName : user name
   * @param email : user email
   * @param projectName : project name
   * @param rootProjectUrl : root gitlab project url
   */
  public CreateStudentProjectThread(int id, String userName, 
      String email, String projectName, String rootProjectUrl) {
    this.id = id;
    this.userName = userName;
    this.email = email;
    this.projectName = projectName;
    this.rootProjectUrl = rootProjectUrl;
  }
  
  /**
   *  thread run function
   */
  public void run() {
    conn.createPrivateProject(id, projectName, rootProjectUrl);
    System.out.println(userName + ", Create student project, and import project");
    
    projectService.sendEmail(email, projectName);
    System.out.println(userName + ", Send notification email to student");
  }

}
