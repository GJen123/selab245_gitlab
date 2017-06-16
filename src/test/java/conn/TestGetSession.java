package conn;

import org.gitlab.api.models.GitlabSession;
import org.json.JSONObject;
import org.junit.Test;

import fcu.selab.progedu.conn.Conn;

public class TestGetSession {

  @Test
  public void testSession() {
    Conn conn = Conn.getInstance();
    String username = "root";
    String password = "iecsfcu123456";

    GitlabSession session = conn.getSession(username, password);
    if (session == null) {
      System.out.println("session null");
    }
    JSONObject json = new JSONObject(session);
    System.out.println("session : " + session);
    System.out.println("json : " + json);
    // System.out.println("Token : " + session.getPrivateToken());
  }
}
