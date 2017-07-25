package unzip;

import java.io.IOException;

import org.junit.Test;

import fcu.selab.progedu.exception.LoadConfigFailureException;
import fcu.selab.progedu.utils.ZipHandler;

public class ZipHandlerTest {

  @Test
  public void unzipTest() {
    try {
      ZipHandler zip = new ZipHandler();
      zip.unzip("C:\\Users\\GJen\\Desktop\\test\\MvnQuickStart.zip", "MvnQuickStart.zip", "zipTest");
    } catch (LoadConfigFailureException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
