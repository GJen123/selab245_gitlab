package fcu.selab.progedu.data;

public class Project {

  private String name = "";

  private String deadline = "";

  private String description = "";

  private boolean hasTemplate = false;

  private String type = "";

  private String gitLabUrl = "";

  private String jenkinsUrl = "";

  private long testZipChecksum = -1;

  private String testZipUrl = "";

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDeadline() {
    return deadline;
  }

  public void setDeadline(String deadline) {
    this.deadline = deadline;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isHasTemplate() {
    return hasTemplate;
  }

  public void setHasTemplate(boolean hasTemplate) {
    this.hasTemplate = hasTemplate;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getGitLabUrl() {
    return gitLabUrl;
  }

  public void setGitLabUrl(String gitLabUrl) {
    this.gitLabUrl = gitLabUrl;
  }

  public String getJenkinsUrl() {
    return jenkinsUrl;
  }

  public void setJenkinsUrl(String jenkinsUrl) {
    this.jenkinsUrl = jenkinsUrl;
  }

  public long getTestZipChecksum() {
    return testZipChecksum;
  }

  public void setTestZipChecksum(long testZipChecksum) {
    this.testZipChecksum = testZipChecksum;
  }

  public String getTestZipUrl() {
    return testZipUrl;
  }

  public void setTestZipUrl(String testZipUrl) {
    this.testZipUrl = testZipUrl;
  }
}
