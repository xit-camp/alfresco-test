/**
 * Copyright (C) 2017 Alfresco Software Limited.
 * <p/>
 * This file is part of the Alfresco SDK project.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.redpill.alfresco.test;


import org.alfresco.service.cmr.site.SiteInfo;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class RLAbstractComponentIT extends AbstractComponentIT {

    @Before
    public void setUp() {

    }


    @Test
    public void testBackwardCompatability() {
        SiteInfo createSite = createSite();
        deleteSite(createSite);
    }

    @Test
    public void testComponentsAreWired() {
        assertNotNull(authenticationComponent);
        assertNotNull(transactionService);
        assertNotNull(transactionHelper);
        assertNotNull(behaviourFilter);
        assertNotNull(repository);
        assertNotNull(siteService);
        assertNotNull(authenticationService);
        assertNotNull(personService);
        assertNotNull(nodeService);
        assertNotNull(fileFolderService);
        assertNotNull(namespaceService);
        assertNotNull(contentService);
        assertNotNull(workflowService);
        assertNotNull(authorityService);
        assertNotNull(permissionService);
        assertNotNull(properties);
        assertNotNull(ownableService);
        assertNotNull(policyComponent);
        assertNotNull(searchService);
        assertNotNull(dictionaryService);
    }
}
