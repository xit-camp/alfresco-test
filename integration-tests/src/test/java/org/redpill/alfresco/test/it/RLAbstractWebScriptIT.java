package org.redpill.alfresco.test.it;

import org.alfresco.service.cmr.repository.NodeRef;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import org.redpill.alfresco.test.AbstractWebScriptIT;

/**
 * A test of RL abstract web script functions
 */
public class RLAbstractWebScriptIT extends AbstractWebScriptIT {

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
