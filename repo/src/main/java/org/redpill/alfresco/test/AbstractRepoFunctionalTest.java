package org.redpill.alfresco.test;

import static com.jayway.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public abstract class AbstractRepoFunctionalTest {

  private final static Logger LOG = Logger.getLogger(AbstractRepoFunctionalTest.class);

  public static final String DEFAULT_BASE_URI = "http://localhost:%s/alfresco/service";
  
  public static final String DEFAULT_HTTP_PORT = "8080";

  protected JSONObject getMetadata(String nodeRef) throws JSONException {
    RestAssured.requestContentType(ContentType.JSON);
    RestAssured.responseContentType(ContentType.JSON);

    Response response = given().baseUri(getBaseUri()).pathParam("nodeRef", nodeRef).expect().statusCode(200).when().get("/api/metadata?nodeRef={nodeRef}&shortQNames=true");

    if (LOG.isDebugEnabled()) {
      response.prettyPrint();
    }

    return new JSONObject(response.asString());
  }

  protected void updateDocument(String nodeRef, Map<String, String> properties) throws JSONException {
    RestAssured.requestContentType(ContentType.JSON);
    RestAssured.responseContentType(ContentType.JSON);

    JSONObject json = new JSONObject();
    json.put("properties", properties);

    String id = StringUtils.replace(nodeRef, "workspace://SpacesStore/", "");

    Response response = given()
        .baseUri(getBaseUri())
        .pathParam("store_type", "workspace")
        .pathParam("store_id", "SpacesStore")
        .pathParam("id", id)
        .request().body(json.toString())
        .expect().statusCode(200)
        .when().post("/api/metadata/node/{store_type}/{store_id}/{id}");
    
    if (LOG.isDebugEnabled()) {
      response.prettyPrint();
    }
  }

  protected JSONObject checkoutDocument(String nodeRef) throws JSONException {
    RestAssured.requestContentType(ContentType.JSON);
    RestAssured.responseContentType(ContentType.JSON);

    String storeType = "workspace";
    String storeId = "SpacesStore";
    String id = StringUtils.replace(nodeRef, "workspace://SpacesStore/", "");

    Response response = given().baseUri(getBaseUri()).pathParam("store_type", storeType).pathParam("store_id", storeId).pathParam("id", id).body("{}").expect().statusCode(200).when()
        .post("/slingshot/doclib/action/checkout/node/{store_type}/{store_id}/{id}");

    return new JSONObject(response.asString());
  }

  protected InputStream downloadDocument(String downloadUrl, String responseContentType) {
    RestAssured.responseContentType(responseContentType);

    Response response = given()
        .baseUri(getBaseUri())
        .expect()
        .statusCode(200)
        .when().get(downloadUrl);

    return response.getBody().asInputStream();
  }

  protected JSONObject cancelCheckoutDocument(String nodeRef) throws JSONException {
    RestAssured.requestContentType(ContentType.JSON);
    RestAssured.responseContentType(ContentType.JSON);

    String storeType = "workspace";
    String storeId = "SpacesStore";
    String id = StringUtils.replace(nodeRef, "workspace://SpacesStore/", "");

    Response response = given().baseUri(getBaseUri()).pathParam("store_type", storeType).pathParam("store_id", storeId).pathParam("id", id).body("{}").expect().statusCode(200).when()
        .post("/slingshot/doclib/action/cancel-checkout/node/{store_type}/{store_id}/{id}");

    return new JSONObject(response.asString());
  }

  protected String uploadDocument(String filename, String site) {
    return uploadDocument(filename, site, null);
  }

  protected String uploadDocument(String filename, String site, String contentType) {
    RestAssured.requestContentType("multipart/form-data");
    RestAssured.responseContentType(ContentType.JSON);

    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
    
    RequestSpecification request = given()
        .baseUri(getBaseUri())
        .multiPart("filedata", filename, inputStream)
        .formParam("filename", filename)
        .formParam("siteid", site)
        .formParam("containerid", "documentLibrary");
    
    if (StringUtils.isNotBlank(contentType)) {
      request.formParam("contenttype", contentType);
    }

    Response response = request
        .expect().statusCode(200)
        .when().post("/api/upload");
    
    if (LOG.isDebugEnabled()) {
      response.prettyPrint();
    }
    
    return response.path("nodeRef");
  }

  protected void createSite(String shortName) {
    RestAssured.requestContentType(ContentType.JSON);
    RestAssured.responseContentType(ContentType.JSON);

    String visibility = "PRIVATE";
    String title = "Demoplats";
    String description = "This is a description";
    String siteDashboard = "site-dashboard";

    given()
        .baseUri(getBaseUri())
        .body(
            "{\"visibility\":\"" + visibility + "\",\"title\":\"" + title + "\",\"shortName\":\"" + shortName + "\",\"description\":\"" + description + "\",\"sitePreset\":\"" + siteDashboard + "\"}")
        .expect().contentType(ContentType.JSON).and().statusCode(200).and().body("shortName", equalTo(shortName)).when().post("/api/sites");
  }

  protected JSONObject createUser(String username, String password, String firstname, String lastname, String email) throws JSONException {
    return createUser(username, password, firstname, lastname, email, null, null);
  }

  protected JSONObject createUser(String username, String password, String firstname, String lastname, String email, Map<String, String> properties, List<String> groups) throws JSONException {
    RestAssured.requestContentType(ContentType.JSON);
    RestAssured.responseContentType(ContentType.JSON);

    JSONObject json = new JSONObject();

    json.put("userName", username);
    json.put("password", password);
    json.put("firstName", firstname);
    json.put("lastName", lastname);
    json.put("email", email);

    if (properties != null) {
      for (String property : properties.keySet()) {
        json.put(property, properties.get(property));
      }
    }

    if (groups != null) {
      json.put("groups", new JSONArray(groups));
    }
    
    RequestSpecification request = given()
        .baseUri(getBaseUri())
        .and().body(json.toString())
        .and().contentType(ContentType.JSON.withCharset("UTF-8"));
    
    Response response = request
        .expect().contentType(ContentType.JSON)
        .and().statusCode(200)
        .when().post("/api/people");

    return new JSONObject(response.body().asString());
  }

  protected JSONObject deleteUser(String username) {
    RequestSpecification request = given()
      .baseUri(getBaseUri())
      .pathParam("username", username)
      .contentType(ContentType.JSON.withCharset("UTF-8"));
    
    Response response = request
      .expect().statusCode(200)
      .and().contentType(ContentType.JSON)
      .when().delete("/api/people/{username}");
    
    try {
      return new JSONObject(response.body().asString());
    } catch (JSONException ex) {
      throw new RuntimeException(ex);
    }
  }

  protected void addSiteMembership(String site, String username, String role) throws JSONException {
    JSONObject person = new JSONObject();
    person.put("userName", username);

    JSONObject json = new JSONObject();
    json.put("person", person);
    json.put("role", role);

    RequestSpecification request = given()
      .baseUri(getBaseUri())
      .and().pathParam("shortname", site)
      .and().body(json.toString())
      .and().contentType(ContentType.JSON.withCharset("UTF-8"));
    
    request
      .expect().contentType(ContentType.JSON)
      .and().statusCode(200)
      .when().post("/api/sites/{shortname}/memberships");
  }
  
  /**
   * Creates a root group in Alfresco.
   * 
   * @param shortName the short name of the group
   * @param displayName the display name of the group
   * @return 
   * @throws JSONException 
   */
  protected JSONObject createRootGroup(String shortName, String displayName) throws JSONException {
    JSONObject json = new JSONObject();
    json.put("displayName", displayName);

    RequestSpecification request = given()
        .baseUri(getBaseUri())
        .and().body(json.toString())
        .and().pathParam("shortName", shortName)
        .and().contentType(ContentType.JSON.withCharset("UTF-8"));
    
    Response response = request
        .expect().contentType(ContentType.JSON)
        .and().statusCode(201)
        .when().post("/api/rootgroups/{shortName}");

    return new JSONObject(response.body().asString());
  }
  
  protected JSONObject deleteGroup(String shortName) {
    RequestSpecification request = given()
        .baseUri(getBaseUri())
        .and().pathParam("shortName", shortName)
        .and().contentType(ContentType.JSON.withCharset("UTF-8"));
      
    Response response = request
      .expect().statusCode(200)
      .and().contentType(ContentType.JSON)
      .when().delete("/api/groups/{shortName}");
      
    try {
      return new JSONObject(response.body().asString());
    } catch (JSONException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  /**
   * Adds an authority to a group, can be either a user or a group.
   * 
   * @param shortName the short name for the group 
   * @param authorityName the user or group name
   * @return
   * @throws JSONException
   */
  protected JSONObject addAuthorityToGroup(String shortName, String authorityName) throws JSONException {
    RequestSpecification request = given()
        .baseUri(getBaseUri())
        .and().body(new JSONObject().toString())
        .and().pathParam("shortName", shortName)
        .and().pathParam("authorityName", authorityName)
        .and().contentType(ContentType.JSON.withCharset("UTF-8"));
    
    Response response = request
        .expect().contentType(ContentType.JSON)
        .and().statusCode(200)
        .when().post("/api/groups/{shortName}/children/{authorityName}");
    
    return new JSONObject(response.body().asString());
  }

  protected void deleteSite(String shortName) {
    RestAssured.requestContentType(ContentType.JSON);
    RestAssured.responseContentType(ContentType.JSON);

    Response response = given()
        .baseUri(getBaseUri())
        .expect().statusCode(200)
        .when().delete("/api/sites/" + shortName);

    response.prettyPrint();
  }

  protected String getDocumentLibraryNodeRef(String site) {
    RestAssured.requestContentType(ContentType.JSON);
    RestAssured.responseContentType(ContentType.JSON);

    Response response = given().pathParameter("shortName", site).pathParam("container", "documentLibrary").baseUri(getBaseUri()).expect().statusCode(200).when()
        .get("/slingshot/doclib/container/{shortName}/{container}");

    return response.path("container.nodeRef");
  }
  
  protected JSONObject search(String term, String tag, String site, String container, String sort, String query, String repo) throws JSONException {
    RequestSpecification request = given()
        .baseUri(getBaseUri());
    
    String queryString = "";
    
    if (StringUtils.isNotBlank(term)) {
      request.pathParam("term", term);
      
      queryString += "?term={term}";
    }

    if (StringUtils.isNotBlank(tag)) {
      request.pathParam("tag", tag);
      
      if (StringUtils.isBlank(queryString)) {
        queryString = "?";
      } else {
        queryString = queryString + "&";
      }
      
      queryString = queryString + "tag={tag}";
    }

    if (StringUtils.isNotBlank(site)) {
      request.pathParam("site", site);
      
      if (StringUtils.isBlank(queryString)) {
        queryString = "?";
      } else {
        queryString = queryString + "&";
      }
      
      queryString = queryString + "site={site}";
    }

    if (StringUtils.isNotBlank(container)) {
      request.pathParam("container", container);
      
      if (StringUtils.isBlank(queryString)) {
        queryString = "?";
      } else {
        queryString = queryString + "&";
      }
      
      queryString = queryString + "container={container}";
    }

    if (StringUtils.isNotBlank(sort)) {
      request.pathParam("sort", sort);
      
      if (StringUtils.isBlank(queryString)) {
        queryString = "?";
      } else {
        queryString = queryString + "&";
      }
      
      queryString = queryString + "sort={sort}";
    }

    if (StringUtils.isNotBlank(query)) {
      request.pathParam("query", query);
      
      if (StringUtils.isBlank(queryString)) {
        queryString = "?";
      } else {
        queryString = queryString + "&";
      }
      
      queryString = queryString + "query={query}";
    }

    if (StringUtils.isNotBlank(repo)) {
      request.pathParam("repo", repo);
      
      if (StringUtils.isBlank(queryString)) {
        queryString = "?";
      } else {
        queryString = queryString + "&";
      }
      
      queryString = queryString + "repo={repo}";
    }
    
    System.out.println(queryString);

    Response response = request
        .expect().statusCode(200)
        .when().get("/slingshot/search" + queryString);

    if (LOG.isDebugEnabled()) {
      response.prettyPrint();
    }
    
    return new JSONObject(response.asString());
  }

  public void assertHasAspect(String nodeRef, String aspect) throws JSONException {
    JSONObject metadata = getMetadata(nodeRef);

    assertHasAspect(metadata, aspect);
  }

  public static void assertHasAspect(JSONObject metadata, String expectedAspect) throws JSONException {
    JSONArray aspects = metadata.getJSONArray("aspects");

    for (int x = 0; x < aspects.length(); x++) {
      String aspect = aspects.getString(x);

      if (aspect.equals(expectedAspect)) {
        return;
      }
    }

    fail("Aspect '" + expectedAspect + "' not found.");
  }

  public void assertDocumentExist(String nodeRef) throws JSONException {
    JSONObject metadata = getMetadata(nodeRef);

    assertDocumentExist(metadata);
  }

  public static void assertDocumentExist(JSONObject metadata) throws JSONException {
    assertTrue(metadata.length() > 0);
  }

  public void assertDocumentNotExist(String nodeRef) throws JSONException {
    JSONObject metadata = getMetadata(nodeRef);

    assertDocumentNotExist(metadata);
  }

  public static void assertDocumentNotExist(JSONObject metadata) throws JSONException {
    assertEquals(0, metadata.length());
  }

  protected String getBaseUri() {
    String port = System.getProperty("cargo.servlet.port", DEFAULT_HTTP_PORT);
    
    return String.format(DEFAULT_BASE_URI, port);
  }


}

