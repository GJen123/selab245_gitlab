package fcu.selab.progedu.conn;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import fcu.selab.progedu.config.GitlabConfig;
import fcu.selab.progedu.db.UserDbManager;

public class HttpConnect {

  private static HttpConnect httpConn = new HttpConnect();

  public static HttpConnect getInstance() {
    return httpConn;
  }

  GitlabConfig gitData = GitlabConfig.getInstance();

  UserDbManager userDb = UserDbManager.getInstance();

  /**
   * Httppost Readme to gitlab when creating project
   * @param url            Project url
   * @param content        Readme content
   */
  public void httpPostReadme(String url, String content) {
    String filePath = "README.md";
    String branchName = "master";
    String encoding = "test";
    String commitMessage = "README";
    HttpClient client = new DefaultHttpClient();
    try {
      final HttpPost post = new HttpPost(url);
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add((NameValuePair) new BasicNameValuePair("file_path", filePath));
      params.add((NameValuePair) new BasicNameValuePair("branch_name", branchName));
      params.add((NameValuePair) new BasicNameValuePair("encoding", encoding));
      params.add((NameValuePair) new BasicNameValuePair("content", content));
      params.add((NameValuePair) new BasicNameValuePair("commit_message", commitMessage));

      UrlEncodedFormEntity ent = null;
      ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
      post.setEntity(ent);

      HttpResponse response = client.execute(post);
      HttpEntity resEntity = response.getEntity();

      if (resEntity != null) {
        System.out.println("HttpPost Readme Success");
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Httpput readme to gitlab when creating project if readme is exist
   * @param url            Project url
   * @param content        Readme content
   */
  public void httpPutReadme(String url, String content) {
    String filePath = "README.md";
    String branchName = "master";
    String encoding = "test";
    String commitMessage = "README";
    HttpClient client = new DefaultHttpClient();
    try {
      final HttpPut post = new HttpPut(url);
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add((NameValuePair) new BasicNameValuePair("file_path", filePath));
      params.add((NameValuePair) new BasicNameValuePair("branch_name", branchName));
      params.add((NameValuePair) new BasicNameValuePair("encoding", encoding));
      params.add((NameValuePair) new BasicNameValuePair("content", content));
      params.add((NameValuePair) new BasicNameValuePair("commit_message", commitMessage));

      UrlEncodedFormEntity ent = null;
      ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
      post.setEntity(ent);

      HttpResponse response = client.execute(post);
      HttpEntity resEntity = response.getEntity();

      if (resEntity != null) {
        System.out.println("HttpPut Readme Success");
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Httppost file to gitlab when creating project
   * @param filePath           File path
   * @param url                 Project url
   * @param fileContent         File content
   */
  public void httpPostFile(String filePath, String url, String fileContent) {
    // String file_path = "src";
    String branchName = "master";
    String encoding = "text";
    String commitMessage = "HW";
    HttpClient client = new DefaultHttpClient();
    try {
      final HttpPost post = new HttpPost(url);
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add((NameValuePair) new BasicNameValuePair("file_path", filePath));
      params.add((NameValuePair) new BasicNameValuePair("branch_name", branchName));
      params.add((NameValuePair) new BasicNameValuePair("encoding", encoding));
      params.add((NameValuePair) new BasicNameValuePair("content", fileContent));
      params.add((NameValuePair) new BasicNameValuePair("commit_message", commitMessage));

      UrlEncodedFormEntity ent = null;
      ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
      post.setEntity(ent);

      HttpResponse response = client.execute(post);
      HttpEntity resEntity = response.getEntity();

      if (resEntity != null) {
        String result = resEntity.toString();
        System.out.println("HttpPost File Success : " + result);
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
