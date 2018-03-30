package conn;

import fcu.selab.progedu.data.User;
import fcu.selab.progedu.db.UserDbManager;
import fcu.selab.progedu.service.CommitResultService;
import org.junit.Test;

import fcu.selab.progedu.conn.Conn;

import java.util.List;

public class TestUpdatePassword {
  static CommitResultService service = new CommitResultService();
  static UserDbManager userDb = UserDbManager.getInstance();

  public static void main(String[] args) {
    List<User> users = userDb.listAllUsers();

    for(User user : users) {
      String userName = user.getUserName();
      System.out.println(userName);
      service.updateCommitResult(userName, "OOP-HW2");
      System.out.println("--------------------------------------------------\n");
    }

  }
}
