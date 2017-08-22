package conn;

import org.junit.Test;

import fcu.selab.progedu.conn.Conn;

public class TestDeleteUser {
  Conn conn = Conn.getInstance();

  @Test
  public void testDeleteUser() {
    for (int i = 70; i < 91; i++) {
      conn.deleteUser(i);
    }
  }
}
