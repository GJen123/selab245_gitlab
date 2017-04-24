package fcu.selab.progedu.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fcu.selab.progedu.exception.LoadConfigFailureException;

public class CourseConfig {
  private static final String PROPERTY_FILE = "/config/course_config.properties";

  private static CourseConfig INSTANCE = new CourseConfig();

  public static CourseConfig getInstance() {
    return INSTANCE;
  }

  private Properties props;

  private CourseConfig() {
    InputStream is = this.getClass().getResourceAsStream(PROPERTY_FILE);
    try {
      props = new Properties();
      props.load(is);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get Course Name
   * 
   * @return courseName
   * @throws LoadConfigFailureException
   *           on properties call error
   */
  public String getCourseName() throws LoadConfigFailureException {
    if (props != null) {
      return props.getProperty("COURSE_NAME");
    }
    throw new LoadConfigFailureException("Unable to get config of COURSE connection string from file;" + PROPERTY_FILE);
  }

  /**
   * Get School Email
   * 
   * @return schoolEmail
   * @throws LoadConfigFailureException
   *           on properties call error
   */
  public String getSchoolEmail() throws LoadConfigFailureException {
    if (props != null) {
      return props.getProperty("COURSE_SCHOOL_EMAIL");
    }
    throw new LoadConfigFailureException("Unable to get config of COURSE connection string from file;" + PROPERTY_FILE);
  }
}
