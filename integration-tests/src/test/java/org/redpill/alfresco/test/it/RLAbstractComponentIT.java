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
package org.redpill.alfresco.test.it;

import org.redpill.alfresco.platformsample.*;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.redpill.alfresco.test.AbstractComponentIT;

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
    assertNotNull(_policyComponent);
    assertNotNull(_searchService);
    assertNotNull(_dictionaryService);
  }
}
