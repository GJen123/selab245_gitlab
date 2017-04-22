package conn;

public class Language {

  private static final String Chinese = "zh";
  private static final String English = "en";

  private static final String form_tw = "form_tw";
  private static final String form_en = "form_en";

  public String getBaseName(String language) {
    String basename = null;
    if (language.equals(Chinese)) {
      basename = form_tw;
    } else if (language.equals(English)) {
      basename = form_en;
    }
    return basename;
  }
}