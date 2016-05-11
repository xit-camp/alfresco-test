package org.redpill.alfresco.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class BootstrapIntegrationTest extends AbstractRepoIntegrationTest {

  @Test
  public void testComponentsAreWired() {
    assertNotNull(_authenticationComponent);
    assertNotNull(_transactionService);
    assertNotNull(_transactionHelper);
    assertNotNull(_behaviourFilter);
    assertNotNull(_repository);
    assertNotNull(_siteService);
    assertNotNull(_authenticationService);
    assertNotNull(_personService);
    assertNotNull(_nodeService);
    assertNotNull(_fileFolderService);
    assertNotNull(_namespaceService);
    assertNotNull(_contentService);
    assertNotNull(_workflowService);
    assertNotNull(_authorityService);
    assertNotNull(_permissionService);
    assertNotNull(_properties);
    assertNotNull(_ownableService);
  }
}
