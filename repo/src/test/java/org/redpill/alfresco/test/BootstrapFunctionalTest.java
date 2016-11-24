package org.redpill.alfresco.test;

import org.alfresco.service.cmr.repository.NodeRef;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Marcus Svartmark - Redpill Linpro AB
 */
public class BootstrapFunctionalTest extends AbstractRepoFunctionalTest {

  public static final String SITE_SHORTNAME = "test_" + System.currentTimeMillis();

  @Test
  public void testSite() {
    createSite(SITE_SHORTNAME);
    String documentLibraryNodeRef = getDocumentLibraryNodeRef(SITE_SHORTNAME);
    assertNotNull(documentLibraryNodeRef);
    assertTrue(NodeRef.isNodeRef(documentLibraryNodeRef));
    deleteSite(SITE_SHORTNAME);
  }
}
