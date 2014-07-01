package org.redpill.alfresco.test;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class SpringInstanceTestClassRunner extends SpringJUnit4ClassRunner {

  private InstanceTestClassListener _instanceSetupListener;

  public SpringInstanceTestClassRunner(Class<?> clazz) throws InitializationError {
    super(clazz);
  }

  @Override
  protected Object createTest() throws Exception {
    Object test = super.createTest();

    // Note that JUnit4 will call this createTest() multiple times for each
    // test method, so we need to ensure to call "beforeClassSetup" only once.
    if (test instanceof InstanceTestClassListener && _instanceSetupListener == null) {
      _instanceSetupListener = (InstanceTestClassListener) test;
      _instanceSetupListener.beforeClassSetup();
    }

    return test;
  }

  @Override
  public void run(RunNotifier notifier) {
    super.run(notifier);

    if (_instanceSetupListener != null) {
      _instanceSetupListener.afterClassSetup();
    }
  }
  
}
