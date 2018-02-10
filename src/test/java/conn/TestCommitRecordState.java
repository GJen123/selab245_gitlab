package conn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fcu.selab.progedu.db.CommitRecordDbManager;
import fcu.selab.progedu.db.CommitRecordStateDbManager;
import fcu.selab.progedu.db.ProjectDbManager;

public class TestCommitRecordState {

  static CommitRecordDbManager commitRecordDb = CommitRecordDbManager.getInstance();
  static CommitRecordStateDbManager commitRecordStateDb = CommitRecordStateDbManager.getInstance();
  static ProjectDbManager projectDb = ProjectDbManager.getInstance();

  public static void main(String[] args) {
    // TODO Auto-generated method stub

    List<String> lsNames = new ArrayList<String>();
    lsNames = projectDb.listAllProjectNames();

    for (String name : lsNames) {
      name = name.replaceAll("\"", "");
      name = name.replaceAll("HW", "");

      int hwnum = Integer.parseInt(name);

      Map<String, Integer> map = new HashMap<>();

      map = commitRecordDb.getCommitRecordStateCounts(hwnum);

      int blue = 0;
      int gray = 0;
      int green = 0;
      int orange = 0;
      int red = 0;

      if (map.containsKey("blue")) {
        blue = map.get("blue");
      }

      if (map.containsKey("gray")) {
        gray = map.get("gray");
      }

      if (map.containsKey("green")) {
        green = map.get("green");
      }

      if (map.containsKey("orange")) {
        orange = map.get("orange");
      }

      if (map.containsKey("red")) {
        red = map.get("red");
      }

      commitRecordStateDb.addCommitRecordState(hwnum, blue, orange, red, green, gray);

    }

  }

}
