import fcu.selab.progedu.db.ProjectDbManager;

public class TestSendEmail {

  static ProjectDbManager projectDb = ProjectDbManager.getInstance();
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    projectDb.sendEmail("kira070725@gmail.com");
  }

}
