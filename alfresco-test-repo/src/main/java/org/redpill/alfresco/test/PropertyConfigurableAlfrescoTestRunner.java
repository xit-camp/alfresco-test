package org.redpill.alfresco.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.alfresco.rad.test.AlfrescoTestRunner;
import org.alfresco.rad.test.Remote;
import org.apache.commons.io.IOUtils;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

/**
 *
 * @author mars
 */
public class PropertyConfigurableAlfrescoTestRunner extends AlfrescoTestRunner {

  public PropertyConfigurableAlfrescoTestRunner(Class<?> klass) throws InitializationError {
    super(klass);
  }

  /**
   * Check the @Remote config on the test class to see where the Alfresco Repo
   * is running
   *
   * @param method
   * @return
   */
  protected String getContextRoot(FrameworkMethod method) {
    Class<?> declaringClass = method.getMethod().getDeclaringClass();
    boolean annotationPresent = declaringClass.isAnnotationPresent(Remote.class);
    if (annotationPresent) {
      Remote annotation = declaringClass.getAnnotation(Remote.class);
      return annotation.endpoint();
    }

    //Fall back to property detection of context
    final Properties properties = new Properties();
    InputStream stream = null;
    try {
      stream = this.getClass().getResourceAsStream("/rl-testingproperties.properties");
      properties.load(stream);

    } catch (IOException ex) {

    } finally {
      IOUtils.closeQuietly(stream);
    }
    String port = properties.getProperty("test.alfresco.tomcat.port", "8080");

    return "http://localhost:" + port + "/alfresco";
  }
}
