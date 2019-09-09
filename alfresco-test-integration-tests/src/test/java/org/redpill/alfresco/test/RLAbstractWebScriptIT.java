package org.redpill.alfresco.test;

import org.alfresco.service.cmr.repository.NodeRef;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
