package conn;

import java.sql.Connection;

import fcu.selab.progedu.db.CommitResultDbManager;
import fcu.selab.progedu.db.IDatabase;
import fcu.selab.progedu.db.MySqlDatabase;

public class TestGetCommitSituation {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    IDatabase database = new MySqlDatabase();
    Connection connection = database.getConnection();
    CommitResultDbManager db = CommitResultDbManager.getInstance();
    // int[] results = db.getCounts(connection, "orange");
    //
    // for (int i : results) {
    // System.out.println(i);
    // }
  }

}
