/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.redpill.alfresco.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.rad.test.AbstractAlfrescoIT;
import org.alfresco.rad.test.Remote;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.OwnableService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.PropertyMap;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;

@RunWith(value = PropertyConfigurableAlfrescoTestRunner.class)
public abstract class AbstractComponentIT extends AbstractAlfrescoIT {

  private static final Logger LOG = Logger.getLogger(AbstractComponentIT.class);

  private final static String NAMESPACE_BEGIN = "" + QName.NAMESPACE_BEGIN;
  private boolean _requiresNew = true;

  protected AuthenticationComponent _authenticationComponent;
  protected TransactionService _transactionService;

  protected static RetryingTransactionHelper _transactionHelper;
  protected BehaviourFilter _behaviourFilter;
  protected Repository _repository;
  protected SiteService _siteService;
  protected MutableAuthenticationService _authenticationService;
  protected PersonService _personService;
  protected NodeService _nodeService;
  protected FileFolderService _fileFolderService;
  protected NamespaceService _namespaceService;
  protected ContentService _contentService;
  protected WorkflowService _workflowService;
  protected AuthorityService _authorityService;

  protected PermissionService _permissionService;
  protected Properties _properties;

  protected OwnableService _ownableService;
  protected SearchService _searchService;
  protected DictionaryService _dictionaryService;
  protected PolicyComponent _policyComponent;

  @Before
  public void setUpAbstract() {
    _nodeService = getServiceRegistry().getNodeService();
    _searchService = getServiceRegistry().getSearchService();
    _dictionaryService = getServiceRegistry().getDictionaryService();
    _ownableService = getServiceRegistry().getOwnableService();
    _policyComponent = getServiceRegistry().getPolicyComponent();
    _permissionService = getServiceRegistry().getPermissionService();
    _authorityService = getServiceRegistry().getAuthorityService();
    _workflowService = getServiceRegistry().getWorkflowService();
    _contentService = getServiceRegistry().getContentService();
    _namespaceService = getServiceRegistry().getNamespaceService();
    _fileFolderService = getServiceRegistry().getFileFolderService();
    _personService = getServiceRegistry().getPersonService();
    _authenticationService = getServiceRegistry().getAuthenticationService();
    _siteService = getServiceRegistry().getSiteService();
    _transactionService = getServiceRegistry().getTransactionService();

    _transactionHelper = _transactionService.getRetryingTransactionHelper();

    ApplicationContext ctx = getApplicationContext();
    _properties = (Properties) ctx.getBean("global-properties");
    _repository = (Repository) ctx.getBean("repositoryHelper");
    _behaviourFilter = (BehaviourFilter) ctx.getBean("policyBehaviourFilter");
    _authenticationComponent = (AuthenticationComponent) ctx.getBean("authenticationComponent");
  }

  /**
   * Creates a user and a related person in the repository.
   *
   * @param userId
   * @return
   */
  protected NodeRef createUser(String userId) {
    return createUser(userId, null);
  }

  /**
   * Creates a user and a related person in the repository.
   *
   * @param userId
   * @param callback
   * @return
   */
  protected NodeRef createUser(final String userId, final CreateUserCallback callback) {
    return AuthenticationUtil.runAsSystem(new RunAsWork<NodeRef>() {

      @Override
      public NodeRef doWork() throws Exception {
        if (!_authenticationService.authenticationExists(userId)) {
          _authenticationService.createAuthentication(userId, "password".toCharArray());
          PropertyMap properties = new PropertyMap(3);
          properties.put(ContentModel.PROP_USERNAME, userId);
          properties.put(ContentModel.PROP_FIRSTNAME, userId);
          properties.put(ContentModel.PROP_LASTNAME, "Test");
          properties.put(ContentModel.PROP_EMAIL, _properties.getProperty("mail.to.default"));

          NodeRef user = _personService.createPerson(properties);

          if (callback != null) {
            callback.onCreateUser(user);
          }

          return user;
        } else {
          fail("User exists: " + userId);
          return null;
        }
      }

    });
  }

