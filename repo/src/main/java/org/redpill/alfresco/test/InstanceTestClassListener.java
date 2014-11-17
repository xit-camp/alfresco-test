package org.redpill.alfresco.test;

public interface InstanceTestClassListener {

  /**
   * Acts as the static @BeforeClass. Any field that are set in this method must
   * be declared as static for results to persist. The method is run once per class.
   */
  void beforeClassSetup();

  /**
   * Acts as the static @AfterClass. Any field that are set in this method must
   * be declared as static for results to persist. The method is run once per class.
   */
  void afterClassSetup();

}
