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
import org.alfresco.rad.test.AlfrescoTestRunner;
import org.alfresco.rad.test.Remote;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.redpill.alfresco.test.AbstractComponentIT;

@RunWith(value = AlfrescoTestRunner.class)
// Specifying the remote endpoint is not required, it
// will default to http://localhost:8080/alfresco if
// not provided. This shows the syntax but simply
// sets the value back to the default value.
@Remote(endpoint = "http://localhost:8765/alfresco")
public class RLAbstractComponentIT extends AbstractComponentIT {

  @Before
  public void setUp() {
   
  }
  
  @Test
  public void testGetCompanyHome() {
    DemoComponent demoComponent = (DemoComponent) getApplicationContext().getBean("org.redpill-linpro.alfresco.DemoComponent");
    NodeRef companyHome = demoComponent.getCompanyHome();
    assertNotNull(companyHome);
    String companyHomeName = (String) getServiceRegistry().getNodeService().getProperty(
            companyHome, ContentModel.PROP_NAME);
    assertNotNull(companyHomeName);
    assertEquals("Company Home", companyHomeName);
  }

  @Test
  public void testChildNodesCount() {
    DemoComponent demoComponent = (DemoComponent) getApplicationContext().getBean("org.redpill-linpro.alfresco.DemoComponent");
    NodeRef companyHome = demoComponent.getCompanyHome();
    int childNodeCount = demoComponent.childNodesCount(companyHome);
    assertNotNull(childNodeCount);
    // There are 7 folders by default under Company Home
    assertEquals(7, childNodeCount);
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