  protected void deleteUser(final NodeRef personRef) {
    AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {

      @Override
      public Void doWork() throws Exception {
        _personService.deletePerson(personRef);

        return null;
      }

    });
  }

  protected void deleteUser(final String userName) {
    AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {

      @Override
      public Void doWork() throws Exception {
        _personService.deletePerson(userName);

        return null;
      }
    });
  }

  /**
   * Creates a site and makes sure that a document library and a data list
   * container exist.
   *
   * @param preset
   * @param visibility
   * @param siteType
   * @return
   */
  protected SiteInfo createSite(final String preset, final String siteName, final SiteVisibility visibility, final QName siteType, final CreateSiteCallback callback) {
    return _transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<SiteInfo>() {

      @Override
      public SiteInfo execute() throws Throwable {
        // Create site
        String name = null;

        if (siteName == null) {
          name = "it-" + System.currentTimeMillis();
        } else {
          name = siteName;
        }

        SiteInfo site = _siteService.createSite(preset, name, name, name, visibility, siteType);

        if (callback != null) {
          callback.onCreateSite(site);
        }

        assertNotNull(site);

        _nodeService.addAspect(site.getNodeRef(), ContentModel.ASPECT_TEMPORARY, null);

        // Create document library container
        NodeRef documentLibrary = _siteService.getContainer(name, SiteService.DOCUMENT_LIBRARY);

        if (documentLibrary == null) {
          documentLibrary = _siteService.createContainer(name, SiteService.DOCUMENT_LIBRARY, ContentModel.TYPE_FOLDER, null);
        }

        assertNotNull(documentLibrary);

        return site;
      }

    }, false, _requiresNew);
  }

  protected SiteInfo createSite(final String preset) {
    return _transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<SiteInfo>() {

      @Override
      public SiteInfo execute() throws Throwable {
        return createSite(preset, null, SiteVisibility.PRIVATE, SiteModel.TYPE_SITE, null);
      }

    }, false, _requiresNew);
  }

  protected SiteInfo createSite() {
    return createSite("site-dashboard");
  }

  protected void deleteSite(SiteInfo siteInfo) {
    deleteSite(siteInfo, null);
  }

  protected void deleteSite(final SiteInfo siteInfo, final BeforeDeleteSiteCallback callback) {
    _transactionHelper.doInTransaction(new RetryingTransactionHelper.RetryingTransactionCallback<Void>() {

      @Override
      public Void execute() throws Throwable {
        String fullyAuthenticatedUser = AuthenticationUtil.getFullyAuthenticatedUser();
        AuthenticationUtil.setAdminUserAsFullyAuthenticatedUser();

        if (callback != null) {
          callback.beforeDeleteSite(siteInfo);
        }

        deleteLingeringSiteGroups(siteInfo);
        _siteService.deleteSite(siteInfo.getShortName());

        System.out.println("deleted site with shortName: " + siteInfo.getShortName());
        AuthenticationUtil.setFullyAuthenticatedUser(fullyAuthenticatedUser);
        return null;
      }

    }, false, _requiresNew);

  }

  protected void deleteLingeringSiteGroups(SiteInfo siteInfo) {
    final NodeRef nodeRef = siteInfo.getNodeRef();
    final QName siteType = _nodeService.getType(nodeRef);
    final String shortName = siteInfo.getShortName();

    // Delete the associated groups
    AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
      public Void doWork() throws Exception {
        // Delete the master site group
        final String siteGroup = _siteService.getSiteGroup(shortName);
        if (_authorityService.authorityExists(siteGroup)) {
          _authorityService.deleteAuthority(siteGroup, false);

          // Iterate over the role related groups and delete then
          Set<String> permissions = _permissionService.getSettablePermissions(siteType);
          for (String permission : permissions) {
            String siteRoleGroup = _siteService.getSiteRoleGroup(shortName, permission);

            // Delete the site role group
            _authorityService.deleteAuthority(siteRoleGroup);
          }
        }

        return null;
      }
    }, AuthenticationUtil.getSystemUserName());

  }

  protected FileInfo uploadDocument(SiteInfo site, String filename) {
    return uploadDocument(site, filename, null);
  }

  protected FileInfo uploadDocument(SiteInfo site, String filename, InputStream inputStream) {
    return uploadDocument(site, filename, inputStream, null);
  }

  protected FileInfo uploadDocument(SiteInfo site, String filename, InputStream inputStream, List<String> folders) {
    return uploadDocument(site, filename, inputStream, folders, null);
  }

  protected FileInfo uploadDocument(SiteInfo site, String filename, InputStream inputStream, List<String> folders, String name) {
    return uploadDocument(site, filename, inputStream, folders, name, null);
  }

  protected FileInfo uploadDocument(SiteInfo site, String filename, InputStream inputStream, List<String> folders, String name, NodeRef parentNodeRef) {
    return uploadDocument(site, filename, inputStream, folders, name, parentNodeRef, null);
  }

  protected FileInfo uploadDocument(SiteInfo site, String filename, InputStream inputStream, List<String> folders, String name, NodeRef parentNodeRef, String type) {
    return uploadDocument(site, filename, inputStream, folders, name, parentNodeRef, type, null);
  }

  protected FileInfo uploadDocument(final SiteInfo site, final String filename, final InputStream inputStream, final List<String> folders, final String name, final NodeRef parentNodeRef,
          final String type, final Map<QName, Serializable> properties) {
    return _transactionHelper.doInTransaction(new RetryingTransactionCallback<FileInfo>() {

      @Override
      public FileInfo execute() throws Throwable {
        LOG.trace("Uploading " + filename);
        String finalName = StringUtils.isNotEmpty(name) ? name : FilenameUtils.getName(filename);

        // gets the folder to create the document in, if it's a site then it's
        // the documentLibrary, if not then it's the user home
        NodeRef documentLibrary = site != null ? _siteService.getContainer(site.getShortName(), SiteService.DOCUMENT_LIBRARY) : _repository.getUserHome(_repository.getPerson());

        NodeRef finalParentNodeRef = parentNodeRef != null ? parentNodeRef : documentLibrary;

        if (folders != null) {
          for (String folder : folders) {
            FileInfo folderInfo = _fileFolderService.create(finalParentNodeRef, folder, ContentModel.TYPE_FOLDER);
            finalParentNodeRef = folderInfo.getNodeRef();
          }
        }

        QName nodeTypeQName = type == null ? ContentModel.TYPE_CONTENT : createQName(type);

        QName assocQName = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(finalName));

        Map<QName, Serializable> creationProperites = properties != null ? properties : new HashMap<QName, Serializable>();

        if (!creationProperites.containsKey(ContentModel.PROP_NAME)) {
          creationProperites.put(ContentModel.PROP_NAME, finalName);
        }

        LOG.trace("Node type: " + nodeTypeQName);

        // creates the document
        final NodeRef document = _nodeService.createNode(finalParentNodeRef, ContentModel.ASSOC_CONTAINS, assocQName, nodeTypeQName, creationProperites).getChildRef();
        assertNotNull("Created node is null!", document);
        // set the correct owner on the document
        AuthenticationUtil.runAsSystem(new RunAsWork<Void>() {

          @Override
          public Void doWork() throws Exception {
            String username = AuthenticationUtil.getFullyAuthenticatedUser();

            LOG.debug("Setting owner of '" + document + "' to '" + username + "'");

            _ownableService.setOwner(document, username);

            return null;
          }

        });

        FileInfo fileInfo = _fileFolderService.getFileInfo(document);

        ContentWriter writer = _contentService.getWriter(document, ContentModel.PROP_CONTENT, true);

        writer.guessEncoding();

        writer.guessMimetype(filename);

        InputStream content = inputStream != null ? inputStream : Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);

        try {
          if (content != null && content.available() > 0) {
            writer.putContent(content);
          } else {
            writer.putContent("");
          }
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        } finally {
          IOUtils.closeQuietly(content);
        }
        LOG.trace("Upload finished ");
        return fileInfo;
      }
    }, false, _requiresNew);
  }

  protected QName createQName(String s) {
    QName qname;
    if (s.indexOf(NAMESPACE_BEGIN) != -1) {
      qname = QName.createQName(s);
    } else {
      qname = QName.createQName(s, _namespaceService);
    }
    return qname;
  }

  public <R> R runInSite(RunInSite<R> runInSite) {
    SiteInfo site = createSite();

    try {
      return runInSite.doInSite(site);
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } finally {
      deleteSite(site);
    }
  }

  public interface RunInSite<Result> {

    Result doInSite(SiteInfo site) throws Exception;

  }

  public void setRequiresNew(boolean requiresNew) {
    _requiresNew = requiresNew;
  }

  public boolean isRequiresNew() {
    return _requiresNew;
  }

  public interface CreateUserCallback {

    void onCreateUser(NodeRef user);

  }

  public interface BeforeDeleteSiteCallback {

    void beforeDeleteSite(SiteInfo siteInfo);

  }

  public interface CreateSiteCallback {

    void onCreateSite(SiteInfo site);

  }

  public void assertType(String message, NodeRef node, QName type) {
    if (!_nodeService.getType(node).isMatch(type)) {
      fail(message);
    }
  }

  public void assertType(NodeRef node, QName type) {
    assertType(null, node, type);
  }

  public void assertHasAspect(String message, NodeRef node, QName aspect) {
    if (!_nodeService.hasAspect(node, aspect)) {
      fail(message);
    }
  }

  public void assertHasAspect(NodeRef node, QName aspect) {
    assertHasAspect(null, node, aspect);
  }

  public void setEndpointValue(String endpoint) {
    final Remote classAnnotation;
    classAnnotation = getClass().getAnnotation(Remote.class);
    System.out.println("old Remote endpoint = " + classAnnotation.endpoint());
    changeAnnotationValue(classAnnotation, "endpoint", endpoint);
    System.out.println("modified Remote endpoint  = " + classAnnotation.endpoint());
  }

  @SuppressWarnings("unchecked")
  public static Object changeAnnotationValue(Annotation annotation, String key, Object newValue) {
    Object handler = Proxy.getInvocationHandler(annotation);
    Field f;
    try {
      f = handler.getClass().getDeclaredField("memberValues");
    } catch (NoSuchFieldException | SecurityException e) {
      throw new IllegalStateException(e);
    }
    f.setAccessible(true);
    Map<String, Object> memberValues;
    try {
      memberValues = (Map<String, Object>) f.get(handler);
    } catch (IllegalArgumentException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
    Object oldValue = memberValues.get(key);
    if (oldValue == null || oldValue.getClass() != newValue.getClass()) {
      throw new IllegalArgumentException();
    }
    memberValues.put(key, newValue);
    return oldValue;
  }
}
