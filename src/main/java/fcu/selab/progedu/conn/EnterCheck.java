package fcu.selab.progedu.conn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.exception.LoadConfigFailureException;

public class EnterCheck {
  GitlabConfig gitData = GitlabConfig.getInstance();

  /**
   * Httppost to check if username and password are right
   * 
   * @param username             Gitlab user name
   * @param password             Gitalb user password
   * @return result
   * @throws LoadConfigFailureException on gitlab config call error
   */
  public String httpPost(String username, String password) throws LoadConfigFailureException {
    String result = null;
    String response = null;
    StringBuilder sb = new StringBuilder();
    String url = gitData.getGitlabHostUrl() + "/oauth/token";
    try {
      URL object = new URL(url);

      HttpURLConnection con = (HttpURLConnection) object.openConnection();
      con.setDoOutput(true);
      con.setDoInput(true);
      con.setRequestProperty("Content-Type", "application/json");
      con.setRequestProperty("Accept", "application/json");
      con.setRequestMethod("POST");

      JSONObject jsonObj = new JSONObject();

      jsonObj.put("grant_type", "password");
      jsonObj.put("username", username);
      jsonObj.put("password", password);

      OutputStreamWriter wr;

      wr = new OutputStreamWriter(con.getOutputStream());
      wr.write(jsonObj.toString());
      wr.flush();

      // display what returns the POST request

      int httpResult = con.getResponseCode();
      if (httpResult == HttpURLConnection.HTTP_OK) {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(con.getInputStream(), "utf-8"));

        while ((response = br.readLine()) != null) {
          sb.append(response + "\n");
          result = sb.toString();
        }
        br.close();
      } else {
        result = con.getResponseMessage();
        System.out.println(con.getResponseMessage());
      }
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return result;

  }

  /**
   * Analysis the recall JSON for accessToken
   * @param str    The string of JSON
   * @return accessToken
   */
  public String analysisJson(String str) {
    String accessToken = null;
    if (!str.equals("")) {
      JSONObject json = new JSONObject(str);
      if (json.has("access_token")) {
        accessToken = json.getString("access_token");
      } else {
        accessToken = null;
      }
    } else {
      System.out.println("str == null");
    }

    return accessToken;
  }

}