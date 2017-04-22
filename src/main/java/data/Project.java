package data;

public class Project {

  private String name = "";

  private String description = "";

  private boolean hasTemplate = false;

  private String type = "";

  private String gitLabUrl = "";

  private String jenkinsUrl = "";

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
}
